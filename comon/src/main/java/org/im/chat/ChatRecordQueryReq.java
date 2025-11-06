package org.im.chat;

public record ChatRecordQueryReq(
        String form,
        String to,
        String startChatTime,
        String endChatTime,
        String search
) {}
