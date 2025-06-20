package qwen.chat.platform.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private Long id;
    private String userId;
    private String historyCode;
    private String historyJson;
    private String requestJson;
    private Date createTime;
    private Date updateTime;
}
