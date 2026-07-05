package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 刷新令牌实体类
 */
@Data
public class RefreshToken {
    private Integer id;           // 主键ID
    private Integer empId;        // 用户ID
    private String token;         // 刷新令牌
    private LocalDateTime expireTime;  // 过期时间
    private Integer revoked;      // 是否已吊销：0-未吊销，1-已吊销
    private LocalDateTime createTime;  // 创建时间
}
