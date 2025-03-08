FROM openjdk:22

COPY build/libs/PashmSocialCreditBot-2.0.5.jar /app.jar

CMD ["/usr/bin/java", "-jar", "/app.jar", "/data/secret.properties", "/data/ratings.db"]