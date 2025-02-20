import os                                  # 운영 체제와 상호 작용 (에: 환경 변수 로드드)
import json                                # json 데이터 변환
import requests                            # spring boot로 전송
from openai import OpenAI                  # gpt api
import fitz                                # pdf 파일에서 텍스트 추출
import numpy as np                         # 벡터 연산 및 코사인 유사도 계산
from dotenv import load_dotenv             # .env 파일에서 환경 변수 로드
from flask import Flask, request, jsonify  # rest api 서버 구현현
import boto3                               # aws s3 접근
import botocore                            # boto3 예외 처리리
import base64                              # base64 인코딩/디코딩
from io import BytesIO                     # 바이너리 데이터 처리
from sentence_transformers import SentenceTransformer # 임베딩
from concurrent.futures import ThreadPoolExecutor     # 병렬처리
import re                                             # 청크

# .env 파일 로드
load_dotenv()
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# AWS S3 클라이언트 생성성
s3 = boto3.client(
    "s3",
    aws_access_key_id=os.getenv("AWS_ACCESS_KEY_ID"),
    aws_secret_access_key=os.getenv("AWS_SECRET_ACCESS_KEY"),
    region_name="ap-northeast-2"
)


app = Flask(__name__)

# 애니메이션 목록 및 임베딩 관련 함수들
# 미리 정의한 애니메이션 목록: 각 항목은 키워드와 URL을 포함
ANIMATIONS = [
    {"keyword": "블루베리를 블렌더에 넣고 혼합합니다.", "url": "https://cdn.lottielab.com/l/FFMJLV74Qm6bfo.json"},
    {"keyword": "자바칩을 블렌더에 넣고 혼합합니다.", "url": "https://cdn.lottielab.com/l/ENtDzinSoQZDPq.json"},
    {"keyword": "민트초코 재료를 블렌더에 넣고 혼합합니다.", "url": "https://cdn.lottielab.com/l/95Dg6EC3kRfgri.json"},
    {"keyword": "쿠앤크를 블렌더에 넣고 혼합합니다.", "url": "https://cdn.lottielab.com/l/7XThKYcimpCMMt.json"},
    {"keyword": "딸기를 블렌더에 넣고 혼합합니다.", "url": "https://cdn.lottielab.com/l/4gZp1dQ4pj4mB1.json"},
    {"keyword": "요거트를 블렌더에 넣고 혼합합니다.", "url": "https://cdn.lottielab.com/l/Dybf1Je8jZjR1L.json"},
    {"keyword": "초코 파우더를 컵에 담습니다.", "url": "https://cdn.lottielab.com/l/7FQ8q41J2syYBB.json"},
    {"keyword": "초코 파우더를 준비합니다.", "url": "https://cdn.lottielab.com/l/BmkF6MHBAZSQdW.json"},
    {"keyword": "고구마 파우더를 컵에 담습니다.", "url": "https://cdn.lottielab.com/l/Co1f3bNMNDtEar.json"},
    {"keyword": "요거트 파우더를 컵에 담습니다.", "url": "https://cdn.lottielab.com/l/4dN1uJNg62XiKZ.json"},
    {"keyword": "바닐라 시럽을 펌프로 추출합니다.", "url": "https://cdn.lottielab.com/l/4kq2VaFenuZhLs.json"},
    {"keyword": "헤이즐넛 시럽을 펌프로 추출합니다.", "url": "https://cdn.lottielab.com/l/CQQ78SacnAXP8k.json"},
    {"keyword": "카라멜 드리즐링을 음료 위에 뿌립니다.", "url": "https://cdn.lottielab.com/l/EVPfK3e5skieMi.json"},
    {"keyword": "카라멜 시럽을 펌프로 추출합니다.", "url": "https://cdn.lottielab.com/l/qXGCejGzgtaXe0.json"},
    {"keyword": "아이스티를 컵에 붓습니다.", "url": "https://cdn.lottielab.com/l/ErsA1gsymfKrp6.json"},
    {"keyword": "사이다를 컵에 붓습니다.", "url": "https://cdn.lottielab.com/l/6Y3CtYEieXZZrx.json"},
    {"keyword": "얼음을 컵에 담습니다.", "url": "https://cdn.lottielab.com/l/7Hr5gt1NZkp3ci.json"},
    {"keyword": "얼음을 준비합니다.", "url": "https://cdn.lottielab.com/l/6QbraoGrKFWZwG.json"},
    {"keyword": "티백을 컵에 담습니다.", "url": "https://cdn.lottielab.com/l/72bFfMRVK8HRpz.json"},
    {"keyword": "물을 컵에 붓습니다.", "url": "https://cdn.lottielab.com/l/3ByZp4zyFxH5L6.json"},
    {"keyword": "뜨거운 물을 컵에 붓습니다.", "url": "https://cdn.lottielab.com/l/3ByZp4zyFxH5L6.json"},
    {"keyword": "스팀 우유를 컵에 붓습니다.", "url": "https://cdn.lottielab.com/l/C5V62488EpY7ZK.json"},
    {"keyword": "우유를 컵에 붓습니다.", "url": "https://cdn.lottielab.com/l/CUbn1kkAFg2Mry.json"},
    {"keyword": "우유를 스팀기로 데웁니다.", "url": "https://cdn.lottielab.com/l/9R9pCXFPnWSKTU.json"},
    {"keyword": "휘핑크림을 음료 위에 올립니다.", "url": "https://cdn.lottielab.com/l/FJ5ab6hdNstTRJ.json"},
    {"keyword": "딸기 베이스를 컵에 담습니다.", "url": "https://cdn.lottielab.com/l/2Y6Ggm7jWcNaJ6.json"},
    {"keyword": "레몬청을 스푼으로 덜어 냅니다.", "url": "https://cdn.lottielab.com/l/A95CqnJxqAfT79.json"},
    {"keyword": "자몽청을 스푼으로 덜어 냅니다.", "url": "https://cdn.lottielab.com/l/CLut8Kky51eJ7B.json"},
    {"keyword": "크림베이스를 스푼으로 올립니다.", "url": "https://cdn.lottielab.com/l/3Zb2Fu2gKEqwdu.json"},
    {"keyword": "우유 거품을 스푼으로 떠서 올립니다.", "url": "https://cdn.lottielab.com/l/3Zb2Fu2gKEqwdu.json"},
    {"keyword": "대추청을 스푼으로 덜어 냅니다.", "url": "https://cdn.lottielab.com/l/6Tm6WxmBktLg6d.json"},
    {"keyword": "초코 소스를 스푼으로 추가합니다.", "url": "https://cdn.lottielab.com/l/81ckDCvhP5kb4H.json"},
    {"keyword": "카라멜 소스를 스푼으로 추가합니다.", "url": "https://cdn.lottielab.com/l/9p7bDTV4ad7gPo.json"},
    {"keyword": "에스프레소 샷을 머신에서 추출합니다.", "url": "https://cdn.lottielab.com/l/7s4iHVxukkaC57.json"},
    {"keyword": "추출한 에스프레소 샷을 컵에 붓습니다.", "url": "https://cdn.lottielab.com/l/DnWc3zyzDZBhAx.json"}
]

