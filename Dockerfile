FROM openjdk:23

COPY build/libs/PashmSocialCreditBot-2.1.1.jar /app.jar

CMD ["/usr/bin/java", "-jar", "/app.jar"]