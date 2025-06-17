package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CommandTypeEnum {

    CREATE_IMAGE(1, "生成图片"),
    DESCRIPTION_EDIT(2, "指令编辑"),
    REMOVE_WATERMARK(3, "去文字水印"),
    EXPAND(4, "扩图"),
    SUPER_RESOLUTION(5, "图像超分"),
    COLORIZATION(6, "图像上色"),
    ;

    private Integer type;
    private String info;

    public static String getCommand(Integer type) {
        for (CommandTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value.getInfo();
            }
        }
        return SizeTypeEnum.ONE_ONE.getSize();
    }
}
