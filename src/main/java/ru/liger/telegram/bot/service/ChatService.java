package ru.liger.telegram.bot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
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
    private final Map<Long, String> chatState = new HashMap<>();

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
                return createMessage(chatId, "The command was not recognised", null);
            }
        }
    }

    private SendMessage createNewDialogWithUser(Long chatId) {
        var message = createMessage(chatId, "Введи свой вопрос");
        chatState.put(chatId, ChatStates.WAITING_USER_QUESTION);
        return message;
    }

    private SendMessage startCommandReceived(String userName, Long chatId) {
        var responseToUser = getStartResponseForUser(userName);
        chatState.put(chatId, ChatStates.WAITING_USER_QUESTION);
        return createMessage(chatId, responseToUser, null);
    }

    public String getStartResponseForUser(String userName) {
        return "Привет, " + userName + "! \n"+
                "Я бот Антона (и не только). На данном этапе развития я не умнее людей, " +
                "которые пьют кофе в серфе, но, надеюсь, я стану умнее!" +
                "Попробуй ввести свой вопрос - пока у нас нет модели, но я сэмулирую поведение";
    }


    private SendMessage handleUserResponseAfterBotResponse(Long chatId, String userText) {
        if (Commands.isAllowedCommandAfterModelResponse(userText)) {
            return saveUserOpinion(chatId, userText);
        }
        return createMessage(chatId, "Пожалуйста, используй одну из предложенных команд ниже");
    }

    private SendMessage saveUserOpinion(Long chatId, String userText) {
        //TODO ACTUALLY SAVE HIS OPINION
        chatState.put(chatId, ChatStates.WAITING_USER_QUESTION);
        return createMessage(chatId, "Cохранил твое мнение и/или продолжили диалог", null); // todo если мнение один текст, если новый диалог то другой
    }

    private SendMessage handleUserQuestion(Long chatId, String userText) {
        // TODO send question to bot and receive an answer
        var answer = "Response from bot"; // TODO use userText to ask model
        var keyboardMarkup = keyBoardService.getDefaultUserKeyboard();

        var message = createMessage (chatId, answer, keyboardMarkup);

        chatState.put(chatId, ChatStates.RECEIVED_BOT_RESPONSE);

        return message;
    }

    private SendMessage createMessage(Long chatId, String userMessage) {
        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(userMessage);
        return message;
    }

    private SendMessage createMessage(Long chatId, String answer, ReplyKeyboardMarkup keyboardMarkup) {
        var message =  createMessage(chatId, answer);
        if (keyboardMarkup == null) {
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            message.setReplyMarkup(keyboardRemove);
        } else  {
            message.setReplyMarkup(keyboardMarkup);
        }
        return message;
    }

    private boolean isUserResponseAfterBotResponse(Long chatId) {
        return chatState.containsKey(chatId) && chatState.get(chatId).equals(ChatStates.RECEIVED_BOT_RESPONSE);
    }

    private boolean isUserQuestion(Long chatId) {
        return chatState.containsKey(chatId) && chatState.get(chatId).equals(ChatStates.WAITING_USER_QUESTION);
    }

}