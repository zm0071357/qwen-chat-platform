package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SizeTypeEnum {
    ONE_ONE(1, "1024*1024", "1:1"),
    FOUR_THREE(2, "960*1440", "4:3"),
    NINE_SIXTEEN(3, "810*1440", "9:16"),
    SIXTEEN_NINE(4, "1440*810", "16:9"),
    ;

    private Integer type;
    private String size;
    private String info;

    public static String getSize(Integer sizeType) {
        for (SizeTypeEnum value : values()) {
            if (value.getType().equals(sizeType)) {
                return value.getSize();
            }
        }
        return SizeTypeEnum.ONE_ONE.getSize();
    }

    public static String getInfo(Integer sizeType) {
        for (SizeTypeEnum value : values()) {
            if (value.getType().equals(sizeType)) {
                return value.getInfo();
            }
        }
        return SizeTypeEnum.ONE_ONE.getInfo();
    }
}
