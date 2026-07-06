package org.example.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 操作日志分页查询参数
 */
@Data
public class OperateLogQueryParam {
    private Integer page = 1;       // 页码
    private Integer pageSize = 10;  // 每页记录数
    private String className;       // 操作类名（模糊搜索）
    private String methodName;      // 操作方法名（模糊搜索）
    private String resultStatus;    // 操作结果：成功/失败
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime begin;    // 操作时间-开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;      // 操作时间-结束
}
