FROM eclipse-temurin:17-jdk

WORKDIR /appContainer

COPY target/jenkins-cicd-docker.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]