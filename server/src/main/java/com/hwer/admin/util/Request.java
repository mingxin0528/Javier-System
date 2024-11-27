package com.hwer.admin.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.hwer.admin.bean.HwerConfig;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {

    String baseUrl;
    String apiKey;
    String apiSecret;
    HwerConfig hwerConfig;
    Signature sign = new Signature();

    public Request(String baseUrl, String apiKey, String apiSecret, HwerConfig hwerConfig) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.hwerConfig = hwerConfig;
    }

    private String printResponse(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private String printError(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getErrorStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private String getTimeStamp() {
        long timestamp = System.currentTimeMillis();
        return "timestamp=" + String.valueOf(timestamp);
    }

    //concatenate query parameters
    private String joinQueryParameters(Map<String, Object> parameters) {
        StringBuilder urlPath = new StringBuilder();
        boolean isFirst = true;

        for (Map.Entry<String, Object> mapElement : parameters.entrySet()) {
            if (isFirst) {
                isFirst = false;
                urlPath.append(mapElement.getKey()).append("=").append(mapElement.getValue());
            } else {
                urlPath.append("&").append(mapElement.getKey()).append("=").append(mapElement.getValue());
            }
        }
        return urlPath.toString();
    }

    private String send(URL obj, String httpMethod) throws Exception {
        HttpURLConnection conn;
        if (null != this.hwerConfig&&this.hwerConfig.getProxy().getEnabled()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hwerConfig.getProxy().getIp(), hwerConfig.getProxy().getPort()));
            conn = (HttpURLConnection) obj.openConnection(proxy);
            if (StrUtil.isNotEmpty(hwerConfig.getProxy().getAuth())) {
                String headerKey = "Proxy-Authorization";
                String headerValue = Base64.encode(hwerConfig.getProxy().getUser() + ":" + hwerConfig.getProxy().getAuth());
                conn.setRequestProperty(headerKey, headerValue);
            }
        } else {
            conn = (HttpURLConnection) obj.openConnection();
        }
        if (httpMethod != null) {
            conn.setRequestMethod(httpMethod);
        }
        //add API_KEY to header content
        conn.setRequestProperty("X-MBX-APIKEY", apiKey);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            return printResponse(conn);
        } else {
            return printError(conn);
        }
    }

    public String sendPublicRequest(Map<String, Object> parameters, String urlPath) throws Exception {
        String queryPath = joinQueryParameters(parameters);
        URL obj = new URL(baseUrl + urlPath + "?" + queryPath);
        System.out.println("url:" + obj);

        return send(obj, null);
    }

    public String sendSignedRequest(Map<String, Object> parameters, String urlPath, String httpMethod) throws Exception {
        String queryPath = "";
        String signature = "";
        if (!parameters.isEmpty()) {
            queryPath += joinQueryParameters(parameters) + "&" + getTimeStamp();
        } else {
            queryPath += getTimeStamp();
        }
        try {
            signature = sign.getSignature(queryPath, apiSecret);
        } catch (Exception e) {
            System.out.println("Please Ensure Your Secret Key Is Set Up Correctly! " + e);
            System.exit(0);
        }
        queryPath += "&signature=" + signature;

        URL obj = new URL(baseUrl + urlPath + "?" + queryPath);
        System.out.println("url:" + obj.toString());

        return send(obj, httpMethod);
    }
}
