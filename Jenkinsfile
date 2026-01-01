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
                        ssh -o StrictHostKeyChecking=no ec2-user@13.60.12.187 '
                            mkdir -p ~/app
                        '
                        """

 
                        sh """
                        scp -o StrictHostKeyChecking=no target/RIS_App.jar \
                            ec2-user@13.60.12.187:~/app/RIS_App.jar
                        """


                        sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@13.60.12.187 '
                            cat > ~/app/Dockerfile << EOF
							FROM eclipse-temurin:21-jdk
							WORKDIR /app
							COPY RIS_App.jar app.jar
							EXPOSE 8080
							ENTRYPOINT ["java","-jar","app.jar"]
							EOF

                            docker stop ris-backend || true
                            docker rm ris-backend || true
                            docker build -t ris-backend ~/app

                            docker run -d --name ris-backend \
                              --restart unless-stopped \
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
