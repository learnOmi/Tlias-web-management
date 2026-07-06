package org.example.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置
 * 操作日志和前端日志的异步写入
 */
@Configuration
public class RabbitMQConfig {

    // ==================== 操作日志队列 ====================

    public static final String OPERATE_LOG_EXCHANGE = "operate_log_exchange";
    public static final String OPERATE_LOG_QUEUE = "operate_log_queue";
    public static final String OPERATE_LOG_ROUTING_KEY = "operate.log";

    /**
     * 操作日志 Direct 交换机
     */
    @Bean
    public DirectExchange operateLogExchange() {
        return new DirectExchange(OPERATE_LOG_EXCHANGE);
    }

    /**
     * 操作日志队列
     */
    @Bean
    public Queue operateLogQueue() {
        return QueueBuilder.durable(OPERATE_LOG_QUEUE).build();
    }

    /**
     * 操作日志队列绑定交换机
     */
    @Bean
    public Binding operateLogBinding(@Qualifier("operateLogQueue") Queue queue,
                                     @Qualifier("operateLogExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(OPERATE_LOG_ROUTING_KEY);
    }

    // ==================== 前端日志队列 ====================

    public static final String FRONTEND_LOG_EXCHANGE = "frontend_log_exchange";
    public static final String FRONTEND_LOG_QUEUE = "frontend_log_queue";
    public static final String FRONTEND_LOG_ROUTING_KEY = "frontend.log";

    /**
     * 前端日志 Direct 交换机
     */
    @Bean
    public DirectExchange frontendLogExchange() {
        return new DirectExchange(FRONTEND_LOG_EXCHANGE);
    }

    /**
     * 前端日志队列
     */
    @Bean
    public Queue frontendLogQueue() {
        return QueueBuilder.durable(FRONTEND_LOG_QUEUE).build();
    }

    /**
     * 前端日志队列绑定交换机
     */
    @Bean
    public Binding frontendLogBinding(@Qualifier("frontendLogQueue") Queue queue,
                                      @Qualifier("frontendLogExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(FRONTEND_LOG_ROUTING_KEY);
    }

    // ==================== 死信队列 ====================

    public static final String DLQ_EXCHANGE = "dlq_exchange";
    public static final String DLQ_QUEUE = "dlq_queue";
    public static final String DLQ_ROUTING_KEY = "dlq.routing.key";

    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding dlqBinding(@Qualifier("dlqQueue") Queue queue,
                              @Qualifier("dlqExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DLQ_ROUTING_KEY);
    }
}
