# 프로젝트 Java 버전에 맞는 openjdk 이미지 설정
FROM openjdk:17-jdk-slim

# DB Dependencies 설정 (mariadb와 mysql-client은 같은것...)
RUN apt-get update && apt-get install -y mariadb-client && rm -rf /var/lib/apt/lists/*

# Jaso Analyzer 플러그인 추가 (필요한 경우)
# Jaso Analyzer 플러그인 추가 (정확한 경로로 수정)
COPY /home/ubuntu/docker/jaso-analyzer /usr/share/elasticsearch/plugins/jaso-analyzer
RUN ls -la /usr/share/elasticsearch/plugins/jaso-analyzer

# gradle 빌드를 하면 build/libs 하위에 *.jar 생성됨. 해당 '*.jar'를 'app.jar'로 copy
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

# .yaml local/prod 프로필 분리 구조일때 실행할 프로필 지정 prod(=운영)
ENV USE_PROFILE prod

# 🔥 Firebase JSON을 환경 변수로 전달하여 파일을 저장하지 않음
# 🔥 Jenkins Pipeline에서 ENV FIREBASE_JSON 값을 주입하도록 변경
ENV FIREBASE_JSON=""

# 실행 전 Jaso Analyzer 플러그인이 없으면 복사
RUN if [ ! -d /usr/share/elasticsearch/plugins/jaso-analyzer ]; then \
      mkdir -p /usr/share/elasticsearch/plugins/jaso-analyzer; \
      cp -r /app/docker/jaso-analyzer/* /usr/share/elasticsearch/plugins/jaso-analyzer; \
    fi

# 이미지 빌드 명령
ENTRYPOINT ["java", "-Dspring.profiles.active=${USE_PROFILE}", "-jar", "app.jar"]
