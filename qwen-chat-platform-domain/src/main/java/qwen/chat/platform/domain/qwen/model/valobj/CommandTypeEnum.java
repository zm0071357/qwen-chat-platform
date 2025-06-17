package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CommandTypeEnum {

    CREATE_IMAGE(1, "生成图片", ""),
    DESCRIPTION_EDIT(2, "指令编辑", "description_edit"),
    REMOVE_WATERMARK(3, "去文字水印", "remove_watermark"),
    EXPAND(4, "扩图", "expand"),
    SUPER_RESOLUTION(5, "图像超分", "super_resolution"),
    COLORIZATION(6, "图像上色", "colorization"),
    ;

    private Integer type;
    private String info;
    private String function;

    public static String getCommand(Integer type) {
        for (CommandTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value.getInfo();
            }
        }
        return null;
    }
}
