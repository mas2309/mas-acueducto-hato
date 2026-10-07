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
    NEW_RELIC_APPLICATION_LOGGING_FORWARDING_ENABLED=true \
    # Logs: incluir MDC (usuario) y no enviar DEBUG/TRACE para cuidar la cuota gratuita
    NEW_RELIC_APPLICATION_LOGGING_FORWARDING_CONTEXT_DATA_ENABLED=true \
    NEW_RELIC_APPLICATION_LOGGING_FORWARDING_LOG_LEVEL_DENYLIST=debug,trace \
    # Errores de negocio/autenticación: visibles pero sin afectar la tasa de error
    NEW_RELIC_ERROR_COLLECTOR_EXPECTED_STATUS_CODES=400-403 \
    # Base de datos: trazas desde 1 s y plan de ejecución/stack de SQL desde 300 ms
    NEW_RELIC_TRANSACTION_TRACER_TRANSACTION_THRESHOLD=1.0 \
    NEW_RELIC_TRANSACTION_TRACER_EXPLAIN_THRESHOLD=0.3 \
    NEW_RELIC_TRANSACTION_TRACER_STACK_TRACE_THRESHOLD=0.3 \
    # JVM: perfilado en tiempo real con Java Flight Recorder
    NEW_RELIC_JFR_ENABLED=true

USER appuser
EXPOSE 8084
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-Dserver.port=8084", "-jar", "app.war"]
