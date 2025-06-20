package qwen.chat.platform.domain.qwen.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qwen.chat.platform.domain.qwen.model.valobj.FileTypeEnum;

import java.util.List;
import java.util.regex.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreRequestEntity {
    private String userId;
    private String content;
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
        int lastQuestionMarkIndex = fileName.lastIndexOf('?');
        if (lastQuestionMarkIndex == -1) {
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                return fileName.substring(lastDotIndex + 1);
            }
        } else {
            // 获取问号前的子字符串
            String beforeQuestion = fileName.substring(0, lastQuestionMarkIndex);

            // 在问号前部分查找最后一个点号的位置
            int lastDotIndex = beforeQuestion.lastIndexOf('.');
            if (lastDotIndex == -1) {
                return ""; // 没有点号则返回空字符串
            }
            // 截取最后一个点号之后到问号之前的部分
            return beforeQuestion.substring(lastDotIndex + 1);
        }
        return "";
    }
}
