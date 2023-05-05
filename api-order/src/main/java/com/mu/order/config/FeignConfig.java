package com.mu.order.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 解决feign远程调用丢失请求头的问题
 * 这段代码是一个Feign的配置类，其中定义了一个名为"requestInterceptor"的Bean。这个Bean的作用是在使用Feign进行服务调用时，拦截请求并进行一些操作。具体来说，它会获取当前请求的请求头中的cookie信息，并将其设置到Feign请求的请求头中，以保证在服务调用过程中cookie信息的一致性。这样做的好处是可以在分布式系统中保持会话的一致性，确保用户的操作可以在整个系统中被正确地追踪和记录。
 * @author 沐
 */
@Configuration
public class FeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        RequestInterceptor requestInterceptor = template -> {
            //1、使用RequestContextHolder拿到刚进来的请求数据
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                //老请求
                HttpServletRequest request = requestAttributes.getRequest();
                if (request != null) {
                    //2、同步请求头的数据
                    //把老请求的cookie值放到新请求上来，进行一个同步
                    String cookie = request.getHeader("Cookie");
                    template.header("Cookie", cookie);
                }
            }
        };
        return requestInterceptor;
    }
}
