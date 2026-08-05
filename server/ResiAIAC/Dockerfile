FROM maven:3.9-eclipse-temurin-21-alpine

WORKDIR /
COPY . .
RUN mvn package -DskipTests
WORKDIR target
CMD ["java" , "-jar" , "app.jar"]
