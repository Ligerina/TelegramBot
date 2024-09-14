FROM openjdk:21
ENV TELEGRAM_BOT_NAME=TestBotForPetProhectBot
ARG TELEGRAM_BOT_TOKEN
ENV TELEGRAM_BOT_TOKEN=$TELEGRAM_BOT_TOKEN
COPY build/libs/telegram.bot-0.0.1-SNAPSHOT.jar /telegram-bot.jar
CMD ["java", "-jar", "/telegram-bot.jar"]