package com.mu.api.common;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 沐
 */
@Component
public class MobileSignature {

    private static final byte[] key = "MuSekoSign-hughmum".getBytes();

    /**
     * 生成手机号签名
     * @param username
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String makeMobileSignature(String username) throws NoSuchAlgorithmException {
        String data = username + ":" + "MuSekoSign";
        MessageDigest digest;
        digest = MessageDigest.getInstance("MD5");
        return new String(Hex.encode(digest.digest(data.getBytes()))); //将摘要计算后的字节数组转换成十六进制字符串，并返回结果字符串。
    }


    /**
     * 对用户名和手机号进行加密
     * @param username
     * @param mobile
     * @return
     */
    public String makeEncryptHex(String username,String mobile){
        String data = username + ":" + mobile;
        // 构建
        AES aes = SecureUtil.aes(key);
        // 加密
        return aes.encryptHex(data);
    }

    /**
     * 对手机号进行解密
     * @param encryptHex
     * @return
     */
    public String[] decodeHex(String encryptHex){
        // 构建
        AES aes = SecureUtil.aes(key);
        // 解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        return decryptStr.split(":");
    }
}
