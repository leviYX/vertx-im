package org.im.chat;

/**
 *
 * @param chatId
 * @param from
 * @param to
 * @param content
 * @param chatTime
 * @param createTime
 * @param status 1:已读 0:未读
 * @param deleteStatus 逻辑删除 1:已删除 0:未删除
 */
public record ChatRecord (
        String chatId,
        String from,
        String to,
        String content,
        String chatTime,
        String createTime,
        Integer status,
        Integer deleteStatus
){}
