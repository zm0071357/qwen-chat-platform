package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum FileTypeEnum {

    IMAGE(1, "图片"),
    AUDIO(2, "音频"),
    VIDEO(3, "视频"),
    DOCUMENT(4, "文档 - EXCEL、PDF等"),
    UNKNOWN(5, "未知类型"),
    ;

    private Integer fileType;
    private String info;
}
