package org.example.service;

import org.example.pojo.FrontendLog;
import org.example.pojo.OperateLog;

import java.util.List;

/**
 * Elasticsearch 日志写入服务
 */
public interface LogEsService {

    /**
     * 批量写入操作日志到 ES
     */
    void batchInsertOperateLog(List<OperateLog> logs);

    /**
     * 批量写入前端日志到 ES
     */
    void batchInsertFrontendLog(List<FrontendLog> logs);
}
