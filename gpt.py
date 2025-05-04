# gpt.py


import os
from dotenv import load_dotenv

load_dotenv()

# 확인용 출력
print("📁 현재 실행 디렉토리:", os.getcwd())
print("🧪 [DEBUG] API KEY:", os.getenv("OPENAI_API_KEY"))


# import os
# from dotenv import load_dotenv
# from openai import OpenAI

# # 🔐 .env에서 키 불러오기
# load_dotenv()
# api_key = os.getenv("OPENAI_API_KEY")

# if not api_key:
#     raise ValueError("❌ OPENAI_API_KEY가 없습니다. .env 설정 확인하세요.")

# client = OpenAI(api_key=api_key)

# def generate_family_question(family_info: dict, previous_answers: str = "") -> str:
#     prompt = f"""
#     다음은 한 가족의 정보야:
#     아버지: {family_info.get("father", "")}
#     어머니: {family_info.get("mother", "")}
#     자녀: {family_info.get("child", "")}

#     최근 대화 내용:
#     {previous_answers}

#     이 가족이 오늘 자연스럽게 대화할 수 있도록
#     따뜻하고 공감할 수 있는 질문을 하나만 만들어줘.
#     """

#     response = client.chat.completions.create(
#         model="gpt-3.5-turbo",  # 또는 "gpt-4"
#         messages=[
#             {"role": "system", "content": "너는 가족 간 따뜻한 대화를 이끌어주는 AI 챗봇이야."},
#             {"role": "user", "content": prompt}
#         ],
#         temperature=0.8,
#         max_tokens=150
#     )

#     return response.choices[0].message.content
