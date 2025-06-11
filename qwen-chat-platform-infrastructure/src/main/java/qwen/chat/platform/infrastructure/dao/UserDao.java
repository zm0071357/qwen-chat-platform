package qwen.chat.platform.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import qwen.chat.platform.infrastructure.dao.po.User;

@Mapper
public interface UserDao {

    /**
     * 查询用户
     * @param userId
     * @return
     */
    User getUserById(String userId);

    /**
     * 插入用户
     * @param user
     * @return
     */
    int insert(User user);
}
