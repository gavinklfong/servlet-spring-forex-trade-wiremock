FROM openjdk:17-jdk-slim-buster

# install Node JS and json mock server
RUN apt-get update && apt upgrade -y \
    && apt-get install -y curl \
    && curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs \
    && npm install -g json-server

COPY mock-server/mock-data.json /app/mock-data.json

ARG JAR_FILE=app/target/*.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8080

WORKDIR /app

COPY docker/entrypoint.sh /app/entrypoint.sh

CMD ["/app/entrypoint.sh"]

