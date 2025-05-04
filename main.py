import os
import requests
import firebase_admin
from firebase_admin import credentials
from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse
from fastapi.templating import Jinja2Templates

# Firebase 서비스 계정 키 경로 (절대 경로로 처리)
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
KEY_PATH = os.path.join(BASE_DIR, "firebase-service-account.json")

# Firebase 초기화
cred = credentials.Certificate(KEY_PATH)
firebase_admin.initialize_app(cred)

# FastAPI 앱
app = FastAPI()
templates = Jinja2Templates(directory="templates")

#Firebase Web API Key 넣기
FIREBASE_WEB_API_KEY = "AIzaSyC_DtavGFueJHK_QexFs6jRx16PyxLVRaw"

@app.get("/", response_class=HTMLResponse)
def login_page(request: Request):
    return templates.TemplateResponse("login.html", {"request": request, "error": None})

@app.post("/login", response_class=HTMLResponse)
def login(request: Request, email: str = Form(...), password: str = Form(...)):
    url = f"https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={FIREBASE_WEB_API_KEY}"
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
# # main.py
# from fastapi import FastAPI
# from pydantic import BaseModel
# from gpt import generate_family_question

# app = FastAPI()

# class QuestionRequest(BaseModel):
#     family_info: dict
#     previous_answers: str = ""

# @app.post("/generate-question/")
# async def generate_question(request: QuestionRequest):
#     question = generate_family_question(request.family_info, request.previous_answers)
#     return {"question": question}


