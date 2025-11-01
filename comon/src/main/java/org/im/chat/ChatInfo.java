package org.im.chat;

public class ChatInfo {

    // 1、注册 2、登录 3、聊天
    private int type;

    // 2、消息体
    private String data;

    // 3、source 聊天的来源，登录的用户ID
    private String source;

    // 4、target 聊天的目标，登录的用户ID
    private String target;

    public ChatInfo() {}

    public ChatInfo(int type, String data, String source, String target) {
        this.type = type;
        this.data = data;
        this.source = source;
        this.target = target;
    }

    public int getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }
}
