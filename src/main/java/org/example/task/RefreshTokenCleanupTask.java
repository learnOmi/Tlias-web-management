package org.example.task;

import lombok.extern.slf4j.Slf4j;
import org.example.mapper.RefreshTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：清理过期的 refreshToken 记录
 * 每天凌晨 2 点执行一次
 */
@Slf4j
@Component
public class RefreshTokenCleanupTask {

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    /**
     * 清理过期或已吊销的 refreshToken
     * 执行频率：每天凌晨 2 点
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        log.info("开始清理过期 refreshToken 记录...");

        int deleted = refreshTokenMapper.deleteExpired();

        log.info("清理完成，共删除 {} 条过期/已吊销的 refreshToken 记录", deleted);
    }
}
