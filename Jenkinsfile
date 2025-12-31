pipeline {
    agent any

    stages {
        stage('Build JAR') {
            steps {
                sh 'mvn clean package -Dmaven.test.skip=true'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ris-backend .'
            }
        }
    }
}