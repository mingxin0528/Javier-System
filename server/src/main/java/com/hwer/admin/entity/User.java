package com.hwer.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@TableName("t_user")
public class User implements UserDetails {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("role")
    private Integer role;
    @TableField("secret")
    private String secret;
    @TableField("self_code")
    private String selfCode;
    @TableField("used_code")
    private String usedCode;
    @TableField("api_key")
    private String apiKey;
    @TableField("secret_key")
    private String secretKey;
    @TableField("allow")
    private Integer allow;
    @TableField("status")
    private Integer status;
    @TableField(value = "created_time", insertStrategy = FieldStrategy.ALWAYS)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @TableField("token")
    private String token;
    @TableField("show_secret")
    private Integer showSecret;

    @TableField(exist = false)
    private List<Binance> binanceList=new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("admin"));
        auths.add(new SimpleGrantedAuthority("user"));
        return auths;
    }
}
