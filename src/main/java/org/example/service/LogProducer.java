package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.config.RabbitMQConfig;
import org.example.pojo.FrontendLog;
import org.example.pojo.OperateLog;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 日志消息生产者
 * 将日志发送到 RabbitMQ
 */
@Slf4j
@Service
public class LogProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送操作日志到 MQ
     */
    public void sendOperateLog(OperateLog operateLog) {
        try {
            String message = objectMapper.writeValueAsString(Collections.singletonList(operateLog));
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.OPERATE_LOG_EXCHANGE,
                    RabbitMQConfig.OPERATE_LOG_ROUTING_KEY,
                    message
            );
            log.debug("发送操作日志到 MQ: {}", operateLog.getMethodName());
        } catch (JsonProcessingException e) {
            log.error("序列化操作日志失败", e);
        }
    }

    /**
     * 发送前端日志到 MQ
     */
    public void sendFrontendLog(FrontendLog frontendLog) {
        try {
            String message = objectMapper.writeValueAsString(Collections.singletonList(frontendLog));
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.FRONTEND_LOG_EXCHANGE,
                    RabbitMQConfig.FRONTEND_LOG_ROUTING_KEY,
                    message
            );
            log.debug("发送前端日志到 MQ: type={}", frontendLog.getType());
        } catch (JsonProcessingException e) {
            log.error("序列化前端日志失败", e);
        }
    }
}
