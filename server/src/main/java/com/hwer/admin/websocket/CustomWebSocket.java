package com.hwer.admin.websocket;

import com.hwer.admin.service.impl.UserServiceImpl;
import com.hwer.admin.util.URLConstant;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class CustomWebSocket extends WebSocketClient {
    private final UserServiceImpl.MsgCallback callback;

    public CustomWebSocket(UserServiceImpl.MsgCallback callback) {
        super(URI.create(URLConstant.WS_API_URL));
        this.callback = callback;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("================ open =================");
    }

    @Override
    public void onMessage(String s) {
        System.out.println("================ message =============");
        callback.onMessage(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("================ close =============");

    }

    @Override
    public void onError(Exception e) {
        System.out.println("================ error =============");
        e.printStackTrace();
    }
}
