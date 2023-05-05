package com.mu.api.constant;


import cn.hutool.crypto.digest.DigestAlgorithm;

/**
 * 接口验证信息
 * 这段代码用于定义应用程序中进行身份验证和授权时需要使用的常量。这些常量可以在整个应用程序中使用，保证了在不同的地方对于加密和解密密钥以及消息摘要算法的使用是一致的
 */
public interface AuthConstant {
    byte[] key = "muApiAuth".getBytes();

    DigestAlgorithm algorithm = DigestAlgorithm.SHA512;
}
