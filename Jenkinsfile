pipeline {
    agent any

    tools {
        maven 'maven-3.9.6'
        jdk 'jdk-21'
    }

    environment {
        AUTH_IMAGE = 'auth-service:latest'
        COLLECTOR_IMAGE = 'data-collector-service:latest'
        ALERT_IMAGE = 'alert-service:latest'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    sh "docker build -t ${AUTH_IMAGE} -f auth-service/Dockerfile ."
                    sh "docker build -t ${COLLECTOR_IMAGE} -f data-collector-service/Dockerfile ."
                    sh "docker build -t ${ALERT_IMAGE} -f alert-service/Dockerfile ."
                }
            }
        }
    }

    post {
        success {
            echo "Build successful!"
        }
        failure {
            echo "Build failed!"
        }
    }
}
