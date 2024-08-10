package ru.spring.weather.bot.flow.command.phenom;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.dto.ViewPhenomDto;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;
import java.util.Objects;

public class ConfirmCommand implements Command {

    public static final String NAME = "confirmphenom";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return "Подтвердить";
    }

    @Override
    public List<Stage> getKnownStages() {
        return List.of(
                Stage.CONFIRM_CREATION,
                Stage.CONFIRM_REMOVAL,
                Stage.AUTH_MAIN_MENU
        );
    }

    @Override
    public void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
        switch (chatState.getCurrentStage()) {
            case CONFIRM_CREATION -> {
                // TODO: Получение явлений пользователя
                var phenoms = chatState.getTrackedPhenoms();
                if (checkIfExists(phenoms, chatState.getCity(), chatState.getPhenomType())) {
                    sendTextMessage(chatState, "Вы уже добавляли такое явление", sender);
                } else {
                    sendTextMessage(chatState, "Явление добавлено", sender);
                    // TODO: Отправка нового явления
                }
            }
            case CONFIRM_REMOVAL -> {
                sendTextMessage(chatState, "Явление удалено", sender);
                // TODO: Удаление явления
            }
        }
        chatState.resetMenuMessageId();
        chatState.resetRequest();
        Command.enterStage(Stage.AUTH_MAIN_MENU, chatState, sender);
    }

    private boolean checkIfExists(List<ViewPhenomDto> phenoms, String city, String type) {
        boolean ifExists = false;
        for (var phenom : phenoms) {
            if (Objects.equals(phenom.city(), city) && Objects.equals(phenom.type(), type)) {
                ifExists = true;
                break;
            }
        }
        return ifExists;
    }
}
