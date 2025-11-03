package org.im.chat;

/**
 * 聊天信息
 *
 * @param type 1、加好友 2、普通聊天
 * @param data  消息体
 * @param from 聊天的来源，登录的用户名 username
 * @param to   聊天的目标，登录的用户名 username
 */
public record ChatInfo(int type, String data, String from, String to) {}
