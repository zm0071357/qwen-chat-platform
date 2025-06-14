package qwen.chat.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "qwen.sdk.config", ignoreInvalidFields = true)
public class QwenConfigProperties {
    private boolean enable;
    private String apiKey;
    private String analysisVideoUrl;
}
