package org.example.controller;

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
public class UploadController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @PreAuthorize("system:file:upload")
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "type", required = false) String type) throws Exception {
        log.info("文件上传: {}, type: {}", file.getOriginalFilename(), type);

        FileUploadResult result = aliyunOSSOperator.upload(file, type);
        log.info("文件上传OSS成功, url: {}", result.getUrl());

        return Result.success(result);
    }

    @PreAuthorize("system:file:delete")
    @DeleteMapping
    public Result delete(@RequestParam("url") String url) throws Exception {
        log.info("文件删除: {}", url);

        aliyunOSSOperator.delete(url);

        return Result.success();
    }
}
