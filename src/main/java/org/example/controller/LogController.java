package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.PreAuthorize;
import org.example.pojo.FrontendLog;
import org.example.pojo.FrontendLogQueryParam;
import org.example.pojo.FrontendLogReport;
import org.example.pojo.OperateLogQueryParam;
import org.example.pojo.PageResult;
import org.example.pojo.Result;
import org.example.service.FrontendLogService;
import org.example.service.OperateLogService;
import org.example.utils.CurrentHolder;
import org.example.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 日志控制器
 * 提供前端日志上报和管理员查看日志的功能
 */
@Slf4j
@RestController
@RequestMapping("/log")
@Tag(name = "日志管理", description = "操作日志和前端日志接口")
public class LogController {

    @Autowired
    private FrontendLogService frontendLogService;

    @Autowired
    private OperateLogService operateLogService;

    /**
     * 接收前端上报的日志
     *
     * @param report 前端日志上报数据
     * @param request HTTP 请求
     * @return 处理结果
     */
    @Operation(summary = "前端日志上报")
    @PostMapping("/report")
    public Result report(@RequestBody FrontendLogReport report, HttpServletRequest request) {
        log.info("收到前端日志上报: type={}, level={}", report.getType(), report.getLevel());

        FrontendLog logEntry = new FrontendLog();
        logEntry.setEmpId(CurrentHolder.getCurrentId());
        logEntry.setType(report.getType());
        logEntry.setLevel(report.getLevel());
        logEntry.setMessage(report.getMessage());
        logEntry.setUrl(report.getUrl());
        logEntry.setUserAgent(request.getHeader("User-Agent"));
        logEntry.setOperateIp(IpUtils.getClientIp(request));

        frontendLogService.save(logEntry);
        return Result.success();
    }

    /**
     * 分页查询操作日志（管理员功能）
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Operation(summary = "分页查询操作日志")
    @PreAuthorize("report:log:view")
    @GetMapping("/operate/page")
    public Result operatePage(OperateLogQueryParam param) {
        log.info("分页查询操作日志: page={}, pageSize={}", param.getPage(), param.getPageSize());
        PageResult<org.example.pojo.OperateLog> result = operateLogService.pageByCondition(param);
        return Result.success(result);
    }

    /**
     * 分页查询前端日志（管理员功能）
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Operation(summary = "分页查询前端日志")
    @PreAuthorize("report:log:view")
    @GetMapping("/frontend/page")
    public Result frontendPage(FrontendLogQueryParam param) {
        log.info("分页查询前端日志: page={}, pageSize={}", param.getPage(), param.getPageSize());
        PageResult<FrontendLog> result = frontendLogService.pageByCondition(param);
        return Result.success(result);
    }
}
