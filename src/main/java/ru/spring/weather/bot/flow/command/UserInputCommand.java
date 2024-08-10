package ru.spring.weather.bot.flow.command;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;
import ru.spring.weather.bot.storage.ChatState;

@Component
@RequiredArgsConstructor
public class UserInputCommand implements Command {
    public static final String NAME = "###userinput$$$";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isApplicable(String textMessage, ChatState chatState) {
        return getName().equals(textMessage);
    }
}
