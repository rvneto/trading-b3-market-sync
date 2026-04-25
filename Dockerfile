FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
# Garantir que a timezone esteja correta para a regra de horário de pregão
RUN apk add --no-available tzdata
ENV TZ=America/Sao_Paulo
ENTRYPOINT ["java", "-jar", "app.jar"]