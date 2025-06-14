package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum FileTypeEnum {

    IMAGE(1, "图片"),
    VIDEO(2, "视频"),
    DOCUMENT(3, "文档 - EXCEL、PDE等"),
    ;

    private Integer fileType;
    private String info;
}
