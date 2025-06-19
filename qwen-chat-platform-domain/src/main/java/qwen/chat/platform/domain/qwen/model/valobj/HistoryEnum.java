package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum HistoryEnum {

    SUCCESS(0, "获取成功"),
    FAILED(1, "获取失败"),
    ILLEAGAL(1, "获取失败：参数非法"),
    NULL_PARAMETER(1, "获取失败：必要参数为空"),
    USER_NOT_EXIST(1, "用户不存在"),
    ;

    private Integer code;
    private String info;
}
