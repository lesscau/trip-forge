FROM gradle:9-jdk25-alpine AS build
WORKDIR /workspace
COPY settings.gradle build.gradle ./
COPY src ./src
RUN gradle --no-daemon clean bootJar

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY --from=build /workspace/build/libs/trip-forge-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
