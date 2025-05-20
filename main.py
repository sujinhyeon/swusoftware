import os
import secrets
import requests
import firebase_admin
from firebase_admin import credentials, auth as firebase_auth
from fastapi import FastAPI, Request, Form, HTTPException
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates

# ── Firebase 서비스 계정 키 경로 (절대 경로) ──
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
KEY_PATH = os.path.join(BASE_DIR, "firebase-service-account.json")
cred = credentials.Certificate(KEY_PATH)
firebase_admin.initialize_app(cred)

# ── FastAPI & 템플릿 ──
app = FastAPI()
templates = Jinja2Templates(directory="templates")

# ── Firebase Web API Key ──
FIREBASE_WEB_API_KEY = "AIzaSyC_DtavGFueJHK_QexFs6jRx16PyxLVRaw"

# ── 네이버 OAuth 설정 ──
NAVER_CLIENT_ID     = "6SC5aZWdmK0Mrtg84_Vh"
NAVER_CLIENT_SECRET = "K1K6RC6ZYw"
# FastAPI 엔드포인트와 완전히 일치해야 함
NAVER_REDIRECT_URI = "http://localhost:8000/auth/naver/callback"

# 간단한 인메모리 state 저장소 (실서비스에선 Redis/DB 사용)
_state_store: set[str] = set()


@app.get("/", response_class=HTMLResponse)
def login_page(request: Request):
    return templates.TemplateResponse("login.html", {"request": request, "error": None})


@app.post("/login", response_class=HTMLResponse)
def login(request: Request, email: str = Form(...), password: str = Form(...)):
    url = (
        f"https://identitytoolkit.googleapis.com/v1/"
        f"accounts:signInWithPassword?key={FIREBASE_WEB_API_KEY}"
    )
    payload = {
        "email": email,
        "password": password,
        "returnSecureToken": True
    }
    res = requests.post(url, json=payload)

    if res.status_code == 200:
        user = res.json()
        return HTMLResponse(f"<h2>Welcome, {user['email']}!</h2>")
    else:
        return templates.TemplateResponse("login.html", {
            "request": request,
            "error": "Invalid email or password."
        })


# 2-1. 네이버 로그인 요청용 엔드포인트
@app.get("/auth/naver/login")
def naver_login():
    state = secrets.token_urlsafe(16)
    _state_store.add(state)
    authorize_url = (
        "https://nid.naver.com/oauth2.0/authorize"
        f"?response_type=code"
        f"&client_id={NAVER_CLIENT_ID}"
        f"&redirect_uri={NAVER_REDIRECT_URI}"
        f"&state={state}"
    )
    return RedirectResponse(authorize_url)


# 2-2 & 2-3. Callback 처리 → 토큰 교환 → Firebase 커스텀 토큰 생성
@app.get("/auth/naver/callback", response_class=HTMLResponse)
def naver_callback(request: Request):
    code  = request.query_params.get("code")
    state = request.query_params.get("state")

    if not code or not state:
        raise HTTPException(400, "code나 state가 누락되었습니다.")
    if state not in _state_store:
        raise HTTPException(400, "Invalid state.")
    _state_store.remove(state)

    # (1) 네이버 Access Token 교환
    token_res = requests.get(
        "https://nid.naver.com/oauth2.0/token",
        params={
            "grant_type":    "authorization_code",
            "client_id":     NAVER_CLIENT_ID,
            "client_secret": NAVER_CLIENT_SECRET,
            "code":          code,
            "state":         state,
        },
    )
    token_res.raise_for_status()
    access_token = token_res.json().get("access_token")

    # (2) 프로필 조회
    profile_res = requests.get(
        "https://openapi.naver.com/v1/nid/me",
        headers={"Authorization": f"Bearer {access_token}"}
    )
    profile_res.raise_for_status()
    profile = profile_res.json().get("response", {})
    # 4) 프로필에서 ID, 이메일, 닉네임을 꺼내 변수에 담기
    naver_id = profile.get("id")                     # ← 이 줄이 먼저!
    email    = profile.get("email", "")
    nickname = profile.get("nickname", "").strip()    # 빈 문자열일 수 있으니 .strip()

    # 5) Firebase 사용자 동기화 로직
    uid = f"naver:{naver_id}"
    user_args = {"uid": uid, "email": email}
    if nickname:                                    # nickname이 비어있지 않을 때만 넣기
        user_args["display_name"] = nickname

    try:
        # 이미 존재하면 업데이트
        firebase_auth.get_user(uid)
        firebase_auth.update_user(**user_args)
    except firebase_auth.UserNotFoundError:
        # 없으면 새로 생성
        firebase_auth.create_user(**user_args)
    # ─────────────────

    # (3) Firebase 커스텀 토큰 생성
    firebase_token = (
        firebase_auth
        .create_custom_token(f"naver:{naver_id}", {"provider":"naver","email":email})
        .decode("utf-8")
    )

    # (4) 클라이언트용 callback.html 렌더링
    return templates.TemplateResponse("callback.html", {
        "request": request,
        "firebase_token": firebase_token
    })