# PDF에서 텍스트 추출
def extract_text_from_pdf(pdf_path):
    doc = fitz.open(pdf_path)
    full_text = ""

    for page in doc:
        full_text += page.get_text() + "\n"

    menu_blocks = re.split(r"\n이름: ", full_text)

    if menu_blocks[0].strip() == "":
        menu_blocks = menu_blocks[1:]

    menu_blocks = [f"이름: {block.strip()}" for block in menu_blocks]

    return menu_blocks

# 청크 나누기 
def chunk_menus(menu_blocks, chunk_size=3):
    menu_chunks = []
    for i in range(0, len(menu_blocks), chunk_size):
        chunk = menu_blocks[i:i + chunk_size]
        menu_chunks.append(chunk)
    return menu_chunks


# 병렬로  GPT API 요청 보내기
def process_recipe_text_parallel(chunks):
    with ThreadPoolExecutor() as excutor:
        results = list(excutor.map(process_recipe_text, chunks))

        combined_data = {"recipes": []}
        for result in results:
            json_data = json.loads(result)
            combined_data["recipes"].extend(json_data.get("recipes", []))
        
        return json.dumps(combined_data)


# GPT API를 사용해 레시피 데이터를 JSON으로 변환
def process_recipe_text(text):
    if not isinstance(text, str):
        text = str(text)

    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {
                "role": "developer",
                "content": """
                You are an AI that extracts coffee shop recipe data into structured JSON format.
                Please ensure:
                - The response follows the JSON schema exactly.
                - **ALL recipes in the input text** must be included in the response. DO NOT OMIT any recipes.*
                - Steps must be separated into distinct actions (not combined in one sentence)..
                - The `type` field **MUST be one of**: `HOT`, `ICE`.
                - The `category` field **MUST be one of**: `COFFEE`, `NON_COFFEE`, `ADE`, `TEA`, `SMOOTHIE`, `FRAPPE`.
                - **DO NOT** generate any other values for `type` and `category`."
                """
            },
            {"role": "user", "content": text}
        ],
        response_format={"type": "json_schema", "json_schema": {"name": "recipe_schema", "schema": {
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
                                "items": {"type": "object", "properties": {"ingredientName": {"type": "string"}}, "required": ["ingredientName"], "additionalProperties": False}
                            },
                            "recipeSteps": {
                                "type": "array",
                                "items": {"type": "object", "properties": {"instruction": {"type": "string"}}, "required": ["instruction"], "additionalProperties": False}
                            }
                        },
                        "required": ["recipeName", "category", "type", "ingredients", "recipeSteps"]
                    }
                }
            },
            "required": ["recipes"]
        }}}
    )
        
    return response.choices[0].message.content


