package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典项封装类（仅包含 label 和 value）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictItem {
    private String label;  // 显示标签
    private String value;  // 字典值
}
