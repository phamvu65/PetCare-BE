FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} pet-care.jar
ENTRYPOINT ["java", "-jar", "pet-care.jar"]
EXPOSE 8080