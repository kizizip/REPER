# 프로젝트 Java 버전에 맞는 openjdk 이미지 설정
FROM openjdk:17-jdk-slim

# DB Dependencies 설정 (mariadb와 mysql-client은 같은것...)
RUN apt-get update && apt-get install -y mariadb-client && rm -rf /var/lib/apt/lists/*

#만들면 생기려나
RUN mkdir -p /app/config


# gradle 빌드를 하면 build/libs 하위에 *.jar 생성됨. 해당 '*.jar'를 'app.jar'로 copy
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

# .yaml local/prod 프로필 분리 구조일때 실행할 프로필 지정 prod(=운영)
ENV USE_PROFILE prod

# Firebase Admin SDK JSON 파일을 컨테이너 내 경로에 복사
COPY ./src/main/resources/firebase/reper-7e5b4-firebase-adminsdk-fbsvc-ee6581830a.json /app/config/firebase.json

# Firebase JSON 파일 경로를 환경 변수로 설정
ENV FIREBASE_JSON=/app/config/firebase.json


# 이미지 빌드 명령
ENTRYPOINT ["java", "-Dspring.profiles.active=${USE_PROFILE}", "-jar", "app.jar"]
