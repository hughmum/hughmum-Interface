package com.mu.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 * 
 */
@Target(ElementType.METHOD) //这是元注解，表示这个注解只能用在方法上。
@Retention(RetentionPolicy.RUNTIME) //元注解，表示这个注解在运行时保留，以便于反射机制读取。
public @interface AuthCheck {

    /**
     * 有任何一个角色
     *
     * @return
     */
    String[] anyRole() default "";

    /**
     * 必须有某个角色
     *
     * @return
     */
    String mustRole() default "";

}

