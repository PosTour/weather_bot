package ru.spring.weather.bot.flow.command.phenom.type;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;

@Component
public class OvercastPhenomCommand implements Command {

    public static final String NAME = "overcastphenom";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return "Пасмурная погода";
    }

    @Override
    public List<Stage> getKnownStages() {
        return List.of(
                Stage.CREATE_PHENOM
        );
    }

    @Override
    public void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
        chatState.resetMenuMessageId();
        chatState.setPhenomType("Overcast");
        Command.enterStage(Stage.CONFIRM_CREATION, chatState, sender);
    }
}
