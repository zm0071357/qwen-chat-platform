package qwen.chat.platform.types.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Component;

@Component
public class AgronUtils {
    /**
     * Argon2 进行密码加密
     * @param password
     * @return
     */
    public String hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon2.hash(10, 65536, 1, password.toCharArray());
    }

    /**
     * 验证密码
     * @param hash
     * @param password
     * @return
     */
    public boolean verifyPassword(String hash, String password) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon2.verify(hash, password.toCharArray());
    }
}
