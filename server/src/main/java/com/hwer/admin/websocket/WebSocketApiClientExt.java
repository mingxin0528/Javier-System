package com.hwer.admin.websocket;

import com.binance.connector.client.impl.websocketapi.*;
import com.binance.connector.client.utils.websocketcallback.*;

public interface WebSocketApiClientExt {
    void connect(WebSocketMessageCallback var1);

    void connect(WebSocketOpenCallback var1, WebSocketMessageCallback var2, WebSocketClosingCallback var3, WebSocketClosedCallback var4, WebSocketFailureCallback var5);

    void close();

    WebSocketApiAccountExt account();

    WebSocketApiAuth auth();

    WebSocketApiGeneral general();

    WebSocketApiMarket market();

    WebSocketApiTrade trade();

    WebSocketApiUserDataStream userDataStream();
}
