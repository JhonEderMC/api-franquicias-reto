# Build stage
FROM gradle:8.3.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon --stacktrace --info

# Run stage
FROM openjdk:21-jdk-slim
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/build/libs/franquicias-api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]