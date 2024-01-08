FROM openjdk:18

COPY build/libs/PashmSocialCreditBot-1.0.0.jar /app.jar

CMD ["/usr/bin/java", "-jar", "/app.jar"]