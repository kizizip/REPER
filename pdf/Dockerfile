# 1️⃣ Python 3.9 Slim 기반 이미지 사용 (더 안정적인 3.9 버전)
FROM python:3.9-slim


# 작업 디렉토리 설정
WORKDIR /app

# 시스템 패키지 업데이트 및 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    && rm -rf /var/lib/apt/lists/*

# Python 패키지 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 파일 복사
COPY . .


# 환경변수 설정 (Jenkins에서 전달받음)
ENV OPENAI_API_KEY=""
ENV AWS_ACCESS_KEY_ID=""
ENV AWS_SECRET_ACCESS_KEY=""

# Flask 포트 노출
EXPOSE 20250

# 컨테이너 실행 시 Flask 서버 시작
CMD ["python", "recipe_data_processor.py"]
