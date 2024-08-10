package ru.spring.weather.bot.flow.command;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;

public class HelpCommand implements Command {
    public static final String NAME = "showhelp";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return "ℹ️ Справка";
    }

    @Override
    public List<Stage> getKnownStages() {
        return List.of(
                Stage.AUTH_MAIN_MENU,
                Stage.NOT_AUTHORIZED
        );
    }

    @Override
    public void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
        String regFlowInfo = chatState.isApproved() ?
                """          
                """ :
                """
                """;

        sendTextMessage(chatState, regFlowInfo, sender);

        Command.enterStage(chatState.getCurrentStage(), chatState, sender);
    }
}
