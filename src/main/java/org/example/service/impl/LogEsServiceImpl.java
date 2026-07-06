package org.example.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.FrontendLog;
import org.example.pojo.OperateLog;
import org.example.service.LogEsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Elasticsearch 日志写入服务实现
 */
@Slf4j
@Service
public class LogEsServiceImpl implements LogEsService {

    @Autowired
    private ElasticsearchClient esClient;

    @Override
    public void batchInsertOperateLog(List<OperateLog> logs) {
        if (logs == null || logs.isEmpty()) return;

        try {
            BulkRequest.Builder builder = new BulkRequest.Builder();
            for (OperateLog log : logs) {
                builder.operations(op -> op
                        .index(idx -> idx
                                .index("operate_log")
                                .id(String.valueOf(log.getId()))
                                .document(log)
                        )
                );
            }
            BulkResponse response = esClient.bulk(builder.build());
            if (response.errors()) {
                log.error("ES 批量写入操作日志有失败项");
                response.items().stream()
                        .filter(item -> item.error() != null)
                        .forEach(item -> log.error("ES 写入失败: {}", item.error().reason()));
            } else {
                log.debug("ES 批量写入操作日志 {} 条成功", logs.size());
            }
        } catch (IOException e) {
            log.error("ES 批量写入操作日志失败", e);
        }
    }

    @Override
    public void batchInsertFrontendLog(List<FrontendLog> logs) {
        if (logs == null || logs.isEmpty()) return;

        try {
            BulkRequest.Builder builder = new BulkRequest.Builder();
            for (FrontendLog log : logs) {
                builder.operations(op -> op
                        .index(idx -> idx
                                .index("frontend_log")
                                .id(String.valueOf(log.getId()))
                                .document(log)
                        )
                );
            }
            BulkRequest bulkRequest = builder.build();
            BulkResponse response = esClient.bulk(bulkRequest);
            if (response.errors()) {
                log.error("ES 批量写入前端日志有失败项");
            } else {
                log.debug("ES 批量写入前端日志 {} 条成功", logs.size());
            }
        } catch (IOException e) {
            log.error("ES 批量写入前端日志失败", e);
        }
    }
}
