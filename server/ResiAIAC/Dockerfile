FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY . .
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/app.jar .

CMD ["java" , "-jar" , "app.jar"]