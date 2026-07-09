/*
pipeline {
    agent any

    tools {
        maven "maven"
    }
    stages {
        stage('SCM checkout') {
            steps {
                checkout scmGit(branches: [[name: '*//*
main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Lavanya1518/OrderService.git']])
            }
        }
        stage('Build process') {
            steps {
                script {
                    sh 'mvn clean install'
                }
            }
        }
       stage {
            steps {
                 script {
                      sh 'docker build -t lavanya1518/jenkins-pipeline-file:1.0 .'
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

} */
