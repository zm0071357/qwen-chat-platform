package qwen.chat.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class HistoryRequestDTO {
    private String userId;

    @JsonProperty("history_code")
    private String historyCode;
}
