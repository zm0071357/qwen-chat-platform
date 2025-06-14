package qwen.chat.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import qwen.chat.platform.domain.qwen.model.valobj.File;

import java.util.List;

/**
 * 对话请求对象
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRequestDTO {
    private String userId;
    private String content;
    @JsonProperty("file")
    private List<File> file;
    @JsonProperty("msg_type")
    private Integer msgType;
    private boolean think;
    private boolean search;
    @JsonProperty("history_code")
    private String historyCode;

}
