FROM openjdk:22-oracle
ARG JAR_FILE=build/libs/yo-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]