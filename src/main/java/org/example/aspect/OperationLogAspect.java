package org.example.aspect;

import org.example.pojo.OperateLog;
import org.example.service.LogProducer;
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
 * 拦截 @Log 注解的方法，将日志发送到 MQ 异步写入
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class OperationLogAspect {

    @Autowired
    private LogProducer logProducer;

    /**
     * 环绕通知：记录操作日志到 MQ
     */
    @Around("@annotation(org.example.anno.Log)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 执行目标方法
        Object result;
        try {
            result = joinPoint.proceed();
            // 操作成功
            saveLog(joinPoint, result, "成功", null, startTime);
        } catch (Throwable e) {
            // 操作失败
            saveLog(joinPoint, null, "失败", e.getMessage(), startTime);
            throw e;
        }

        return result;
    }

    /**
     * 构建日志并发送到 MQ
     */
    private void saveLog(ProceedingJoinPoint joinPoint, Object result,
                         String resultStatus, String errorMsg, long startTime) {
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;

        // 获取请求信息
        HttpServletRequest request = getRequest();
        String requestMethod = request != null ? request.getMethod() : "";
        String requestUrl = request != null ? request.getRequestURI() : "";
        String operateIp = request != null ? IpUtils.getClientIp(request) : "";

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

        // 发送到 MQ，不再直接写 DB
        logProducer.sendOperateLog(olog);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private Integer getCurrentUserId() {
        return CurrentHolder.getCurrentId();
    }
}
