# ---- Build stage ----
FROM gradle:8.7-jdk17-alpine AS build
WORKDIR /workspace
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
RUN chmod +x gradlew && ./gradlew clean bootJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 스냅샷/버전 변경에도 대응되도록 와일드카드 사용
COPY --from=build /workspace/build/libs/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
