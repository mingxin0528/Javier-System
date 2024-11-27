package com.hwer.admin.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hwer.admin.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UserDTO extends User {
    private Integer id;
    private String username;
    private String secret;
    private String selfCode;
    private String usedCode;
    private String apiKey;
    private String secretKey;
    private Integer allow;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    private String token;
}
