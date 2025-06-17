package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SizeTypeEnum {
    ONE_ONE(1, "1024*1024", "1：1"),
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
}
