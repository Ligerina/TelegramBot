package ru.liger.telegram.bot.utils;

import java.util.List;

public class Commands {

    public final static String START_COMMAND = "/start";
    public final static String START_NEW_DIALOG_COMMAND = "/startNewDialog";
    public final static String RESPONSE_LIKED_COMMAND = "/liked";
    public final static String RESPONSE_DISLIKED_COMMAND = "/disliked";

    public final static List<String> ALLOWED_COMMAND_AFTER_MODEL_RESPONSE = List.of(START_COMMAND,
            START_NEW_DIALOG_COMMAND,
            RESPONSE_LIKED_COMMAND,
            RESPONSE_DISLIKED_COMMAND);

    public static boolean isAllowedCommandAfterModelResponse(String userText) {
        return ALLOWED_COMMAND_AFTER_MODEL_RESPONSE.contains(userText);
    }



}
