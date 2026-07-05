package org.example.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.OSSClientBuilder;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.DeleteObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectResult;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.FileUploadResult;
import org.example.utils.AliyunOSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AliyunOSSOperator {

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    // 允许的图片扩展名
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
    
    // 允许的文档扩展名
    private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(".pdf", ".doc", ".docx", ".xls", ".xlsx");
    
    // 图片最大大小：5MB
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024L;
    
    // 文档最大大小：20MB
    private static final long MAX_DOCUMENT_SIZE = 20 * 1024 * 1024L;

    /**
     * 上传文件到阿里云OSS
     *
     * @param file 上传的文件
     * @return 文件的访问URL
     */
    public FileUploadResult upload(MultipartFile file) {
        return upload(file, null);
    }

    /**
     * 上传文件到阿里云OSS（带类型分类）
     *
     * @param file 上传的文件
     * @param type 文件类型：image/document/avatar
     * @return 文件的访问URL
     */
    public FileUploadResult upload(MultipartFile file, String type) {
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                : "";

        // 校验文件类型
        validateFileType(extension, type);

        // 校验文件大小
        validateFileSize(file, type);

        // 生成唯一的文件名,避免重名覆盖
        String dir = getTypeDir(type);
        String fileName = dir + '/' + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"))
                + '/' + UUID.randomUUID() + extension;

        // 创建凭证提供者
        CredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();

        // 创建OSSClientBuilder
        OSSClientBuilder clientBuilder = OSSClient.newBuilder()
                .credentialsProvider(credentialsProvider)
                .region(aliyunOSSProperties.getRegion());

        if (aliyunOSSProperties.getEndpoint() != null) {
            clientBuilder.endpoint(aliyunOSSProperties.getEndpoint());
        }

        try (OSSClient client = clientBuilder.build();
             InputStream inputStream = file.getInputStream()) {

            // 上传文件
            client.putObject(PutObjectRequest.newBuilder()
                            .bucket(aliyunOSSProperties.getBucketName())
                            .key(fileName)
                            .body(BinaryData.fromStream(inputStream, file.getSize()))
                            .build());

            // 构建文件访问URL
            String url = "https://" + aliyunOSSProperties.getBucketName() + "."
                    + aliyunOSSProperties.getEndpoint() + "/" + fileName;

            log.info("文件上传成功, URL: {}", url);

            // 返回结构化结果
            return new FileUploadResult(url, originalFilename, file.getSize(), type != null ? type : "other");

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除OSS文件
     *
     * @param url 文件访问URL
     */
    public void delete(String url) {
        if (url == null || url.isEmpty()) {
            throw new RuntimeException("文件URL不能为空");
        }

        // 从URL中提取文件key
        String bucketName = aliyunOSSProperties.getBucketName();
        String key = url.replace("https://" + bucketName + ".", "")
                        .replace("https://" + bucketName + "/", "");

        // 创建凭证提供者
        CredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();

        OSSClientBuilder clientBuilder = OSSClient.newBuilder()
                .credentialsProvider(credentialsProvider)
                .region(aliyunOSSProperties.getRegion());

        if (aliyunOSSProperties.getEndpoint() != null) {
            clientBuilder.endpoint(aliyunOSSProperties.getEndpoint());
        }

        try (OSSClient client = clientBuilder.build()) {
            client.deleteObject(DeleteObjectRequest.newBuilder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            log.info("文件删除成功, key: {}", key);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }

    /**
     * 校验文件类型
     */
    private void validateFileType(String extension, String type) {
        if (extension.isEmpty()) {
            throw new RuntimeException("不支持的文件类型");
        }

        // 根据类型校验
        if ("image".equals(type) || "avatar".equals(type)) {
            if (!IMAGE_EXTENSIONS.contains(extension)) {
                throw new RuntimeException("只支持上传图片类型: " + String.join(", ", IMAGE_EXTENSIONS));
            }
        } else if ("document".equals(type)) {
            if (!DOCUMENT_EXTENSIONS.contains(extension)) {
                throw new RuntimeException("只支持文档类型: " + String.join(", ", DOCUMENT_EXTENSIONS));
            }
        } else {
            // 未指定类型，允许所有常见格式
            List<String> allExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif",
                    ".pdf", ".doc", ".docx", ".xls", ".xlsx");
            if (!allExtensions.contains(extension)) {
                throw new RuntimeException("不支持的文件类型，支持的类型: " + String.join(", ", allExtensions));
            }
        }
    }

    /**
     * 校验文件大小
     */
    private void validateFileSize(MultipartFile file, String type) {
        long fileSize = file.getSize();
        long maxSize;

        if ("image".equals(type) || "avatar".equals(type)) {
            maxSize = MAX_IMAGE_SIZE;
        } else {
            maxSize = MAX_DOCUMENT_SIZE;
        }

        if (fileSize > maxSize) {
            throw new RuntimeException("文件大小超过限制，最大支持: " + maxSize / 1024 / 1024 + "MB");
        }
    }

    /**
     * 获取类型目录
     */
    private String getTypeDir(String type) {
        if ("avatar".equals(type)) {
            return "avatar";
        } else if ("image".equals(type)) {
            return "images";
        } else if ("document".equals(type)) {
            return "documents";
        }
        return "files";
    }
}
