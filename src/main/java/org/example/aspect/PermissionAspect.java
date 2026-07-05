package org.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.example.anno.PreAuthorize;
import org.example.exception.BusinessException;
import org.example.utils.PermissionHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import org.springframework.core.annotation.Order;

/**
 * 权限校验切面
 * 在方法执行前检查当前用户是否拥有指定权限
 * 使用 @Order 确保在操作日志切面之前执行
 */
@Slf4j
@Aspect
@Component
@Order(1) // 权限切面优先于日志切面执行
public class PermissionAspect {

    /**
     * 权限校验切面
     * 拦截带有 @PreAuthorize 注解的方法
     */
    @Around("@annotation(org.example.anno.PreAuthorize)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的 @PreAuthorize 注解
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        if (preAuthorize == null) {
            // 没有注解，直接放行
            return joinPoint.proceed();
        }

        // 获取需要的权限代码
        String requiredPermission = preAuthorize.value();
        log.debug("权限校验: 需要权限 [{}]", requiredPermission);

        // 检查当前用户是否拥有该权限
        if (!PermissionHolder.hasPermission(requiredPermission)) {
            log.warn("权限不足: 用户缺少权限 [{}]", requiredPermission);
            throw new BusinessException("权限不足，无法访问");
        }

        // 权限足够，执行目标方法
        log.debug("权限校验通过");
        return joinPoint.proceed();
    }
}
