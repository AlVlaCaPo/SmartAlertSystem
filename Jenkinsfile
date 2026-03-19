pipeline {
    agent any

    tools {
        maven 'maven-3.9.6'
        jdk 'jdk-21'
    }

    environment {
        DOCKER_IMAGE = 'auth-service:latest'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package -pl auth-service -am'
            }
            post {
                always {
                    junit 'auth-service/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    // El comando 'mvn clean package' en el Dockerfile ya ejecuta los tests
                    sh "docker build -t ${DOCKER_IMAGE} -f auth-service/Dockerfile ."
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