# s3 버킷 내에 지정한 키의 객체가 존재하는지 확인
def check_image_exists(bucket_name, my_key):
    try:
        s3.head_object(Bucket=bucket_name, Key=my_key)
        return True # 이미지 존재함
    except botocore.exceptions.ClientError as e:
        if e.response["Error"]["Code"] == "404":
            return False # 이미지 존재하지 않음
        else:
            raise # 다른 예외 발생


# 레시피 이미지 생성
def generate_recipe_image(recipe_name, recipe_type):
    formatted_recipe_name = recipe_name.replace(" ", "") # 띄어쓰기 제거
    bucket_name = "reper-images"
    my_key = f"recipe_images/{formatted_recipe_name}_{recipe_type}.png"

    # s3에 이미지가 존재하는지 확인
    if check_image_exists(bucket_name, my_key):
        s3_url = f"https://{bucket_name}.s3.amazonaws.com/{my_key}"
        return s3_url

    # 이미지가 존재하지 않으면 생성
    response = client.images.generate(
        model="dall-e-3",
        prompt=f"""A high-resolution, realistic photograph of a {recipe_type.lower()} {recipe_name}. 
                The beverage is presented on a clean, white background with soft, even lighting, 
                clearly showing its rich colors, textures, and fresh ingredients. 
                The background is completely white, ensuring the focus remains on the vibrant and well-defined drink in the foreground.""",
        size="1024x1024",
        quality="standard",
        response_format="b64_json",
        n=1,
    )

    # s3에 저장하고 s3 url 반환
    if response.data:
        image_base64 = response.data[0].b64_json
        image_data = base64.b64decode(image_base64)
        s3.upload_fileobj(BytesIO(image_data), bucket_name, my_key)
        s3_url = f"https://{bucket_name}.s3.amazonaws.com/{my_key}"
        return s3_url
    return None


