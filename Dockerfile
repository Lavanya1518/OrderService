FROM eclipse-temurin:17-jdk
WORKDIR /appContainer
ARG JAR_FILE=target/jenkins-cicd-docker.jar
COPY ${JAR_FILE} appContainer.jar
EXPOSE 8282
CMD ["java", "-jar", "jenkins-cicd-docker.jar"]