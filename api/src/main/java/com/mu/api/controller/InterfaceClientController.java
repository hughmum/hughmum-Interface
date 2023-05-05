package com.mu.api.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import common.Exception.BusinessException;
import com.mu.api.model.dto.interfaceinfo.InterfaceInfoInvokRequest;
import com.mu.api.model.entity.Auth;
import com.mu.api.model.entity.InterfaceInfo;
import com.mu.api.model.entity.User;
import com.mu.api.model.enums.InterFaceInfoEnum;
import com.mu.api.service.AuthService;
import com.mu.api.service.InterfaceInfoService;
import com.mu.apiclient.client.ApiClient;
import com.mu.apiclient.model.Api;
import common.ErrorCode;
import common.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
public class InterfaceClientController {

    @Autowired
    private AuthService authService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * 接口在线调用
     * @param userRequestParams
     * @param request
     * @return
     */
    @PostMapping("/apiclient")
    public Object apiClient(@RequestBody InterfaceInfoInvokRequest userRequestParams, HttpServletRequest request) {
        if (userRequestParams == null || userRequestParams.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = userRequestParams.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if(oldInterfaceInfo.getStatus() != InterFaceInfoEnum.ONLINE.getValue()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口关闭");
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;
        if(currentUser == null ){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String params = userRequestParams.getUserRequestParams();
        String method = userRequestParams.getMethod();
        String url = userRequestParams.getUrl();
        if(params == null || method == null || url ==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"请求参数错误");
        }
        //new出来请求的api
        Api api = new Api();
        api.setInterfaceId(String.valueOf(id));
        api.setId(currentUser.getId());
        api.setUserAccount(currentUser.getUserAccount());
        api.setBody(params);
        api.setUrl(url);
        api.setMethod(method);

        Auth auth = authService.getOne(new QueryWrapper<Auth>()
                .eq("userid", currentUser.getId())
                .ne("status", 1));
        if (auth==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"API密钥不存在 或 API密钥已经被关闭");
        }
        ApiClient apiClient = new ApiClient(auth.getAppid(),auth.getAccesskey(),auth.getSecretkey());
        String result = apiClient.getResult(api);
        if (!url.contains("localhost")) {
            String s = JSONUtil.toJsonStr(result).replaceAll("\\\\", "");
            Object o = JSON.toJSON(s);
            return ResultUtils.success(o);
        }
        return ResultUtils.success(result);
    }
}
