package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 前端日志实体类
 */
@Data
public class FrontendLog {
    private Integer id;           // ID
    private Integer empId;        // 操作用户ID
    private String type;          // 日志类型：error/performance/behavior
    private String level;         // 日志级别：info/warn/error
    private String message;       // 日志内容
    private String url;           // 请求URL
    private String userAgent;     // 浏览器UA
    private String operateIp;     // 操作IP
    private LocalDateTime createTime; // 创建时间
}
