FROM maven:3.9.7-eclipse-temurin-17 AS build
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]