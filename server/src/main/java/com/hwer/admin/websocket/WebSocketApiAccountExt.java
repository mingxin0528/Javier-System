package com.hwer.admin.websocket;

import com.binance.connector.client.impl.websocketapi.WebSocketApiAccount;
import com.binance.connector.client.impl.websocketapi.WebSocketApiModule;
import com.binance.connector.client.utils.JSONParser;
import com.binance.connector.client.utils.ParameterChecker;
import com.binance.connector.client.utils.websocketapi.WebSocketApiRequestHandler;
import org.json.JSONObject;

public class WebSocketApiAccountExt implements WebSocketApiModule {
    private WebSocketApiRequestHandler handler;

    public WebSocketApiAccountExt(WebSocketApiRequestHandler handler) {
        this.handler = handler;
    }

    public void accountStatus(JSONObject parameters) {
        this.handler.signedRequest("account.status", parameters);
    }

    public void accountBalance(JSONObject parameters) {
        this.handler.signedRequest("account.balance", parameters);
    }

    public void accountRateLimitsOrders(JSONObject parameters) {
        this.handler.signedRequest("account.rateLimits.orders", parameters);
    }

    public void accountAllOrders(String symbol, JSONObject parameters) {
        ParameterChecker.checkParameterType(symbol, String.class, "symbol");
        parameters = JSONParser.addKeyValue(parameters, "symbol", symbol);
        this.handler.signedRequest("allOrders", parameters);
    }

    public void accountAllOcoOrders(JSONObject parameters) {
        this.handler.signedRequest("allOrderLists", parameters);
    }

    public void accountTradeHistory(String symbol, JSONObject parameters) {
        ParameterChecker.checkParameterType(symbol, String.class, "symbol");
        parameters = JSONParser.addKeyValue(parameters, "symbol", symbol);
        this.handler.signedRequest("myTrades", parameters);
    }

    public void accountPreventedMatches(String symbol, JSONObject parameters) {
        ParameterChecker.checkParameterType(symbol, String.class, "symbol");
        ParameterChecker.checkOneOfParametersRequired(parameters, new String[]{"preventedMatchId", "orderId"});
        ParameterChecker.checkOnlyOneOfParameters(parameters, new String[]{"preventedMatchId", "orderId"});
        parameters = JSONParser.addKeyValue(parameters, "symbol", symbol);
        this.handler.signedRequest("myPreventedMatches", parameters);
    }

    public void accountAllocations(String symbol, JSONObject parameters) {
        ParameterChecker.checkParameterType(symbol, String.class, "symbol");
        parameters = JSONParser.addKeyValue(parameters, "symbol", symbol);
        this.handler.signedRequest("myAllocations", parameters);
    }

    public void accountCommissionRates(String symbol, JSONObject parameters) {
        ParameterChecker.checkParameterType(symbol, String.class, "symbol");
        parameters = JSONParser.addKeyValue(parameters, "symbol", symbol);
        this.handler.signedRequest("account.commission", parameters);
    }
}
