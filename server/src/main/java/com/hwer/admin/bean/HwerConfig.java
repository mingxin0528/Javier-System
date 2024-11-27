package com.hwer.admin.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "hwer.config")
public class HwerConfig {
    private String fileDir;
    private Proxy proxy;
    private Api api;

    @Data
    public static class Proxy {
        public Boolean enabled;
        public String ip;
        public Integer port;
        public String user;
        public String auth;
    }

    @Data
    public static class Api {
        public List<String> baseUrls;
    }
}
