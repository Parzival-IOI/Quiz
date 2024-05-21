FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/Parzival-0.0.1-SNAPSHOT.jar Parzival.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","Parzival.jar"]