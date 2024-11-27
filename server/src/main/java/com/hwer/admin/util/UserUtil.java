package com.hwer.admin.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONUtil;
import com.hwer.admin.entity.Income;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserUtil {
    private static final String IV = "hwerPasswordHwer";
    private static final AES AES = new AES(Mode.CTS, Padding.PKCS5Padding, IV.getBytes(StandardCharsets.UTF_8), IV.getBytes(StandardCharsets.UTF_8));

    public static String encode(String plain) {
        byte[] encrypt = AES.encrypt(plain);
        return ArrayUtil.join(encrypt, ",");
    }

    public static String decode(String pwd) {
        String[] split = pwd.split(",");
        byte[] bytes = new byte[split.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = Byte.parseByte(split[i]);
        }
        return AES.decryptStr(bytes);
    }
}
