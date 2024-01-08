FROM azul/zulu-openjdk-alpine:15.0.3

COPY build/libs/PashmSocialCreditBot-1.0.0.jar /app.jar

CMD ["/usr/bin/java", "-jar", "/app.jar"]