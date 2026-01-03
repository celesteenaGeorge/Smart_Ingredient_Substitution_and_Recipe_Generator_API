FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY RIS_App.jar  RIS_App.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "RIS_App.jar"]
