FROM openjdk:21 as first
# ARG TELEGRAM_BOT_NAME - возьмется из github secrets
ARG TELEGRAM_BOT_NAME
ENV TELEGRAM_BOT_NAME=$TELEGRAM_BOT_NAME

# ARG TELEGRAM_BOT_TOKEN - возьмется из github secrets
ARG TELEGRAM_BOT_TOKEN
ENV TELEGRAM_BOT_TOKEN=$TELEGRAM_BOT_TOKEN

#COPY build/libs/telegram.bot-0.0.1.jar /telegram-bot.jar

FROM first
# copy the repository form the previous image
COPY build/libs/telegram.bot-0.0.1.jar /telegram-bot.jar

CMD ["java", "-jar", "/telegram-bot.jar"]