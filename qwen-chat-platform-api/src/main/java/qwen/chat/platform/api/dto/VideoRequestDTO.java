package qwen.chat.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoRequestDTO {
    private String userId;
    @JsonProperty("history_code")
    private String historyCode;
    private String content;
    @JsonProperty("first_frame_url")
    private String firstFrameUrl;
    @JsonProperty("last_frame_url")
    private String lastFrameUrl;
}
