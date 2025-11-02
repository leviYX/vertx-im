//package org.im.handler;
//import io.vertx.core.Handler;
//import io.vertx.core.http.ServerWebSocket;
//import io.vertx.core.http.WebSocketFrame;
//import org.im.PriceBroadcast;
//import org.im.group.GroupDomin;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//public class WebSocketHandler implements Handler<ServerWebSocket> {
//
//    private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);
//
//    public static final String WEBSOCKET_PATH = "/ws/echo";
//
//    private static final String CLOSE_MSG = "please close";
//
//    private PriceBroadcast priceBroadcast;
//
//    public WebSocketHandler(String sendName) {
//        this.priceBroadcast = new PriceBroadcast(sendName);
//    }
//
//    @Override
//    public void handle(ServerWebSocket ws) {
//        String wsPath = ws.path();
//        String textHandlerID = ws.textHandlerID();
//        if (!WEBSOCKET_PATH.equalsIgnoreCase(wsPath)) {
//            LOG.info("请求的WebSocket 路径是: {},并且我们只接受 {}", wsPath,WEBSOCKET_PATH);
//            ws.writeFinalTextFrame("当前请求路径错误，服务器只接受 "+WEBSOCKET_PATH+" ,请检查你的请求路径");
//            ws.close();
//            return;
//        }
//        LOG.info("websocket {} 建立连接,请求路径为 is {}", textHandlerID, wsPath);
//        // 服务端接受websocket连接
//        ws.accept();
//        // 注册websocket帧处理器，用于处理客户端发送的消息，websocket的消息概念是帧，每个帧都有一个类型，比如文本帧、二进制帧等
//        ws.frameHandler(webSocketFrameHandler(ws));
//        // 注册websocket关闭处理器，用于处理客户端关闭连接时的事件
//        ws.endHandler( onClose -> {
//            LOG.info("WebSocket {} 关闭", textHandlerID);
//            priceBroadcast.unregister(ws);
//        });
//        ws.exceptionHandler(err -> LOG.error( "websocket is fail" ,err));
//        ws.writeTextMessage( "连接成功");
//        priceBroadcast.register(ws);
//    }
//
//    private Handler<WebSocketFrame> webSocketFrameHandler(ServerWebSocket ws) {
//        // 每一个ws都有一个textHandlerID，用于唯一标识这个ws连接，我们可以通过这个ID来区分不同的ws连接
//        String textHandlerID = ws.textHandlerID();
//        return buffer -> {
//            // 客户端发送的消息是一个文本帧，我们可以通过textData()方法来获取消息内容
//            String msg = buffer.textData();
//            if (CLOSE_MSG.equalsIgnoreCase(msg)) {
//                LOG.info("WebSocket  {} closed", textHandlerID);
//                ws.writeFinalTextFrame("WebSocket  " + textHandlerID + " 关闭");
//                ws.close();
//            } else {
//                priceBroadcast.groupChat(textHandlerID);
//                LOG.info("WebSocket {} received message: {}", textHandlerID, msg);
//                ws.writeTextMessage("WebSocket " + textHandlerID + " received message: " + msg);
//            }
//        };
//    }
//}
