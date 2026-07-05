package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
public class Role {
    private Integer id;           // 角色ID
    private String name;          // 角色名称
    private String code;          // 角色标识
    private String description;   // 角色描述
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}
