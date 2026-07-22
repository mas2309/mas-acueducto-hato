# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
COPY src/ src/
COPY config/ config/
RUN chmod +x gradlew && ./gradlew bootWar -x test -x checkstyleMain -x spotbugsMain --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appuser && adduser -S appuser -G appuser
COPY --from=build /app/build/libs/*.war app.war
RUN chown appuser:appuser app.war
USER appuser
EXPOSE 8084
ENTRYPOINT ["java", "-Dserver.port=8084", "-jar", "app.war"]
