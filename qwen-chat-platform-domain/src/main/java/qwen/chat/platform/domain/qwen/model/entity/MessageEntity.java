package qwen.chat.platform.domain.qwen.model.entity;

import lombok.*;
import qwen.chat.platform.domain.qwen.model.valobj.File;

import java.util.List;
import java.util.regex.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    private String userId;

    private String content;

    private Integer msgType;

    private String historyCode;

    private List<File> fileList;

    private boolean think;

    private boolean search;

    /**
     * 判断内容是否为链接
     * @param content
     * @return
     */
    public boolean isLink(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        // 匹配在线链接的正则表达式
        String regex = "^(?i)(https?|ftp)://[^\\s\\p{Cntrl}]+$";
        return Pattern.matches(regex, content);
    }

}
