package ru.spring.weather.bot.flow.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;

@Component
public class AbortInputFlowCommand implements Command {

    public static final String NAME = "abortinputflow";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return "\uD83D\uDEAB Вернуться";
    }

    @Override
    public List<Stage> getKnownStages() {
        return List.of(
                Stage.VIEW_PHENOMS,
                Stage.ENTER_CITY,
                Stage.CONFIRM_CREATION,
                Stage.CONFIRM_REMOVAL
        );
    }

    @Override
    public void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
        switch (chatState.getCurrentStage()) {
            case VIEW_PHENOMS, ENTER_CITY, CONFIRM_REMOVAL -> {
                chatState.resetMenuMessageId();
                chatState.resetRequest();
                Command.enterStage(Stage.AUTH_MAIN_MENU, chatState, sender);
            }
            case CONFIRM_CREATION -> {
                chatState.resetMenuMessageId();
                chatState.resetRequest();
                Command.enterStage(Stage.CREATE_PHENOM, chatState, sender);
            }
        }
    }
}