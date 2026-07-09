FROM
WORKDIR /appContainer
COPY /target/jenkins-cicd-docker.jar /appContainer
EXPOSE 8282
CMD ["java", "-jar", "jenkins-cicd-docker.jar"]