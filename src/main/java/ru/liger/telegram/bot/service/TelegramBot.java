package ru.liger.telegram.bot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liger.telegram.bot.config.BotConfig;

@Slf4j
@Service
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        var chatId = update.getMessage().getChatId();
        var userText = update.getMessage().getText();
        var userName = update.getMessage().getChat().getUserName();

        switch (userText) {
            case "/start":
                startCommandReceived(userName, chatId);
                log.info("User {} received start message", userName);
                break;
            default:
                log.warn("User {} send unrecognised command: {}", userName, userText);
                sendMessage(chatId, "The command was not recognised");
        }

    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void startCommandReceived(String userName, Long chatId) {
        var responseToUser = "Привет, " + userName + "! \n"+
                "Я бот Антона. На данном этапе развития я не умнее людей, " +
                "которые пьют кофе в серфе, но, надеюсь, я стану умнее!";
        sendMessage(chatId, responseToUser);
    }

    private void sendMessage(long chatId, String messageText) {
        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageText);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
