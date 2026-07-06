package org.example.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 前端日志分页查询参数
 */
@Data
public class FrontendLogQueryParam {
    private Integer page = 1;       // 页码
    private Integer pageSize = 10;  // 每页记录数
    private String type;            // 日志类型：error/performance/behavior
    private String level;           // 日志级别：info/warn/error
    private Integer empId;          // 用户ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime begin;    // 创建时间-开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;      // 创建时间-结束
}
