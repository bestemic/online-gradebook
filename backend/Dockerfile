FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
COPY run-app.sh run-app.sh
ENV SPRING_PROFILES_ACTIVE=production
EXPOSE 8080
ENTRYPOINT ["/bin/bash", "/run-app.sh"]