# 임베딩 bge-m3
model=SentenceTransformer("BAAI/bge-m3")
def get_embedding(text):
    embedding = model.encode(text, convert_to_numpy=True)
    return embedding


def cosine_similarity(vec_a, vec_b):
    """
    두 벡터 간의 코사인 유사도를 계산
    """
    return np.dot(vec_a, vec_b) / (np.linalg.norm(vec_a) * np.linalg.norm(vec_b))

# 애니메이션 목록에 대해 미리 임베딩 계산 후 캐시
ANIMATION_EMBEDDINGS = {}
for anim in ANIMATIONS:
    ANIMATION_EMBEDDINGS[anim["url"]] = get_embedding(anim["keyword"])

def map_animation_url(instruction, threshold=0.7):
    """
    주어진 instruction에 대해 임베딩을 계산한 후,
    캐시된 애니메이션 임베딩과의 코사인 유사도를 비교하여
    가장 유사한 애니메이션의 URL을 반환
    임계값(threshold) 이상일 경우 URL을, 아니면 None을 반환
    """
    # if not instruction or instruction.strip() == "":
    #     return None
    instruction_embedding = get_embedding(instruction)

    best_similarity = 0.0
    best_url = None
    for url, anim_embedding in ANIMATION_EMBEDDINGS.items():
        # print(instruction_embedding.shape)
        # print(anim_embedding.shape)
        similarity = cosine_similarity(instruction_embedding, anim_embedding)
        # print(similarity)
        if similarity > best_similarity:
            best_similarity = similarity
            best_url = url
    # print('-----')
    
    return best_url if best_similarity >= threshold else None


# Spring Boot로 JSON 데이터 전송 함수
def send_json_to_spring(data, store_id):
    SPRING_BOOT_URL = f"http://localhost:8080/api/stores/{store_id}/recipes"
    headers = {"Content-Type" : "application/json"}
    response = requests.post(SPRING_BOOT_URL, headers=headers, json=data["recipes"]) # JSON 데이터 중 recipes 리스트만 전송

    if response.status_code == 200:
        print("JSON 데이터가 Spring Boot에 저장 성공!")
    else:
        print(f"Spring Boot 전송 실패! 상태 코드: {response.status_code}, 응답: {response.text}")



#★ PDF 파일을 업로드 하면 JSON 변환 후 반환 ★
@app.route("/upload", methods=["POST"])
def upload_file():
    file = request.files.get("file")
    store_id = request.form.get("storeId")

    if file:
        # 파일을 임시 경로(예: "temp.pdf")에 저장
        file_path = "temp.pdf"
        file.save(file_path)
        
        # PDF에서 텍스트 추출
        docs = extract_text_from_pdf(file_path)

        # 청크 분할할
        text_chunks = chunk_menus(docs)

        # 병렬로 GPT API로 JSON 구조 생성
        structured_json = process_recipe_text_parallel(text_chunks)
        data = json.loads(structured_json) # JSON 문자열을 -> python 딕셔너리로 변환
        
        recipe_cnt = len(data.get("recipes",  []))
        
        # 각 레시피 스텝에 대해 animationUrl 매핑 수행
        for recipe in data.get("recipes", []):
            recipe["recipeImg"] = generate_recipe_image(recipe["recipeName"], recipe["type"])
            for step in recipe.get("recipeSteps", []):
                instruction = step.get("instruction", "")
                animation_url = map_animation_url(instruction)
                if animation_url:
                    step["animationUrl"] = animation_url
        
        # 최종 JSON 데이터를 Spring Boot로 전송
        send_json_to_spring(data, store_id)
        return jsonify({"recipeCount" : recipe_cnt}), 200
    else:
        return jsonify({"error": "파일이 없습니다."}), 400


# 애플리케이션 실행
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)