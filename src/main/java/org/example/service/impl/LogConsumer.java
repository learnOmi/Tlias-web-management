package org.example.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.FrontendLogMapper;
import org.example.mapper.OperateLogMapper;
import org.example.pojo.FrontendLog;
import org.example.pojo.OperateLog;
import org.example.service.LogEsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志消息消费者
 * 从 RabbitMQ 消费日志消息，批量写入 MySQL 和 ES
 */
@Slf4j
@Service
public class LogConsumer {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Autowired
    private FrontendLogMapper frontendLogMapper;

    @Autowired
    private LogEsService logEsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 操作日志缓冲区
    private final List<OperateLog> operateLogBuffer = new ArrayList<>();
    // 前端日志缓冲区
    private final List<FrontendLog> frontendLogBuffer = new ArrayList<>();

    private static final int BATCH_SIZE = 10;

    /**
     * 监听操作日志队列
     */
    @RabbitListener(queues = "operate_log_queue")
    public void handleOperateLog(String message) {
        log.debug("收到操作日志消息: {}", message);
        try {
            List<OperateLog> logs = objectMapper.readValue(message, new TypeReference<List<OperateLog>>() {});
            operateLogBuffer.addAll(logs);
            if (operateLogBuffer.size() >= BATCH_SIZE) {
                flushOperateLogs();
            }
        } catch (Exception e) {
            log.error("处理操作日志消息失败", e);
        }
    }

    /**
     * 监听前端日志队列
     */
    @RabbitListener(queues = "frontend_log_queue")
    public void handleFrontendLog(String message) {
        log.debug("收到前端日志消息: {}", message);
        try {
            List<FrontendLog> logs = objectMapper.readValue(message, new TypeReference<List<FrontendLog>>() {});
            frontendLogBuffer.addAll(logs);
            if (frontendLogBuffer.size() >= BATCH_SIZE) {
                flushFrontendLogs();
            }
        } catch (Exception e) {
            log.error("处理前端日志消息失败", e);
        }
    }

    /**
     * 刷新操作日志缓冲区（同时写 MySQL 和 ES）
     */
    public void flushOperateLogs() {
        if (operateLogBuffer.isEmpty()) return;
        List<OperateLog> flushList = new ArrayList<>(operateLogBuffer);
        operateLogBuffer.clear();

        // 写入 MySQL
        operateLogMapper.batchInsert(flushList);
        log.info("批量写入操作日志 {} 条到 MySQL", flushList.size());

        // 写入 ES
        logEsService.batchInsertOperateLog(flushList);
    }

    /**
     * 刷新前端日志缓冲区（同时写 MySQL 和 ES）
     */
    public void flushFrontendLogs() {
        if (frontendLogBuffer.isEmpty()) return;
        List<FrontendLog> flushList = new ArrayList<>(frontendLogBuffer);
        frontendLogBuffer.clear();

        // 写入 MySQL
        frontendLogMapper.batchInsert(flushList);
        log.info("批量写入前端日志 {} 条到 MySQL", flushList.size());

        // 写入 ES
        logEsService.batchInsertFrontendLog(flushList);
    }
}
