FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd -r appuser && useradd -r -g appuser appuser
COPY build/libs/*.war app.war
RUN chown appuser:appuser app.war
USER appuser
EXPOSE 8083
ENTRYPOINT ["java", "-Dspring.profiles.active=huawei", "-jar", "app.war"]
