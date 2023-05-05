package com.mu.api.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 沐
 * redissen 配置
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://114.55.238.110:6379");
        config.useSingleServer().setPassword(redisPassword);
        return Redisson.create(config);
    }
}
