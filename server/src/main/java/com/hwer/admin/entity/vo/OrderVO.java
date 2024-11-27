package com.hwer.admin.entity.vo;

import com.hwer.admin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    @NonNull
    private String price;
    @NonNull
    private String stopLossPrice;
    @NonNull
    private String stopEarnPrice;
    @NonNull
    private String positionSide;
    @NonNull
    private String stopLossRate;
    private UserVO user;
}
