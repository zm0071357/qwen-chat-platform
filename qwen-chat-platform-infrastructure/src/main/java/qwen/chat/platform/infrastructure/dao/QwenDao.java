package qwen.chat.platform.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import qwen.chat.platform.infrastructure.dao.po.History;

import java.util.List;

@Mapper
public interface QwenDao {

    History getHistory(String userId, String historyCode);

    int insert(History history);

    void update(History history);

    List<String> getHistoryCodeList(String userId);
}
