FROM openjdk:17
WORKDIR /appContainer
ARG JAR_FILE=target/jenkins-CiCd.jar
COPY ${JAR_FILE} appContainer.jar
EXPOSE 8282
ENTRYPOINT ["java", "-jar", "jenkins-CiCd.jar"]