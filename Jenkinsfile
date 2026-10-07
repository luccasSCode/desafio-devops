pipeline {
    agent any

    environment {
        IMAGE = 'desafio-devops-html'
        CONTAINER = 'desafio-web'
        APP_PORT = '8081'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'docker build -t ${IMAGE}:latest .'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker stop ${CONTAINER} || true
                    docker rm ${CONTAINER} || true
                    docker run -d --name ${CONTAINER} -p ${APP_PORT}:80 ${IMAGE}:latest
                '''
            }
        }
    }
}
