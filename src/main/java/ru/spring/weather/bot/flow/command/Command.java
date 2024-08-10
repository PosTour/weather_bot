package ru.spring.weather.bot.flow.command;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.register.SendContactCommand;
import ru.spring.weather.bot.flow.stage.Stage;
import ru.spring.weather.bot.storage.ChatState;

import java.util.*;
import java.util.stream.Collectors;

public interface Command {
    String COMMAND_PREFIX = "/";
    String COMMAND_PARAM_SEP = ":";

    Logger log = LoggerFactory.getLogger(Command.class);

    String getName();

    String getLabel();

    default List<Stage> getKnownStages() {
        return Collections.emptyList();
    }

    default boolean isApplicable(String textMessage, ChatState chatState) {
        return (COMMAND_PREFIX + getName()).equalsIgnoreCase(textMessage) && getKnownStages().contains(chatState.getCurrentStage());
    }

    default void acceptMessage(List<String> entries, ChatState chatState, EntryBot sender) throws TelegramApiException {
    }

    static void enterStage(Stage newStage, ChatState chatState, EntryBot sender) throws TelegramApiException {

        int menuMessageId = drawSimpleInlineMenu(
                chatState,
                newStage.getMaxButtonsInRow(),
                newStage.getHeader(),
                extractMenu(newStage, sender.getKnownCommands()),
                sender
        );

        chatState.setCurrentStage(newStage);
        chatState.setMenuMessageId(menuMessageId);
    }

    default void reenterStage(ChatState chatState, EntryBot sender) throws TelegramApiException {
        enterStage(chatState.getCurrentStage(), chatState, sender);
    }

    static List<Pair<String, String>> extractMenu(Stage stage, List<Command> knownCommands) {
        List<Pair<String, String>> result = new ArrayList<>();
        Arrays
                .stream(stage.getButtons())
                .forEach(b -> {
                    Optional<Command> cmd = knownCommands.stream().filter(c -> Objects.nonNull(c.getName()) && c.getName().equals(b)).findAny();
                    if (cmd.isPresent()) {
                        result.add(Pair.of(cmd.get().getName(), cmd.get().getLabel()));
                    } else {
                        log.warn("Unable to find any command by name: " + b);
                    }
                });
        return result;
    }

    static int drawSimpleInlineMenu(
            ChatState state,
            int maxInRow,
            String menuHeader,
            List<Pair<String, String>> menuSource,
            EntryBot sender
    ) throws TelegramApiException {
        ReplyKeyboard replyKeyboard;

        if (menuSource.stream().anyMatch(p -> p.getKey().equals(SendContactCommand.NAME))) {
            ReplyKeyboardMarkup keyBoardMarkup = new ReplyKeyboardMarkup();

            List<KeyboardRow> keyboard = menuSource
                    .stream()
                    .map(p -> {
                        KeyboardButton button = new KeyboardButton();
                        if (p.getKey().equals(SendContactCommand.NAME)) {
                            button.setRequestContact(true);
                        }
                        button.setText(p.getValue());
                        KeyboardRow row = new KeyboardRow();
                        row.add(button);
                        return row;
                    })
                    .collect(Collectors.toList());

            keyBoardMarkup.setKeyboard(keyboard);
            keyBoardMarkup.setResizeKeyboard(true);

            replyKeyboard = keyBoardMarkup;
        } else {
            List<InlineKeyboardButton> buttons = menuSource
                    .stream()
                    .map(p -> {
                        InlineKeyboardButton button = new InlineKeyboardButton();
                        button.setCallbackData(COMMAND_PREFIX + p.getKey());
                        String label = p.getValue();
                        button.setText(label);
                        return button;
                    })
                    .collect(Collectors.toList());

            int size = buttons.size();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            for (int i = 0; i < size / maxInRow + (size % maxInRow > 0 ? 1 : 0); i++) {
                keyboard.add(buttons.subList(i * maxInRow, Math.min((i + 1) * maxInRow, size)));
            }

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(keyboard);
            replyKeyboard = inlineKeyboardMarkup;
        }

        if (state.canRedrawMenu() && replyKeyboard instanceof InlineKeyboardMarkup) {
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setText(menuHeader);
            editMessageText.setMessageId(state.getMenuMessageId());
            editMessageText.setChatId(state.getChatIdStr());
            editMessageText.enableMarkdown(true);
            sender.execute(editMessageText);

            EditMessageReplyMarkup editMenu = new EditMessageReplyMarkup();
            editMenu.setReplyMarkup((InlineKeyboardMarkup) replyKeyboard);
            editMenu.setMessageId(state.getMenuMessageId());
            editMenu.setChatId(state.getChatIdStr());
            sender.execute(editMenu);

            return state.getMenuMessageId();
        } else {
            SendMessage msg = new SendMessage(state.getChatIdStr(), menuHeader);
            msg.setReplyMarkup(replyKeyboard);
            msg.enableMarkdown(true);
            return sender.execute(msg).getMessageId();
        }
    }

    default void sendValidationMessage(ChatState state, String text, EntryBot sender) throws TelegramApiException {
        var sendMessage = new SendMessage(state.getChatIdStr(), text);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyToMessageId(state.getLastReceivedMessageId());
        sender.send(sendMessage);
    }

    default void sendTextMessage(ChatState state, String text, EntryBot sender) {
        var sendMessage = new SendMessage(state.getChatIdStr(), text);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sendMessage.enableMarkdown(true);
        try {
            sender.send(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        state.resetMenuMessageId();
    }

    default void sendContact(ChatState state, String phoneNumber, String firstName, EntryBot sender) {
        var sendMessage = new SendContact(state.getChatIdStr(), phoneNumber, firstName);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        try {
            sender.send(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        state.resetMenuMessageId();
    }
}
