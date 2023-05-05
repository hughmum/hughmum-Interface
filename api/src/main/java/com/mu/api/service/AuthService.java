package com.mu.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mu.api.model.entity.Auth;
import com.mu.api.model.vo.AuthVo;
import common.BaseResponse;

import javax.servlet.http.HttpServletRequest;

/**
* @author 沐
* @description 针对表【auth】的数据库操作Service
* @createDate 2023-02-01 15:24:16
*/
public interface AuthService extends IService<Auth> {

    /**
     * 根据用户id获取用户的密钥
     * @param id
     * @param request
     * @return
     */
    BaseResponse<AuthVo> getAuthByUserId(Long id, HttpServletRequest request);

    /**
     * 修改API密钥的状态
     * @param id
     * @param request
     * @return
     */
    BaseResponse updateAuthStatus(Long id, HttpServletRequest request);
}
