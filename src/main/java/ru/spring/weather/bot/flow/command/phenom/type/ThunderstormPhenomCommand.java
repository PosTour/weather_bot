package ru.spring.weather.bot.flow.command.phenom.type;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;

@Component
public class ThunderstormPhenomCommand implements Command {

    public static final String NAME = "thunderstormphenom";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return "Гроза \uD83C\uDF29\uFE0F";
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
        chatState.setPhenomType("Thunderstorm");
        Command.enterStage(Stage.CONFIRM_CREATION, chatState, sender);
    }
}
