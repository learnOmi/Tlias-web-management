package org.example.aspect;

import org.example.mapper.OperateLogMapper;
import org.example.pojo.OperateLog;
import org.example.utils.CurrentHolder;
import org.example.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.core.annotation.Order;

/**
 * 操作日志切面
 * 拦截 @Log 注解的方法，记录操作日志到数据库
 */
@Slf4j
@Aspect
@Component
@Order(2) // 权限切面优先于日志切面执行
public class OperationLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 环绕通知：记录操作日志
     */
    @Around("@annotation(org.example.anno.Log)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取 Servlet 请求信息
        HttpServletRequest request = getRequest();
        String requestMethod = request != null ? request.getMethod() : "";
        String requestUrl = request != null ? request.getRequestURI() : "";
        String operateIp = request != null ? IpUtils.getClientIp(request) : "";

        Object result;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            // 操作成功
            saveLog(joinPoint, result, requestMethod, requestUrl, operateIp, "成功", null, startTime);
        } catch (Throwable e) {
            // 操作失败
            saveLog(joinPoint, null, requestMethod, requestUrl, operateIp, "失败", e.getMessage(), startTime);
            throw e;
        }

        return result;
    }

    /**
     * 保存操作日志
     */
    private void saveLog(ProceedingJoinPoint joinPoint, Object result,
                         String requestMethod, String requestUrl,
                         String operateIp, String resultStatus,
                         String errorMsg, long startTime) {
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;

        OperateLog olog = new OperateLog();
        olog.setOperateEmpId(getCurrentUserId());
        olog.setOperateIp(operateIp);
        olog.setOperateTime(LocalDateTime.now());
        olog.setClassName(joinPoint.getTarget().getClass().getName());
        olog.setMethodName(joinPoint.getSignature().getName());
        olog.setRequestMethod(requestMethod);
        olog.setRequestUrl(requestUrl);
        olog.setMethodParams(Arrays.toString(joinPoint.getArgs()));
        olog.setReturnValue(result != null ? result.toString() : "void");
        olog.setResultStatus(resultStatus);
        olog.setErrorMsg(errorMsg);
        olog.setCostTime(costTime);

        log.info("记录操作日志: {}", olog);
        operateLogMapper.insert(olog);
    }

    /**
     * 获取当前请求
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId() {
        return CurrentHolder.getCurrentId();
    }
}
