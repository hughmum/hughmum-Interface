package com.mu.api.common;

import cn.hutool.crypto.digest.Digester;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.mu.api.constant.AuthConstant;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 沐
 */
@Component
public class GenerateAuthUtils {

    /*JWTSigner 接口是一个抽象接口，不能直接实例化，但可以通过其实现类来创建实例。
    在这段代码中，JWTSignerUtil.hs512(AuthConstant.key) 返回的是实现了 JWTSigner 接口的 HMACSHA512Signer 对象，
    因此可以将其赋值给 JWTSigner 类型的变量。

    HMACSHA512Signer 是 JWTSigner 接口的一个实现类，它使用 HMAC-SHA512 算法对 JWT 进行签名。
    在这个实现类中，实现了 sign() 和 verify() 两个方法来对 JWT 进行签名和验证。
    因此，将 HMACSHA512Signer 对象赋值给 JWTSigner 类型的变量是合法的。
    这样可以确保 signer 变量拥有 JWTSigner 接口的所有方法，
    可以在代码中使用 JWTSigner 接口定义的方法对 JWT 进行签名和验证。*/
    private final JWTSigner signer = JWTSignerUtil.hs512(AuthConstant.key);

    /**
     * 生成 accessKey
     *
     * @return
     */
    public String accessKey(String appId) {
        Digester digester = new Digester(AuthConstant.algorithm);
        return digester.digestHex(appId);
    }

    /**
     * 生成 secretKey
     *
     * @param userAccount
     * @return
     */
    public String secretKey(String appId,String userAccount) {
        String s = appId + userAccount;
        UUID uuid = UUID.nameUUIDFromBytes(s.getBytes());
        String uid = uuid.toString().replaceAll("-", "");
        Map<String, Object> secMap = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("uuid", uid);
            }
        };
        return JWTUtil.createToken(secMap, signer);
    }

    public String token(String appId,String userAccount, String accessKey, String secretKey) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("appId", appId);
                put("userAccount", userAccount);
                put("accessKey", accessKey);
                put("secretKey", secretKey);
            }
        };
        //生成token，并设置签名
        return JWTUtil.createToken(map, signer);
    }
}
