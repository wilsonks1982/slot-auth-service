FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN useradd --create-home springuser

COPY --from=build /app/target/*.jar app.jar

RUN chown springuser:springuser app.jar

USER springuser

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]