pipeline {
    agent any

    stages {

        stage('Build JAR') {
            steps {
                sh 'mvn clean package -Dmaven.test.skip=true'
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
                        ssh -o StrictHostKeyChecking=no ec2-user@13.61.19.36 \
                        'mkdir -p ~/app'
                        """

                        sh """
                        scp -o StrictHostKeyChecking=no target/RIS_App.jar \
                            ec2-user@13.61.19.36:~/app/RIS_App.jar
                        """

                   
                        sh """
                        scp -o StrictHostKeyChecking=no Dockerfile \
                            ec2-user@13.61.19.36:~/app/Dockerfile
                        """

                        sh '''
                        ssh -o StrictHostKeyChecking=no ec2-user@13.61.19.36 "
                            sudo docker stop ris-backend || true
                            sudo docker rm ris-backend || true
                            sudo docker build -t ris-backend ~/app

                            sudo docker run -d --name ris-backend \
                              --restart unless-stopped \
                              -p 8080:8080 \
                              -e OPENAI_API_URL=$OPENAI_API_URL \
                              -e OPENAI_API_KEY=$OPENAI_API_KEY \
                              ris-backend
                        "
                       '''
                    }
                }
            }
        }
    }
}
