package org.im.enums;

import lombok.Getter;

/**
 * 客户端/服务端之间消息命令类型枚举
 */
@Getter
public enum MessageCommandType {

    LOGIN(1,"login","登录"),
    ADD_FRIEND(2, "addFriend","加好友"),
    SEND_MESSAGE(3, "sendMessage","发消息"),
    DELETE_FRIEND(4, "deleteFriend","删除好友"),
    BLOCK_FRIEND(5, "blockFriend","拉黑好友");

    private final Integer commandType;
    private final String command;
    private final String desc;

    MessageCommandType(Integer commandType,String command, String desc) {
        this.commandType = commandType;
        this.command = command;
        this.desc = desc;
    }

    /**
     * 根据数值查找对应枚举，找不到返回 null
     */
    public static MessageCommandType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (MessageCommandType value : values()) {
            if (value.commandType.equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据数值查找对应描述，找不到返回空串
     */
    public static String getDescByCode(Integer code) {
        MessageCommandType type = of(code);
        return type == null ? "" : type.desc;
    }
}