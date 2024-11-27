package com.hwer.admin.websocket;

import com.binance.connector.client.enums.Category;
import com.binance.connector.client.exceptions.BinanceConnectorException;
import com.binance.connector.client.impl.WebSocketApiModuleFactory;
import com.binance.connector.client.impl.websocketapi.*;
import com.binance.connector.client.utils.RequestBuilder;
import com.binance.connector.client.utils.WebSocketConnection;
import com.binance.connector.client.utils.httpclient.WebSocketApiHttpClientSingleton;
import com.binance.connector.client.utils.signaturegenerator.SignatureGenerator;
import com.binance.connector.client.utils.websocketapi.WebSocketApiRequestHandler;
import com.binance.connector.client.utils.websocketcallback.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class WebSocketApiClientExtImpl implements WebSocketApiClientExt {
    private static final OkHttpClient client = WebSocketApiHttpClientSingleton.getHttpClient();
    private final SignatureGenerator signatureGenerator;
    private final String apiKey;
    private final String baseUrl;
    private final WebSocketOpenCallback noopOpenCallback;
    private final WebSocketClosingCallback noopClosingCallback;
    private final WebSocketClosedCallback noopClosedCallback;
    private final WebSocketFailureCallback noopFailureCallback;
    private WebSocketConnection connection;
    private WebSocketApiRequestHandler requestHandler;

    public WebSocketApiClientExtImpl() {
        this("", (SignatureGenerator) null);
    }

    public WebSocketApiClientExtImpl(String baseUrl) {
        this("", (SignatureGenerator) null, baseUrl);
    }

    public WebSocketApiClientExtImpl(String apiKey, SignatureGenerator signatureGenerator) {
        this(apiKey, signatureGenerator, "wss://ws-api.binance.com:443/ws-api/v3");
    }

    public WebSocketApiClientExtImpl(String apiKey, SignatureGenerator signatureGenerator, String baseUrl) {
        this.noopOpenCallback = (response) -> {
        };
        this.noopClosingCallback = (code, reason) -> {
        };
        this.noopClosedCallback = (code, reason) -> {
        };
        this.noopFailureCallback = (throwable, response) -> {
        };
        this.apiKey = apiKey;
        this.signatureGenerator = signatureGenerator;
        this.baseUrl = baseUrl;
    }

    private void checkRequestHandler() {
        if (this.requestHandler == null) {
            throw new BinanceConnectorException("No WebSocket API connection to submit request. Please connect first.");
        }
    }

    public void connect(WebSocketMessageCallback onMessageCallback) {
        this.connect(this.noopOpenCallback, onMessageCallback, this.noopClosingCallback, this.noopClosedCallback, this.noopFailureCallback);
    }

    public void connect(WebSocketOpenCallback onOpenCallback, WebSocketMessageCallback onMessageCallback, WebSocketClosingCallback onClosingCallback, WebSocketClosedCallback onClosedCallback, WebSocketFailureCallback onFailureCallback) {
        Request request = RequestBuilder.buildWebSocketRequest(this.baseUrl);
        this.connection = new WebSocketConnection(onOpenCallback, onMessageCallback, onClosingCallback, onClosedCallback, onFailureCallback, request, client);
        this.requestHandler = new WebSocketApiRequestHandler(this.connection, this.apiKey, this.signatureGenerator);
        this.connection.connect();
    }

    public void close() {
        this.connection.close();
        client.dispatcher().executorService().shutdown();
    }

    public WebSocketApiAccountExt account() {
        this.checkRequestHandler();
        return (WebSocketApiAccountExt) WebSocketApiModuleFactory.build(Category.ACCOUNT, this.requestHandler);
    }

    public WebSocketApiAuth auth() {
        this.checkRequestHandler();
        return (WebSocketApiAuth) WebSocketApiModuleFactory.build(Category.AUTH, this.requestHandler);
    }

    public WebSocketApiGeneral general() {
        this.checkRequestHandler();
        return (WebSocketApiGeneral) WebSocketApiModuleFactory.build(Category.GENERAL, this.requestHandler);
    }

    public WebSocketApiMarket market() {
        this.checkRequestHandler();
        return (WebSocketApiMarket) WebSocketApiModuleFactory.build(Category.MARKET, this.requestHandler);
    }

    public WebSocketApiTrade trade() {
        this.checkRequestHandler();
        return (WebSocketApiTrade) WebSocketApiModuleFactory.build(Category.TRADE, this.requestHandler);
    }

    public WebSocketApiUserDataStream userDataStream() {
        this.checkRequestHandler();
        return (WebSocketApiUserDataStream) WebSocketApiModuleFactory.build(Category.USER_DATA_STREAM, this.requestHandler);
    }
}
