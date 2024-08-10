package ru.spring.weather.bot.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spring.weather.bot.dto.ViewPhenomDto;
import ru.spring.weather.bot.flow.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
public class ChatState {

    @Getter
    private long chatId;

    @Getter
    private String phone;

    @Getter
    private Stage currentStage;

    @Getter
    private String city;

    @Getter
    private String phenomType;

    @Getter
    private List<ViewPhenomDto> trackedPhenoms;

    @Getter
    private Integer phenomForRemoval;

    @Getter
    private int menuMessageId = -1;

    @Getter
    private boolean approved = false;

    @Getter
    private UUID employeeId;

    @JsonIgnore
    private final List<Update> updates = new ArrayList<>();

    @Setter
    @JsonIgnore
    private ChatStateMap storage;

    public ChatState(long chatId, ChatStateMap storage) {
        this.chatId = chatId;
        this.setStorage(storage);
    }

    @JsonIgnore
    public String getChatIdStr() {
        return String.valueOf(getChatId());
    }

    public void setCurrentStage(Stage stage) {
        currentStage = stage;
        updates.clear();
        getStorage().ifPresent(ChatStateMap::store);
    }

    public void addUpdate(Update update) {
        updates.add(update);
    }

    @JsonIgnore
    public int getLastReceivedMessageId() {
        if (updates.isEmpty()) {
            return -1;
        }
        Update last = updates.get(updates.size() - 1);
        if (last.hasMessage()) {
            return last.getMessage().getMessageId();
        }
        if (last.hasCallbackQuery()) {
            return last.getCallbackQuery().getMessage().getMessageId();
        }
        return -1;
    }

    public boolean canRedrawMenu() {
        return getMenuMessageId() != -1;
    }

    public void resetMenuMessageId() {
        setMenuMessageId(-1);
    }

    public void setMenuMessageId(int menuMessageId) {
        this.menuMessageId = menuMessageId;
        getStorage().ifPresent(ChatStateMap::store);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        getStorage().ifPresent(ChatStateMap::store);
    }

    public void setCity(String city) {
        this.city = city;
        getStorage().ifPresent(ChatStateMap::store);
    }

    public void setPhenomForRemoval(Integer phenomForRemoval) {
        this.phenomForRemoval = phenomForRemoval;
        getStorage().ifPresent(ChatStateMap::store);
    }

    public void setPhenomType(String phenomType) {
        this.phenomType = phenomType;
        getStorage().ifPresent(ChatStateMap::store);
    }

    public void setTrackedPhenoms(List<ViewPhenomDto> trackedPhenoms) {
        this.trackedPhenoms = trackedPhenoms;
        getStorage().ifPresent(ChatStateMap::store);
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
        getStorage().ifPresent(ChatStateMap::store);
    }

    public void resetRequest() {
        this.phenomType = null;
        this.city = null;
        this.phenomForRemoval = null;
        getStorage().ifPresent(ChatStateMap::store);
    }

    public Optional<ChatStateMap> getStorage() {
        return Optional.ofNullable(storage);
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
        getStorage().ifPresent(ChatStateMap::store);
    }
}
