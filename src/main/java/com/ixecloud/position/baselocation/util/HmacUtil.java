package com.ixecloud.position.baselocation.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HmacUtil {
    /**
     * 加密生成签名
     * @param data 签名的格式
     * @param key  秘钥
     * @return byte
     */
    public static byte[] hmacSHA512(byte[] data, String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray
     *            需要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1) {
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        }

        final StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            if ((b & 0xff) < 0x10)//0~F前面不零
            {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString().toLowerCase();
    }
}
