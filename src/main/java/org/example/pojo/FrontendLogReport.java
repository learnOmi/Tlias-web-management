package org.example.pojo;

import lombok.Data;

/**
 * 前端日志上报请求体
 */
@Data
public class FrontendLogReport {
    private String type;          // 日志类型：error/performance/behavior
    private String level;         // 日志级别：info/warn/error
    private String message;       // 日志内容
    private String url;           // 请求URL
}
