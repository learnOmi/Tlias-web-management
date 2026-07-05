# Tlias Web Management

Tlias 智能学习辅助系统 - 后端服务，基于 Spring Boot 构建的企业级员工与学员管理平台。

## 项目简介

Tlias Web Management 是一个面向教育培训行业的综合管理系统，提供员工管理、部门管理、班级管理、学员管理等核心功能，并支持数据统计报表、文件上传、操作日志记录等扩展能力。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 4.0.6 |
| ORM 框架 | MyBatis | 4.0.1 |
| 数据库 | MySQL | 8.x |
| 分页插件 | PageHelper | 1.4.7 |
| 认证授权 | JWT (jjwt) | 0.9.1 |
| 文件存储 | 阿里云 OSS | 0.4.0 |
| 切面编程 | Spring AOP | 3.1.1 |
| 工具库 | Lombok | - |
| Java 版本 | JDK | 17 |

## 功能模块

### 1. 登录认证
- 用户登录与身份验证
- JWT Token 生成与校验
- Token 拦截器 / 过滤器 双重防护

### 2. 部门管理
- 部门列表查询
- 部门新增、修改、删除
- 根据 ID 查询部门详情

### 3. 员工管理
- 员工分页条件查询
- 员工新增、修改、删除（支持批量删除）
- 根据 ID 查询员工详情
- 员工工作经历管理

### 4. 班级管理
- 班级分页条件查询
- 班级新增、修改、删除
- 根据 ID 查询班级详情
- 查询全部班级列表

### 5. 学员管理
- 学员分页条件查询（支持姓名、学历、班级筛选）
- 学员新增、修改、删除（支持批量删除）
- 根据 ID 查询学员详情
- 学员违纪扣分处理

### 6. 数据统计报表
- 员工职位人数统计
- 员工性别比例统计
- 学员学历分布统计
- 班级人数统计

### 7. 文件上传
- 基于阿里云 OSS 的文件上传服务
- 支持单文件最大 10MB，单次请求最大 100MB

### 8. 操作日志
- 基于 AOP 的操作日志自动记录
- 关键操作（增删改）日志持久化存储

## 项目结构

```
src/main/java/org/example/
├── anno/              # 自定义注解
│   └── Log.java       # 操作日志注解
├── aspect/            # AOP 切面
│   └── OperationLogAspect.java
├── config/            # 配置类
│   └── WebConfig.java
├── controller/        # 控制层
│   ├── ClazzController.java
│   ├── DeptController.java
│   ├── EmpController.java
│   ├── LoginController.java
│   ├── ReportController.java
│   ├── StudentController.java
│   └── UploadController.java
├── exception/         # 异常处理
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── filter/            # 过滤器
│   └── TokenFilter.java
├── interceptor/       # 拦截器
│   └── TokenInterceptor.java
├── mapper/            # 数据访问层
│   ├── ClazzMapper.java
│   ├── DeptMapper.java
│   ├── EmpExprMapper.java
│   ├── EmpLogMapper.java
│   ├── EmpMapper.java
│   ├── OperateLogMapper.java
│   └── StudentMapper.java
├── pojo/              # 实体类
│   ├── Clazz.java
│   ├── ClazzCountOption.java
│   ├── Dept.java
│   ├── Emp.java
│   ├── EmpExpr.java
│   ├── EmpLog.java
│   ├── EmpQueryParam.java
│   ├── JobOption.java
│   ├── LoginInfo.java
│   ├── OperateLog.java
│   ├── PageResult.java
│   ├── Result.java
│   └── Student.java
├── service/           # 业务逻辑层
│   ├── impl/
│   ├── ClazzService.java
│   ├── DeptService.java
│   ├── EmpLogService.java
│   ├── EmpService.java
│   ├── ReportService.java
│   └── StudentService.java
├── utils/             # 工具类
│   ├── AliyunOSSOperator.java
│   ├── AliyunOSSProperties.java
│   ├── CurrentHolder.java
│   └── JwtUtils.java
└── TliasWebManagementApplication.java  # 启动类
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.x

### 数据库配置

1. 创建数据库 `tlias`
2. 修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tlias
    username: your_username
    password: your_password
```

### 阿里云 OSS 配置（可选）

如需使用文件上传功能，请配置阿里云 OSS 信息：

```yaml
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    bucketName: your-bucket-name
    region: cn-hangzhou
```

### 启动项目

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd Tlias-web-management

# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

服务启动后访问：`http://localhost:8080`

## API 接口概览

| 模块 | 接口路径 | 方法 | 说明 |
|------|----------|------|------|
| 登录 | `/login` | POST | 用户登录 |
| 部门 | `/depts` | GET | 查询部门列表 |
| 部门 | `/depts` | POST | 新增部门 |
| 部门 | `/depts` | DELETE | 删除部门 |
| 部门 | `/depts/{id}` | GET | 根据ID查询部门 |
| 部门 | `/depts` | PUT | 修改部门 |
| 员工 | `/emps` | GET | 分页查询员工 |
| 员工 | `/emps` | POST | 新增员工 |
| 员工 | `/emps` | DELETE | 批量删除员工 |
| 员工 | `/emps/{id}` | GET | 根据ID查询员工 |
| 员工 | `/emps` | PUT | 修改员工 |
| 班级 | `/clazzs` | GET | 分页查询班级 |
| 班级 | `/clazzs` | POST | 新增班级 |
| 班级 | `/clazzs/{id}` | DELETE | 删除班级 |
| 班级 | `/clazzs/{id}` | GET | 根据ID查询班级 |
| 班级 | `/clazzs` | PUT | 修改班级 |
| 班级 | `/clazzs/list` | GET | 查询全部班级 |
| 学员 | `/students` | GET | 分页查询学员 |
| 学员 | `/students` | POST | 新增学员 |
| 学员 | `/students/{ids}` | DELETE | 批量删除学员 |
| 学员 | `/students/{id}` | GET | 根据ID查询学员 |
| 学员 | `/students` | PUT | 修改学员 |
| 学员 | `/students/violation/{id}/{score}` | PUT | 违纪处理 |
| 报表 | `/report/empJobData` | GET | 员工职位统计 |
| 报表 | `/report/empGenderData` | GET | 员工性别统计 |
| 报表 | `/report/studentDegreeData` | GET | 学员学历统计 |
| 报表 | `/report/studentCountData` | GET | 班级人数统计 |
| 上传 | `/upload` | POST | 文件上传 |

## 统一响应格式

所有接口返回统一的 `Result` 格式：

```json
{
  "code": 1,
  "msg": "success",
  "data": {}
}
```

- `code`: 状态码，1 表示成功，0 表示失败
- `msg`: 响应消息
- `data`: 响应数据

## 开发规范

- 采用分层架构：Controller → Service → Mapper
- 使用 Lombok 简化 POJO 类代码
- 分页查询使用 PageHelper 插件
- 全局异常统一处理
- 操作日志通过 AOP 切面自动记录

## License

暂无
