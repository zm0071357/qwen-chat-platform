package qwen.chat.platform.domain.qwen.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVideoEntity {
    private String userId;
    private String historyCode;
    private String content;
    private String firstFrameUrl;
    private String lastFrameUrl;
    private List<ChatRequest.Input.Message> history;
}
