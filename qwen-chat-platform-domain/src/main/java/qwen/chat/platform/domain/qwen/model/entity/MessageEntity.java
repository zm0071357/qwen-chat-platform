package qwen.chat.platform.domain.qwen.model.entity;

import lombok.*;
import qwen.chat.platform.domain.qwen.model.valobj.FileTypeEnum;

import java.util.List;
import java.util.regex.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    private String userId;

    private String content;

    private Integer msgType;

    private String historyCode;

    private List<String> fileList;

    private boolean think;

    private boolean search;
}
