FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/target/quiz-0.0.1-SNAPSHOT.jar quiz.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","quiz.jar"]
