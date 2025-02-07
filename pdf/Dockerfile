# 1️⃣ Python 3.9 Slim 기반 이미지 사용
FROM python:3.8-slim

# 2️⃣ 환경 변수 로드 (빌드 타임이 아니라 런타임에서도 사용 가능하도록 설정)
ENV FLASK_APP=recipe_data_processor.py \
    FLASK_RUN_HOST=0.0.0.0 \
    OPENAI_API_KEY=${OPENAI_API_KEY}

# 3️⃣ 작업 디렉토리 설정
WORKDIR /app

# 4️⃣ 불필요한 캐시 제거 및 필수 패키지 설치
RUN apt-get update && \
    apt-get install -y --no-install-recommends libgl1-mesa-glx && \
    rm -rf /var/lib/apt/lists/*

# 5️⃣ Python 패키지 설치 (캐싱 최적화)
COPY requirements.txt ./requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# 6️⃣ 애플리케이션 코드 복사 (불필요한 파일 제외)
COPY . .

# 7️⃣ Flask 포트 노출
EXPOSE 20250

# 8️⃣ 컨테이너 실행 시 Flask 서버 시작
CMD ["python", "recipe_data_processor.py"]
