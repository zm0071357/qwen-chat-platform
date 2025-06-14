package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MessageTypeEnum {
    TEXT(1, "文本"),
    FILE(2, "文件 - 包括各类文档和图片"),
    ;

    private Integer msgType;
    private String info;
}
