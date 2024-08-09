package ru.spring.weather.bot.flow.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.croc.corpbot.bot.flow.EntryBot;
import ru.croc.corpbot.bot.flow.stage.Stage;
import ru.croc.corpbot.bot.storage.ChatState;

import java.util.List;

@Component
@Slf4j
public class HelpCommand implements Command {
    public static final String NAME = "showhelp";

    @Autowired
    private ResourceLoader resLoad;

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
                В боте вам доступен поиск по базе контактов сотрудников и работа с вашими отпусками (просмотр и создание новых).                            
            """ :
            """
                *Регистрация в сервисе производится в два этапа:*
                1. Вы нажимаете "Регистрация"
                2. Вы предоставляете свой контакт. Если он есть в БД сотрудников, то вы переходите в режим работы с ботом как зарегистрированный пользователь
                """;

        sendTextMessage(chatState, regFlowInfo, sender);

        Command.enterStage(chatState.getCurrentStage(), chatState, sender);
    }
}
