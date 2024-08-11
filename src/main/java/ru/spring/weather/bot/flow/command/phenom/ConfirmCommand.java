package ru.spring.weather.bot.flow.command.phenom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.api.PhenomFeignClient;
import ru.spring.weather.bot.dto.CreationPhenomDto;
import ru.spring.weather.bot.dto.ViewPhenomDto;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ConfirmCommand implements Command {

    public static final String NAME = "confirmphenom";
    private final PhenomFeignClient phenomFeignClient;

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
                chatState.setTrackedPhenoms(phenomFeignClient.getAllPhenomsByChatId(chatState.getChatId()));
                var phenoms = chatState.getTrackedPhenoms();
                if (checkIfExists(phenoms, chatState.getCity(), chatState.getPhenomType())) {
                    sendTextMessage(chatState, "Вы уже добавляли такое явление", sender);
                } else {
                    phenomFeignClient.addPhenom(new CreationPhenomDto(
                            chatState.getCity(),
                            chatState.getPhenomType(),
                            chatState.getChatId()
                    ));
                    sendTextMessage(chatState, "Явление добавлено", sender);
                }
            }
            case CONFIRM_REMOVAL -> {
                phenomFeignClient.deletePhenom(chatState.getPhenomForRemoval());
                sendTextMessage(chatState, "Явление удалено", sender);
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
