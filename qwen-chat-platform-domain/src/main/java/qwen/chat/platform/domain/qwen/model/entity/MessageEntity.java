package qwen.chat.platform.domain.qwen.model.entity;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import qwen.chat.platform.domain.qwen.model.valobj.File;
import qwen.chat.platform.domain.qwen.model.valobj.FileTypeEnum;

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

    private List<String> fileList;

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

    /**
     * 判断文件类型
     * @param fileName
     * @return
     */
    public FileTypeEnum fileType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
                return FileTypeEnum.IMAGE;
            case "mp4":
                return FileTypeEnum.VIDEO;
            case "docx":
            case "xlsx":
            case "pdf":
                return FileTypeEnum.DOCUMENT;
            default:
                return FileTypeEnum.UNKNOWN;
        }
    }

    /**
     * 提取文件扩展名
     * @param fileName
     * @return
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

}
