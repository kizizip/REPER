# 프로젝트 Java 버전에 맞는 openjdk 이미지 설정
FROM openjdk:17-jdk-slim

# DB Dependencies 설정 (MySQL 클라이언트로 변경)
RUN apt-get update && apt-get install -y mysql-client && rm -rf /var/lib/apt/lists/*

# gradle 빌드를 하면 build/libs 하위에 *.jar 생성됨. 해당 '*.jar'를 'app.jar'로 cpoy
ARG JAR_FILE_PATH=build/libs/*.jar 
COPY ${JAR_FILE_PATH} app.jar

# .yaml local/prod 프로필 분리 구조일때 실행할 프로필 지정 prod(=운영)
ENV USE_PROFILE prod

# 이미지 빌드 명령
ENTRYPOINT ["java", "-Dspring.profiles.active=${USE_PROFILE}", "-jar", "app.jar"]

RUN curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-17.04.0-ce.tgz \
  && tar xzvf docker-17.04.0-ce.tgz \
  && mv docker/docker /usr/local/bin \
  && rm -r docker docker-17.04.0-ce.tgz