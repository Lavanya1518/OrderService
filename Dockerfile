FROM openjdk:17
WORKDIR /appContainer
ARG JAR_FILE=target/springboot-docker.jar
COPY ${JAR_FILE} appContainer.jar
EXPOSE 8282
ENTRYPOINT ["java", "-jar", "springboot-docker.jar"]