package com.mu.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 * @author 沐
 */
@Configuration
public class ThreadConfig {

    @Value("${order.thread.coreSize}")
    private Integer coreSize;
    @Value("${order.thread.maxSize}")
    private Integer maxSize;
    @Value("${order.thread.keepAliveTime}")
    private Integer keepAliveTime;

    /**
     * coreSize：线程池核心线程数，表示最小的工作线程数量，即线程池空闲时保留的线程数；
     * maxSize：线程池最大线程数，表示线程池中最多能同时容纳的工作线程数量；
     * keepAliveTime：线程池中空闲线程的存活时间，即当线程池中的线程数大于核心线程数时，多余的空闲线程的存活时间；
     * LinkedBlockingDeque：任务队列，当线程池中的线程数等于核心线程数时，新任务会被放入该队列中等待执行；
     * Executors.defaultThreadFactory()：线程工厂，用于创建新的线程；
     * ThreadPoolExecutor.AbortPolicy()：拒绝策略，即当任务队列和线程池都满时，新任务的处理方式，这里采用的是抛出异常的方式进行处理。
     * 这个线程池可以用于处理一些需要异步执行的任务，例如网络请求、文件读写等IO密集型任务，也可以用于一些计算密集型任务，例如大数据处理、图像处理等。通过合理地配置线程池的参数，可以提高系统的性能和并发能力。需要注意的是，线程池的参数配置需要根据具体应用场景进行调整，不同的应用场景需要不同的线程池配置。
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}