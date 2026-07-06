package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 接口文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tlias 智能学管系统 API 文档")
                        .description("Tlias Web Management 后端接口在线文档")
                        .version("v3.1")
                        .contact(new Contact().name("Tlias Team")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuthentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("BearerAuthentication",
                                new SecurityScheme()
                                        .name("BearerAuthentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * 认证模块
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("认证管理")
                .pathsToMatch("/auth/**", "/login")
                .build();
    }

    /**
     * 部门管理
     */
    @Bean
    public GroupedOpenApi deptApi() {
        return GroupedOpenApi.builder()
                .group("部门管理")
                .pathsToMatch("/depts/**")
                .build();
    }

    /**
     * 员工管理
     */
    @Bean
    public GroupedOpenApi empApi() {
        return GroupedOpenApi.builder()
                .group("员工管理")
                .pathsToMatch("/emps/**")
                .build();
    }

    /**
     * 班级管理
     */
    @Bean
    public GroupedOpenApi clazzApi() {
        return GroupedOpenApi.builder()
                .group("班级管理")
                .pathsToMatch("/clazzs/**")
                .build();
    }

    /**
     * 学员管理
     */
    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
                .group("学员管理")
                .pathsToMatch("/students/**")
                .build();
    }

    /**
     * 报表统计
     */
    @Bean
    public GroupedOpenApi reportApi() {
        return GroupedOpenApi.builder()
                .group("报表统计")
                .pathsToMatch("/report/**")
                .build();
    }

    /**
     * 日志管理
     */
    @Bean
    public GroupedOpenApi logApi() {
        return GroupedOpenApi.builder()
                .group("日志管理")
                .pathsToMatch("/log/**")
                .build();
    }

    /**
     * 数据字典
     */
    @Bean
    public GroupedOpenApi dictApi() {
        return GroupedOpenApi.builder()
                .group("数据字典")
                .pathsToMatch("/dicts/**")
                .build();
    }

    /**
     * 文件管理
     */
    @Bean
    public GroupedOpenApi fileApi() {
        return GroupedOpenApi.builder()
                .group("文件管理")
                .pathsToMatch("/files/**")
                .build();
    }

    /**
     * 用户信息
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户信息")
                .pathsToMatch("/user/**")
                .build();
    }
}
