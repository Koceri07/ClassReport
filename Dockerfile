# 1-ci mərhələ: Gradle ilə projektu build edirik
FROM gradle:8.5-jdk17-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -x test

# 2-ci mərhələ: Yalnız kiçik Alpine Java imicində çalışdırırıq
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]