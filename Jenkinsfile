pipeline {
    agent any

    tools {
        maven "maven"
    }
    environment {
            PATH = "/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:${env.PATH}"
    }
    stages {
        stage('SCM checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Lavanya1518/OrderService.git']])
            }
        }
        stage('Build process') {
            steps {
                script {
                    sh 'mvn clean install'
                }
            }
        }
        stage('Check Docker') {
            steps {
                sh '''
                echo "PATH=$PATH"
                which docker
                docker --version
                '''
            }
        }
        stage('Build docker image') {
            steps {
                 script {
                        sh 'docker build -t lavanya1518/spring-cid:1.0 .'
                   }
                }
        }
        stage('Deploy image to dockerhub') {
                    steps {
                         script {
                             withCredentials([usernamePassword(
                                 credentialsId: 'dockerhub-creds',
                                 usernameVariable: 'DOCKER_USERNAME',
                                 passwordVariable: 'DOCKER_PASSWORD'
                             )]) {
                                 sh '''
                                     echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                                     docker build -t lavanya1518/jenkins-pipeline-file:1.0 .
                                     docker push lavanya1518/jenkins-pipeline-file:1.0
                                 '''
                             }
                           }
                        }
                }
    }
    post {
        always {
            emailext(
                to: 'lavanya.info1518@gmail.com',
                subject: "Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                mimeType: 'text/html',
                replyTo: 'lavanya.info1518@gmail.com',
                body: """
                    <h2>Build Notification</h2>
                    <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                    <p><b>Status:</b> ${currentBuild.currentResult}</p>
                    <p>
                        <a href="${env.BUILD_URL}console">
                            View Console Output
                        </a>
                    </p>
                """
            )
        }
    }

}