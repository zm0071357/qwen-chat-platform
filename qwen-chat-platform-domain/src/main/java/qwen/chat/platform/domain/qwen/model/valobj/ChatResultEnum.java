package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 对话请求状态枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ChatResultEnum {
    SUCCESS(0, "请求成功"),
    FAILED(1, "请求失败，请稍后再试"),
    NULL_PARAMETER(1, "非法请求：缺少必要参数"),
    NOT_EXIST_PARAMETER(1, "非法请求：参数不存在"),
    EXIST_FILE_SEARCH(1, "非法请求：联网搜索不支持上传文件"),
    TIMEOUT(1, "请求超时，请稍后再试"),
    ;

    private Integer code;
    private String info;
}
