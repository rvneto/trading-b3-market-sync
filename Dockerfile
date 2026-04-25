FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
# Ensure correct timezone for market hours scheduling
RUN apk add --no-cache tzdata
ENV TZ=America/Sao_Paulo
ENTRYPOINT ["java", "-Duser.timezone=America/Sao_Paulo", "-jar", "app.jar"]