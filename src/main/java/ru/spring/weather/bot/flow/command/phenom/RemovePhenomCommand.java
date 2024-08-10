package ru.spring.weather.bot.flow.command.phenom;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;

public class RemovePhenomCommand implements Command {

    public static final String NAME = "deletephenom";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return "Удалить явление";
    }

    @Override
    public List<Stage> getKnownStages() {
        return List.of(
                Stage.REMOVE_PHENOM
        );
    }

    @Override
    public void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
        chatState.resetMenuMessageId();
        Command.enterStage(Stage.REMOVE_PHENOM, chatState, sender);
    }
}
