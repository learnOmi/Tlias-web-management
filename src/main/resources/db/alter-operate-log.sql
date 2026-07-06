-- ============================================
-- Tlias 系统 - 操作日志表结构增强脚本
-- 版本：v1.0
-- 日期：2026-07-06
-- ============================================

-- 新增操作日志字段（不加 AFTER，MySQL 默认追加到末尾）
ALTER TABLE `operate_log`
  ADD COLUMN `operate_ip` VARCHAR(50) COMMENT '操作IP',
  ADD COLUMN `request_method` VARCHAR(10) COMMENT '请求方法',
  ADD COLUMN `request_url` VARCHAR(255) COMMENT '请求URL',
  ADD COLUMN `result_status` VARCHAR(20) COMMENT '操作结果：成功/失败',
  ADD COLUMN `error_msg` TEXT COMMENT '错误信息';
