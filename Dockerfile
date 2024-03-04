FROM openjdk:21

COPY build/libs/PashmSocialCreditBot-1.4.2.jar /app.jar

CMD ["/usr/bin/java", "-jar", "/app.jar", "/data/secret.properties", "/data/ratings.db"]