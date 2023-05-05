package com.mu.api.aop;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.mu.api.annotation.AuthCheck;
import common.Exception.BusinessException;
import com.mu.api.model.entity.User;
import com.mu.api.service.UserService;
import common.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 权限校验 AOP
 *
 * 
 */
@Aspect  //标记注解，表示这个类是一个切面，用于定义拦截器的逻辑。
@Component //标记注解，表示这个类是一个 Spring 组件，可以被 Spring 扫描并自动装配。
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 表示连接点（即被拦截的方法）
     * @param authCheck authCheck 表示被拦截方法上的 @AuthCheck 注解
     * @return
     */
    @Around("@annotation(authCheck)")  //这是一个环绕通知，用于拦截带有 @AuthCheck 注解的方法，并执行权限校验逻辑。
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        //这里将 anyRole 数组转为列表，同时过滤掉空字符串。
        List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());

        String mustRole = authCheck.mustRole();
        /*在 Spring 中，RequestContextHolder 类提供了一种访问当前请求的 RequestAttributes 对象的方式。
        这个对象可以用于获取当前请求的信息，例如请求参数、请求头、Session 等等。
        而 ServletRequestAttributes 是 RequestAttributes 的实现类，
        它提供了从 HttpServletRequest 中获取信息的方法。
        因此，通过这两行代码，我们可以获取到当前请求的 HttpServletRequest 对象，
        从而获取到当前请求中的所有信息。这个对象在进行权限校验时非常有用，
        因为我们可以从中获取当前请求的登录用户等信息，并进行相应的权限判断。*/
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User user = userService.getLoginUser(request);
        // 拥有任意权限即通过
        if (CollectionUtils.isNotEmpty(anyRole)) {
            String userRole = user.getUserRole();
            if (!anyRole.contains(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 必须有所有权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            String userRole = user.getUserRole();
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

