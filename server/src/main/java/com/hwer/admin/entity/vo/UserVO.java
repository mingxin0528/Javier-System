package com.hwer.admin.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    @NonNull
    private String username;
    @NonNull
    private String password;
    private String usedCode;
    private Integer allow;
    private String apiKey;
    private String secretKey;

    private Integer page;
    private Integer size;

}
