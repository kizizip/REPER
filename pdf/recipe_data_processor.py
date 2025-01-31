import os
import json
import requests # spring boot로 전송하기 위해 추가
from openai import OpenAI
import fitz
from dotenv import load_dotenv 
from flask import Flask, request, jsonify

# .env 파일 로드
load_dotenv()
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

app = Flask(__name__)


# PDF 파일에서 텍스트 추출
def extract_text_from_pdf(pdf_path):
    doc = fitz.open(pdf_path)
    text = "\n".join([page.get_text() for page in doc])
    return text


# GPT API를 사용해 레시피 데이터를 JSON으로 변환환
def process_recipe_text(text):
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {
                "role": "developer",
                "content": """You are an AI that extracts coffee shop recipe data into structured JSON format.
                Please ensure:
                - The response follows the JSON schema exactly.
                - ALL recipes in the input text must be included in the response. DO NOT OMIT any recipes.
                - Steps must be separated into distinct actions (not combined in one sentence).
                - The `type` field **MUST be one of**: `HOT`, `ICE`.
                - The `category` field **MUST be one of**: `COFFEE`, `NON_COFFEE`, `ADE`, `TEA`, `SMOOTHIE`, `FRAPPE`.
                - **DO NOT** generate any other values for `type` and `category`."

                Example output:
                {
                    "recipes": [
                        {
                            "recipeName": "카페라떼",
                            "category": "COFFEE",
                            "type": "HOT",
                            "ingredients": [
                                { "ingredientName": "에스프레소 2샷" },
                                { "ingredientName": "우유 250ml" }
                            ],
                            "recipeSteps": [
                                { "instruction": "에스프레소 2 샷을 추출합니다." },
                                { "instruction": "추출한 샷을 컵에 붓습니다." },
                                { "instruction": "우유 250ml 를 스팀합니다." },
                                { "instruction": "스팀 우유 250ml 를 컵에 붓습니다." }
                            ]
                        }
                    ]
                }
                """
            },
            {"role": "user", "content": text}
        ],
        response_format={
            "type": "json_schema",
            "json_schema": {
                "name": "recipe_schema",
                "schema": {
                    "type": "object",
                    "properties": {
                        "recipes": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "recipeName": {"type": "string"},
                                    "category": {"type": "string"},
                                    "type": {"type": "string"},
                                    "ingredients": {
                                        "type": "array",
                                        "minItems": 1,
                                        "items": {"type": "string"}
                                    },
                                    "recipeSteps": {
                                        "type": "array",
                                        "minItems": 1,
                                        "items": {"type": "string"}
                                    }
                                },
                                "required": ["recipeName", "category", "type", "ingredients", "recipeSteps"],
                                "additionalProperties": False
                            }
                        }
                    },
                    "required": ["recipes"],
                    "additionalProperties": False
                }
            }
        }
    )
    return response.choices[0].message.content


# spring boot로 JSON 데이터 전송
def send_json_to_spring(data):
    SPRING_BOOT_URL = "http://localhost:8080/api/stores/1/recipes"
    headers = {"Content-Type" : "application/json"}
    response = requests.post(SPRING_BOOT_URL, headers=headers, json=data["recipes"]) # JSON 데이터 중 recipes 리스트만 전송

    if response.status_code == 200:
        print("JSON 데이터가 Spring Boot에 저장 성공!")
    else:
        print(f"Spring Boot 전송 실패! 상태 코드: {response.status_code}, 응답: {response.text}")


# PDF 파일을 업로드 하면 JSON 변환 후 반환
@app.route("/upload", methods=["POST"])
def upload_file():
    file = request.files["file"]
    if file:
        file_path = "C:/Users/SSAFY/Desktop/레시피.pdf"
        file.save(file_path)
        text = extract_text_from_pdf(file_path)
        structured_data = json.loads(process_recipe_text(text)) 
        
        send_json_to_spring(structured_data)

        return jsonify(structured_data)
    
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)