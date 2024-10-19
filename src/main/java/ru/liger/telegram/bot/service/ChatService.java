package ru.liger.telegram.bot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.liger.telegram.bot.entity.TelegramUser;
import ru.liger.telegram.bot.utils.ChatStates;
import ru.liger.telegram.bot.utils.Commands;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class ChatService {

    private final KeyBoardService keyBoardService;
    private final TelegramUserService telegramUserService;
    private final Map<Long, String> userState = new HashMap<>();

    public SendMessage handleUserRequest(Update update) {

        var chatId = update.getMessage().getChatId();
        var userText = update.getMessage().getText();
        var username = update.getMessage().getChat().getUserName();

        if (isUserQuestion(chatId)) {
            return handleUserQuestion(chatId, userText);
        }

        if (isUserResponseAfterBotResponse(chatId)) {
            return handleUserResponseAfterBotResponse(chatId, userText);
        }

        return handleUserCommand(chatId, userText, username);
    }

    private SendMessage handleUserCommand(Long chatId, String userText, String username) {
        switch (userText) {
            case Commands.START_COMMAND -> {
                telegramUserService.addNewUser(TelegramUser.builder()
                        .chatId(chatId)
                        .username(username)
                        .build());
                return startCommandReceived(username, chatId);
            }
            case Commands.START_NEW_DIALOG_COMMAND -> {
                return createNewDialogWithUser(chatId);
            }
            default -> {
                log.warn("Unknown command message {}. User = {}, chatId = {}", userText, username, chatId);
                return createMessage(chatId, "The command was not recognised");
            }
        }
    }

    public String getStartResponseForUser(String userName) {
        return "Привет, " + userName + "! \n"+
                "Я бот Антона (и не только). На данном этапе развития я не умнее людей, " +
                "которые пьют кофе в серфе, но, надеюсь, я стану умнее!" +
                "Попробуй ввести свой вопрос - пока у нас нет модели, но я сэмулирую поведение";
    }

    private SendMessage createNewDialogWithUser(Long chatId) {
        SendMessage message = createMessage(chatId, "Введи свой вопрос");
        userState.put(chatId, ChatStates.WAITING_USER_QUESTION);
        return message;
    }

    private SendMessage startCommandReceived(String userName, Long chatId) {
        var responseToUser = getStartResponseForUser(userName);
        userState.put(chatId, ChatStates.WAITING_USER_QUESTION);
        return createMessage(chatId, responseToUser);
    }


    private SendMessage handleUserResponseAfterBotResponse(Long chatId, String userText) {
        if (Commands.ALLOWED_COMMAND_AFTER_MODEL_RESPONSE.contains(userText)) {
            userState.put(chatId, ChatStates.WAITING_USER_QUESTION);
            return saveUserOpinion(chatId, userText);
        }
        return createMessage(chatId, "Пожалуйста, используй одну из преложенных команд ниже");
    }

    private SendMessage saveUserOpinion(Long chatId, String userText) {
        //ACTUALLY SAVE HIS OPINION
        return createMessage(chatId, "Cохранил твое мнение или продолжили диалог"); // todo если мнение один текст, если новый диалог то другой
    }

    private SendMessage handleUserQuestion(Long chatId, String userText) {
        // send question to bot and receive answer
        var answer = "Response from bot"; // use userText to ask model
        var keyboardMarkup = keyBoardService.getDefaultUserKeyboard();

        var message = createMessage (chatId, answer, keyboardMarkup);

        userState.put(chatId, ChatStates.RECEIVED_BOT_RESPONSE);

        return message;
    }

    private SendMessage createMessage(Long chatId, String userMessage) {
        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(userMessage);
        return message;
    }

    private SendMessage createMessage(Long chatId, String answer, ReplyKeyboardMarkup keyboardMarkup) {
        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(answer);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private boolean isUserResponseAfterBotResponse(Long chatId) {
        return userState.containsKey(chatId) && userState.get(chatId).equals(ChatStates.RECEIVED_BOT_RESPONSE);
    }

    private boolean isUserQuestion(Long chatId) {
        return userState.containsKey(chatId) && userState.get(chatId).equals(ChatStates.WAITING_USER_QUESTION);
    }


}