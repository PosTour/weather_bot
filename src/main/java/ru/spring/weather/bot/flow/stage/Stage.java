package ru.spring.weather.bot.flow.stage;

import lombok.Getter;
import ru.spring.weather.bot.flow.command.AbortInputFlowCommand;
import ru.spring.weather.bot.flow.command.phenom.ConfirmCommand;
import ru.spring.weather.bot.flow.command.phenom.CreatePhenomCommand;
import ru.spring.weather.bot.flow.command.phenom.RemovePhenomCommand;
import ru.spring.weather.bot.flow.command.phenom.ViewPhenomCommand;
import ru.spring.weather.bot.flow.command.phenom.type.*;
import ru.spring.weather.bot.flow.command.register.RegisterCommand;
import ru.spring.weather.bot.flow.command.HelpCommand;
import ru.spring.weather.bot.flow.command.register.SendContactCommand;


public enum Stage {
    NOT_AUTHORIZED(
            "\uD83D\uDCF1 Добро пожаловать в \"МетеоБот\"",
            InputType.NONE,
            RegisterCommand.NAME,
            HelpCommand.NAME
    ),
    REG_NUMBER(
            "Для доступа к полной версии нужен ваш контакт.\nНажмите кнопку \uD83D\uDCF1 *Мой контакт* для отправки",
            InputType.CONTACT,
            SendContactCommand.NAME,
            AbortInputFlowCommand.NAME
    ),
    AUTH_MAIN_MENU(
            "\uD83D\uDCF1 Добро пожаловать в \"МетеоБот!\"",
            InputType.NONE,
            CreatePhenomCommand.NAME,
            ViewPhenomCommand.NAME,
            HelpCommand.NAME
    ),
    ENTER_CITY(
            "Введите название города",
            InputType.TEXT
    ),
    CREATE_PHENOM(
            "Выберите явление, которое хотите отслеживать",
            InputType.NONE,
            ClearPhenomCommand.NAME,
            OvercastPhenomCommand.NAME,
            RainPhenomCommand.NAME,
            ShowersPhenomCommand.NAME,
            SleetPhenomCommand.NAME,
            SnowPhenomCommand.NAME,
            HailPhenomCommand.NAME,
            ThunderstormPhenomCommand.NAME,
            AbortInputFlowCommand.NAME
    ),
    CONFIRM_CREATION(
            "Подтвердите, что хотите отслеживать это явление",
            InputType.NONE,
            ConfirmCommand.NAME,
            AbortInputFlowCommand.NAME
    ),
    VIEW_PHENOMS(
            "Выберите дальнейшее действие",
            InputType.NONE,
            RemovePhenomCommand.NAME,
            AbortInputFlowCommand.NAME
    ),
    REMOVE_PHENOM(
            "Введите номер явления, которое хотите перестать отслеживать",
            InputType.TEXT
    ),
    CONFIRM_REMOVAL(
            "Вы уверены, что хотите перестать отслеживать это явление?",
            InputType.NONE,
            ConfirmCommand.NAME,
            AbortInputFlowCommand.NAME
    );

    @Getter
    private final String header;
    @Getter
    private final String[] buttons;
    @Getter
    private int maxButtonsInRow = 1;
    private final InputType waitInputType;

    Stage(String header, InputType waitInputType, String... buttons) {
        this.header = header;
        this.buttons = buttons;
        this.waitInputType = waitInputType;
    }

    Stage(String header, InputType waitInputType, int maxButtonsInRow, String... buttons) {
        this.header = header;
        this.buttons = buttons;
        this.maxButtonsInRow = maxButtonsInRow;
        this.waitInputType = waitInputType;
    }

    public InputType waitInputType() {
        return waitInputType;
    }
}
