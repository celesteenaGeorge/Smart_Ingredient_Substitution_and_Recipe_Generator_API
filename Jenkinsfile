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

        stage('Deploy to EC2') {
            steps {
                withCredentials([
                    string(credentialsId: 'openai-api-url', variable: 'OPENAI_API_URL'),
                    string(credentialsId: 'openai-api-key', variable: 'OPENAI_API_KEY')
                ]) {
                    sshagent(credentials: ['ec2-ssh-key']) {
                        sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@13.60.12.187 '
                            docker stop ris-backend || true
                            docker rm ris-backend || true

                            docker run -d --name ris-backend \
                              -p 8080:8080 \
                              -e OPENAI_API_URL=${OPENAI_API_URL} \
                              -e OPENAI_API_KEY=${OPENAI_API_KEY} \
                              ris-backend
                        '
                        """
                    }
                }
            }
        }
    }
}
