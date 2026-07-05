package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResult {
    private String url;       // 文件访问地址
    private String name;      // 原始文件名
    private Long size;        // 文件大小（字节）
    private String type;      // 文件类型（image/document/avatar）
}
