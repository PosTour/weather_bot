package ru.spring.weather.bot.flow.command.register;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;

@Component
public class RegisterCommand implements Command {

    public static final String NAME = "register";

    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return "\uD83D\uDD11 Регистрация";
    }

    //список этапов на которых команда доступна
    @Override
    public List<Stage> getKnownStages() {
        return List.of(Stage.NOT_AUTHORIZED);
    }

    @Override
    public void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
        chatState.resetMenuMessageId();
        Command.enterStage(Stage.REG_NUMBER, chatState, sender);
    }
}