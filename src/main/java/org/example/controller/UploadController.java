package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.PreAuthorize;
import org.example.pojo.FileUploadResult;
import org.example.pojo.Result;
import org.example.utils.AliyunOSSOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@Slf4j
@Tag(name = "文件管理", description = "文件上传删除接口")
public class UploadController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    /**
     * 上传文件
     */
    @Operation(summary = "上传文件")
    @PreAuthorize("system:file:upload")
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "type", required = false) String type) throws Exception {
        log.info("文件上传: {}, type: {}", file.getOriginalFilename(), type);

        FileUploadResult result = aliyunOSSOperator.upload(file, type);
        log.info("文件上传OSS成功, url: {}", result.getUrl());

        return Result.success(result);
    }

    /**
     * 删除文件
     */
    @Operation(summary = "删除文件")
    @PreAuthorize("system:file:delete")
    @DeleteMapping
    public Result delete(@RequestParam("url") String url) throws Exception {
        log.info("文件删除: {}", url);

        aliyunOSSOperator.delete(url);

        return Result.success();
    }
}
