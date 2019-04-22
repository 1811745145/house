package com.jk.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitCon {
    @Bean
    public Queue queueMap(){
        return new Queue("hous");
    }
    @Bean
    public Queue housbean(){
        return new Queue("housbean");
    }
}
