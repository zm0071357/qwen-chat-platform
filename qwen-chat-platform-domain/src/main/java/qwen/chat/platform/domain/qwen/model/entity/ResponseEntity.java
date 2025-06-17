package qwen.chat.platform.domain.qwen.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntity {
    private boolean isSuccess;
    private String message;
    private String result;
}
