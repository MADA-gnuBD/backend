# Java 17 이미지를 기반으로 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사 (jar 이름은 반드시 맞춰줘야 함)
COPY build/libs/*.jar app.jar

# Spring Boot가 사용하는 기본 포트
EXPOSE 8080

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
