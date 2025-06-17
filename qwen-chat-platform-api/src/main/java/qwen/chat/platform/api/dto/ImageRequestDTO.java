package qwen.chat.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageRequestDTO {
    private String userId;
    @JsonProperty("history_code")
    private String historyCode;
    private String content;
    @JsonProperty("command_type")
    private Integer commandType;
    @JsonProperty("size_type")
    private Integer sizeType;
    private String refer;
}
