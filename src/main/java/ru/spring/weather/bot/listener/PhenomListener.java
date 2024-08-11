package ru.spring.weather.bot.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spring.weather.bot.dto.NotificationDto;
import ru.spring.weather.bot.flow.EntryBot;
import ru.spring.weather.bot.flow.command.Command;

@RequiredArgsConstructor
@Component
public class PhenomListener {
    private final EntryBot telegramBot;
    private final Logger logger = LoggerFactory.getLogger(Command.class);
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(ConsumerRecord<String, String> payload) {
        NotificationDto notification = objectMapper.readValue(payload.value(), NotificationDto.class);

        String message;
        switch (notification.phenomType()) {
            case("Clear") -> message = "Завтра в " + notification.city() + " ожидается ясная погода";
            case("Hail") -> message = "Завтра в " + notification.city() + " ожидается град. Будьте осторожны";
            case("Overcast") -> message = "Завтра в " + notification.city() + " ожидается пасмурная погода";
            case("Rain") -> message = "Завтра в " + notification.city() + " ожидается дождь. Не забудьте зонт";
            case("Showers") -> message = "Завтра в " + notification.city() + " ожидаются ливни. Не забудьте зонт";
            case("Sleet") -> message = "Завтра в " + notification.city() + " ожидается слякоть";
            case("Snow") -> message = "Завтра в " + notification.city() + " ожидается выпадение снега";
            case("Thunderstorm") -> message = "Завтра в " + notification.city() + " ожидается гроза";
            default -> {
                logger.error("Неизвестное погодное явление");
                return;
            }
        }

        var sendMessage = new SendMessage(
                Long.toString(notification.chatId()), message);

        try {
            telegramBot.send(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Не удалось отправить уведомление пользователю");
        }
    }
}
