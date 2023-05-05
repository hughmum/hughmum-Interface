package com.mu.api.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 沐
 * 通过手机号登录
 */
@Data
public class UserLoginBySmsRequest implements Serializable {
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证码
     */
    private String code;
}
