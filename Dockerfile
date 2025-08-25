FROM openjdk:23

COPY build/libs/PashmSocialCreditBot-2.1.3.jar /app.jar

EXPOSE 10000

CMD ["/usr/bin/java", "-jar", "/app.jar"]