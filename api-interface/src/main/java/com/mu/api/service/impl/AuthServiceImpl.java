package com.mu.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mu.api.mapper.AuthMapper;
import com.mu.api.service.AuthService;
import com.mu.api.util.AuthUtils;
import com.mu.api.util.RequireAllControllerMethodsUtils;
import common.model.entity.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 沐
 * @description 针对表【auth】的数据库操作Service实现
 * @createDate 2023-01-17 10:33:59
 */
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth>
        implements AuthService {

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private RequireAllControllerMethodsUtils utils;


    @Autowired
    private ApplicationContext context;

    @Override
    public String mainRedirect(HttpServletRequest request) {
        Map<String, String> headers = authUtils.getHeaders(request);
        //验证请求参数和密钥等是否合法
        boolean isAuth = authUtils.isAuth(headers);
        if (isAuth) {
            String method = request.getMethod();

            //1、获取当前请求路径中的类名和方法
            Map<String, String> hashmap = utils.hashmap;
            String url = headers.get("url");
            //自己改造的
            String key = url.substring(url.indexOf("/api") + 4);
            key = "[" + key + "]" ;
            String res = hashmap.get(key);
            if(res == null){
                return null;
            }
            String[] split = res.split("-");
            Object body = null;
            try {
                //通过反射构造
                Class<?> forName = Class.forName(split[0]);
                Method[] methods = forName.getMethods();
                System.out.println(methods);
                //由于是object对象，所以实例化对象需要从容器中拿到
                Method classMethod = forName.getMethod(split[1], Object.class);
                //调用方法
                body = classMethod.invoke(context.getBean(forName), headers.get("body"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return String.valueOf(body);
        }
        return null;
    }
}




