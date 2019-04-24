package com.jk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync //开启异步执行注解
public class SpringThreadPool {

    public TaskExecutor createThreadPool(){
        //创建一个spring的线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置核心线程池 线程数量 当任务数小于核心线程数(CorePoolSize)时,
        // 线程接收到新任务之后永远创建新线程执行
        executor.setCorePoolSize(100);
        //缓冲队列 当任务数大于等于核心线程数时，新任务存入缓冲队列中，缓冲队列中的任务并不会立即创建线程
        //等核心线程有空闲线程时，把任务放入核心线程执行
        executor.setQueueCapacity(888888);
        //当任务数量大于等于缓冲队列时 新任务存入最大线程数中 最大线程数也会立即创建线程
        executor.setMaxPoolSize(150);
        //KeepAlive 空闲线程存活时间 当线程完成任务60秒之后 关闭该线程
        executor.setKeepAliveSeconds(60);
        //核心线程 空闲超时后 也会关闭
        executor.setAllowCoreThreadTimeOut(true);
        //当线程达到最大处理能力时 解决办法 如果不设置默认抛异常
        //CallerRunsPolicy 当线程达到最大处理能力时 使用主线程处理任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return  executor;
    }
}
