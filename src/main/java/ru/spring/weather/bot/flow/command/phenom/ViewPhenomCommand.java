package ru.spring.weather.bot.flow.command.phenom;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;

public class ViewPhenomCommand implements Command {

    public static final String NAME = "viewphenoms";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return "Посмотреть отслеживаемые явления";
    }

    @Override
    public List<Stage> getKnownStages() {
        return List.of(
                Stage.AUTH_MAIN_MENU,
                Stage.VIEW_PHENOMS
        );
    }

    @Override
    public void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
        chatState.resetMenuMessageId();
        // TODO: Получение явлений пользователя
        var phenoms = chatState.getTrackedPhenoms();
        if (phenoms == null) {
            sendTextMessage(chatState, "Вы пока не добавляли явлений", sender);
            Command.enterStage(Stage.AUTH_MAIN_MENU, chatState, sender);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < phenoms.size(); i++) {
                sb.append(i + 1)
                    .append(" ")
                    .append(phenoms.get(i).type())
                    .append("\n");
            }

            sendTextMessage(chatState, sb.toString(), sender);
            Command.enterStage(Stage.VIEW_PHENOMS, chatState, sender);
        }
    }
}
