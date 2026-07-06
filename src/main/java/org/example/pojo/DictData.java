package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据字典实体类
 */
@Data
public class DictData {
    private Integer id;              // 字典ID
    private String type;             // 字典类型，如 emp_job
    private String label;            // 显示标签
    private String value;            // 字典值
    private Integer sort;            // 排序
    private LocalDateTime createTime; // 创建时间
}
