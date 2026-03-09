pipeline {
    agent any

    stages {

        stage('Build Backend JAR') {
            steps {
                dir('backend/hotel-inventory-backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Stop Old Containers') {
            steps {
                echo 'Stopping existing containers if any...'
                sh 'docker-compose down || true'
            }
        }

        stage('Build Images') {
            steps {
                echo 'Building Docker images...'
                sh 'docker-compose build'
            }
        }

        stage('Start Containers') {
            steps {
                echo 'Starting application...'
                sh 'docker-compose up -d'
            }
        }

        stage('Check Running Containers') {
            steps {
                echo 'Listing running containers...'
                sh 'docker ps'
            }
        }

    }
}
