package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum UploadFileResultEnum {

    SUCCESS(0, "上传成功"),
    FAILED(1, "上传失败：网络连接失败，请稍后再试"),
    USER_NOT_EXIST(1, "用户不存在"),
    NULL_PARAMETER(1, "上传失败：必要参数为空"),
    ;

    private Integer code;
    private String info;
}
