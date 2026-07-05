package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 权限实体类
 */
@Data
public class Permission {
    private Integer id;           // 权限ID
    private String name;          // 权限名称
    private String code;          // 权限标识
    private String type;          // 类型：menu/button/api
    private Integer parentId;     // 父权限ID
    private Integer sort;         // 排序
    private LocalDateTime createTime;  // 创建时间
}
