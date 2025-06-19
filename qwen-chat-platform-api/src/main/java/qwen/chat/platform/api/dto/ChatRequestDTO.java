package qwen.chat.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * 对话请求对象
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRequestDTO {
    private String userId;
    private String content;
    private List<String> file;
    private boolean think;
    private boolean search;
    @JsonProperty("history_code")
    private String historyCode;

}
