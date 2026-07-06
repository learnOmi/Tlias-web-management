package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
public class OperateLog {
    private Integer id;              // ID
    private Integer operateEmpId;    // 操作人ID
    private String operateIp;        // 操作IP
    private LocalDateTime operateTime; // 操作时间
    private String className;        // 操作类名
    private String methodName;       // 操作方法名
    private String requestMethod;    // 请求方法（GET/POST/PUT/DELETE）
    private String requestUrl;       // 请求URL
    private String methodParams;     // 操作方法参数
    private String returnValue;      // 操作方法返回值
    private String resultStatus;     // 操作结果：成功/失败
    private String errorMsg;         // 错误信息
    private Long costTime;           // 操作耗时（毫秒）
}
