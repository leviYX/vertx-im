package org.im.eventbus;

public class Topic {

    // 温度数据发送事件总线的地址，其他verticle可以订阅该地址来接收温度数据
    public final static String SENSOR_UPDATE_ADDRESS = "sensor.update.address";
}
