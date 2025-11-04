package org.im.constant;

public class RedisConstant {

    private RedisConstant() {
        throw new UnsupportedOperationException("禁止实例化，使用全局单一实例");
    }

    /**
     * 用户注册redis key的前缀
     */
    public static final String USER_REGISTER_INFO = "user:register:";


    /**
     * 用户好友前缀
     */
    public static final String USER_FRIENDS_SET = "user:friends:";
}
