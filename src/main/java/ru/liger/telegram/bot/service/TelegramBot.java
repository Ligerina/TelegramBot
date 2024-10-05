package ru.liger.telegram.bot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liger.telegram.bot.config.BotConfig;
import ru.liger.telegram.bot.entity.TelegramUser;

@Service
@AllArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private final TelegramUserService telegramUserService;

    @Override
    public void onUpdateReceived(Update update) {
        log.info("uodate recieved");

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        var chatId = update.getMessage().getChatId();
        var userText = update.getMessage().getText();
        var username = update.getMessage().getChat().getUserName();
        log.info("info: chatod: {}\\n msgtext: {}\n username {}\n", chatId, userText, username);

        switch (userText) {
            case "/start":
                telegramUserService.addNewUser(TelegramUser.builder()
                        .chatId(chatId)
                        .username(username)
                        .build());
                startCommandReceived(username, chatId);
                break;
            default:
                //logs
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
                "Я бот Антона (и не только). На данном этапе развития я не умнее людей, " +
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
            // add logs
            throw new RuntimeException(e);
        }

    }
}
