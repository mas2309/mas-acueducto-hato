# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
COPY src/ src/
COPY config/ config/
RUN chmod +x gradlew && ./gradlew bootWar -x test -x checkstyleMain -x spotbugsMain --no-daemon

# New Relic Java agent stage
FROM alpine:3.20 AS newrelic
ARG NEWRELIC_AGENT_VERSION=current
WORKDIR /tmp
RUN wget -q "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/${NEWRELIC_AGENT_VERSION}/newrelic-java.zip" \
    && unzip -q newrelic-java.zip

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appuser && adduser -S appuser -G appuser
COPY --from=build /app/build/libs/*.war app.war
COPY --from=newrelic /tmp/newrelic/newrelic.jar /tmp/newrelic/newrelic.yml newrelic/
RUN chown -R appuser:appuser app.war newrelic

# El agente queda apagado por defecto; se activa en el despliegue con
# NEW_RELIC_AGENT_ENABLED=true y NEW_RELIC_LICENSE_KEY.
ENV NEW_RELIC_AGENT_ENABLED=false \
    NEW_RELIC_APP_NAME=acueducto-backend \
    NEW_RELIC_LOG_FILE_NAME=STDOUT \
    NEW_RELIC_LOG_LEVEL=info \
    NEW_RELIC_DISTRIBUTED_TRACING_ENABLED=true \
    NEW_RELIC_APPLICATION_LOGGING_ENABLED=true \
    NEW_RELIC_APPLICATION_LOGGING_FORWARDING_ENABLED=true

USER appuser
EXPOSE 8084
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-Dserver.port=8084", "-jar", "app.war"]
