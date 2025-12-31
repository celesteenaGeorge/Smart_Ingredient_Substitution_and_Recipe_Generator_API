pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -Dmaven.test.skip=true'
            }
        }

        stage('Verify') {
            steps {
                sh 'ls target'
            }
        }
    }
}
