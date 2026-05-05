# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

<<<<<<< HEAD
ENTRYPOINT ["java", "-jar", "app.jar"]

CMD ["sh", "-c", "java -jar app.jar --server.port=$PORT"]
=======
ENTRYPOINT ["java", "-jar", "app.jar"]
>>>>>>> 5aaa7099da1b51ce0075289538892df2866fc149
