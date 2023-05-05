package com.mu.api.config;

import com.mu.api.common.SimpleAccessDeniedHandler;
import com.mu.api.common.SimpleAuthenticationEntryPoint;
import com.mu.api.common.UserDetailsImpl;
import common.constant.CookieConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * @author 沐
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private String[] pathPatterns = {"/user/oauth2/**", "/user/register","/user/login","/user/loginBySms","/v3/api-docs","/user/logoutSuccess","/user/getpassusertype","/user/sendPassUserCode","/user/authPassUserCode","/user/updateUserPass"};

    private String[] adminPath = {"/user/list/page",
            "/user/list",
            "/userInterfaceInfo/add",
            "/userInterfaceInfo/delete",
            "/userInterfaceInfo/update",
            "/userInterfaceInfo/get",
            "/userInterfaceInfo/list",
            "/userInterfaceInfo/list/page",
            "/interfaceInfo/list",
            "/interfaceInfo/list/AllPage",
            "/interfaceInfo/online",
            "/interfaceInfo/online",};

    @Autowired
    private SimpleAuthenticationEntryPoint simpleAuthenticationEntryPoint;

    @Autowired
    private SimpleAccessDeniedHandler simpleAccessDeniedHandler;

    @Autowired
    @Lazy
    private UserDetailsImpl userDetails;

    /**
     * 配置PasswordEncoder
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 防止session 清理用户不及时
     * @return
     */
    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    /**
     * 让admin继承user的所有权限
     * @return
     * 。RoleHierarchy是Spring Security提供的一个接口，用于定义角色之间的继承关系，从而可以在授权时使用继承关系简化授权配置。具体的实现是使用了RoleHierarchyImpl类，该类是RoleHierarchy接口的一个实现类，实现了角色的继承关系。
     *在这段代码中，首先使用@Bean注解将RoleHierarchy对象注入到Spring容器中。在roleHierarchy()方法中，创建了一个RoleHierarchyImpl对象，并设置了角色继承关系。具体来说，调用了roleHierarchy.setHierarchy(hierarchy)方法，传入一个字符串hierarchy，该字符串定义了角色之间的继承关系，其中>符号表示继承关系，例如ROLE_admin > ROLE_user表示ROLE_admin角色继承了ROLE_user角色。这样，在授权时，如果一个用户具有ROLE_admin角色，则也会自动具有ROLE_user角色的权限。
     */
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_admin > ROLE_user";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }


    /**
     * 放行静态资源
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //所需要用到的静态资源，允许访问
        web.ignoring().antMatchers( "/swagger-ui.html",
                "/swagger-ui/*",
                "/swagger-resources/**",
                "/v2/api-docs",
                "/v3/api-docs",
                "/webjars/**",
                "/doc.html");
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //允许跨域
                .cors()
                .and()
                //关闭csrf
                .csrf().disable()
                .authorizeRequests()
                // 管理员才可访问的接口
                .antMatchers(adminPath).hasRole("admin")
                // 对于登录接口 允许匿名访问.anonymous()，即未登陆时可以访问，登陆后携带了token就不能再访问了
                .antMatchers(pathPatterns).anonymous()
                .antMatchers("/userInterfaceInfo/updateUserLeftNum","/user/checkUserLogin","/user/getCaptcha","/user/captcha","/charging/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证,.authenticated()表示认证之后可以访问
                .anyRequest().authenticated();
        //注册自定义异常响应
        http.exceptionHandling()
                .accessDeniedHandler(simpleAccessDeniedHandler)
                .authenticationEntryPoint(simpleAuthenticationEntryPoint);
        //开启配置注销登录功能
        http.logout()
                .logoutUrl("/user/logout") //指定用户注销登录时请求访问的地址
                .deleteCookies(CookieConstant.headAuthorization)//指定用户注销登录后删除的 Cookie。
                .deleteCookies(CookieConstant.autoLoginAuthCheck)
                .logoutSuccessUrl("http://localhost:88/api/user/logoutSuccess");//指定退出登录后跳转的地址
        //每个浏览器最多同时只能登录1个用户
        http.sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }
}
