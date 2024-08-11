package ru.spring.weather.bot.flow.command;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.api.PhenomFeignClient;
import ru.spring.weather.bot.dto.UserDto;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.register.SendContactCommand;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserInputCommand implements Command {
    private final Pattern CITY_PATTERN = Pattern.compile("[А-Я]\\w+");
    public static final String NAME = "###userinput$$$";
    private final PhenomFeignClient phenomFeignClient;

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

    @Override
    public void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
        chatState.resetMenuMessageId();

        String entry = entries.isEmpty() ? null : entries.get(0);
        switch (chatState.getCurrentStage()) {
            case REG_NUMBER -> {
                String phoneNumber = normalizePhoneNumber(entry);
                if (phoneNumber != null) {
                    chatState.setPhone(phoneNumber);
                    sendTextMessage(chatState, "Номер принят", sender);

                    phenomFeignClient.signUp(new UserDto(chatState.getChatId()));
                    chatState.setApproved(true);

                    Command.enterStage(chatState.isApproved() ?
                            Stage.AUTH_MAIN_MENU : Stage.NOT_AUTHORIZED, chatState, sender);
                } else {
                    sendValidationMessage(
                            chatState,
                            "Ввести номер телефона можно только через кнопку " + (new SendContactCommand().getLabel()) + ". Попробуйте еще раз",
                            sender
                    );
                    reenterStage(chatState, sender);
                }
            }
            case ENTER_CITY -> {
                if (entry != null && CITY_PATTERN.matcher(entry).matches()) {
                    chatState.setCity(entry);

                    Command.enterStage(Stage.CREATE_PHENOM, chatState, sender);
                } else {
                    sendValidationMessage(
                            chatState,
                            "Некорректный ввод. Попробуйте еще раз",
                            sender
                    );
                    reenterStage(chatState, sender);
                }
            }
            case REMOVE_PHENOM -> {
                boolean isNumber;
                try {
                    assert entry != null;
                    Integer.parseInt(entry);
                    isNumber = true;
                } catch (NumberFormatException e) {
                    isNumber = false;
                }
                if (isNumber && Integer.parseInt(entry) > 0 &&
                        Integer.parseInt(entry) < chatState.getTrackedPhenoms().size() + 1) {
                    int index = Integer.parseInt(entry);

                    var phenom = chatState.getTrackedPhenoms().get(index - 1).id();
                    chatState.setPhenomForRemoval(phenom);

                    Command.enterStage(Stage.CONFIRM_REMOVAL, chatState, sender);
                } else {
                    sendValidationMessage(
                            chatState,
                            "Нужно выбрать вариант из предложенных. Попробуйте еще раз",
                            sender
                    );
                    reenterStage(chatState, sender);
                }
            }
            default -> sendValidationMessage(chatState, "Неожиданная команда \uD83E\uDD37\u200D♂️", sender);
        }
    }

    private String normalizePhoneNumber(String number) {
        if (StringUtils.isBlank(number)) {
            return null;
        }
        char[] arr = number.toCharArray();
        StringBuilder result = new StringBuilder();
        for (char c : arr) {
            if (c >= '0' && c <= '9') {
                result.append(c);
            }
        }
        if (result.length() == 11) {
            return "7" + result.substring(1);
        }
        if (result.length() < 10) {
            return null;
        }
        return result.toString();
    }
}
