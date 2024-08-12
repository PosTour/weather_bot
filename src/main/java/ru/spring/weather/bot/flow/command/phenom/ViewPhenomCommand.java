package ru.spring.weather.bot.flow.command.phenom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.api.PhenomFeignClient;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewPhenomCommand implements Command {

    public static final String NAME = "viewphenoms";
    private final PhenomFeignClient phenomFeignClient;

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
        chatState.setTrackedPhenoms(phenomFeignClient.getAllPhenomsByChatId(chatState.getChatId()));
        var phenoms = chatState.getTrackedPhenoms();
        if (phenoms.isEmpty()) {
            sendTextMessage(chatState, "Вы пока не добавляли явлений", sender);
            Command.enterStage(Stage.AUTH_MAIN_MENU, chatState, sender);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < phenoms.size(); i++) {
                sb.append(i + 1)
                    .append(". Явление: ")
                    .append(convertTypeToName(phenoms.get(i).type()))
                    .append(". Город: ").append(phenoms.get(i).city())
                    .append("\n");
            }

            sendTextMessage(chatState, sb.toString(), sender);
            Command.enterStage(Stage.VIEW_PHENOMS, chatState, sender);
        }
    }

    public String convertTypeToName(String type) {
        String name;
        switch (type) {
            case("CLEAR") -> name = "Ясная погода";
            case("HAIL") -> name = "Град";
            case("OVERCAST") -> name = "Пасмурная погода";
            case("RAIN") -> name = "Дождь";
            case("SHOWERS") -> name = "Ливень";
            case("SLEET") -> name = "Слякоть";
            case("SNOW") -> name = "Снег";
            case("THUNDERSTORM") -> name = "Гроза";
            default -> name = "Неизвестное явление";
        }
        return name;
    }
}
