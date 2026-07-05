-- ============================================
-- Tlias 系统 - 双 Token 机制初始化脚本
-- 版本：v1.0
-- 日期：2026-07-02
-- ============================================

-- 刷新令牌表
CREATE TABLE IF NOT EXISTS `refresh_token` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `emp_id` INT NOT NULL COMMENT '用户ID',
    `token` VARCHAR(500) UNIQUE NOT NULL COMMENT '刷新令牌',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `revoked` TINYINT DEFAULT 0 COMMENT '是否已吊销：0-未吊销，1-已吊销',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_emp_id` (`emp_id`),
    INDEX `idx_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='刷新令牌表';
