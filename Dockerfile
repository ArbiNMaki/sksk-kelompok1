FROM openjdk:21
ARG JAR_FILE=target/*.jar
COPY ./target/sk-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]