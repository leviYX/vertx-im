package org.im.entity;

import lombok.Data;
import org.im.enums.MessageCommandType;

/**
 * 消息基类
 */
@Data
public class Message {
    String magicNumber = "fb";
    String from;
    String to;
    MessageCommandType commandType;
    String content;
}
