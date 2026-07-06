-- ============================================
-- Tlias 系统 - 全量初始化脚本
-- 版本：v1.0
-- 日期：2026-07-06
-- ============================================

-- ============================================
-- 1. 角色表
-- ============================================
CREATE TABLE IF NOT EXISTS `role` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称，如：管理员',
    `code` VARCHAR(50) UNIQUE NOT NULL COMMENT '角色标识，如：admin',
    `description` VARCHAR(200) COMMENT '角色描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================
-- 2. 权限表
-- ============================================
CREATE TABLE IF NOT EXISTS `permission` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称，如：新增员工',
    `code` VARCHAR(100) UNIQUE NOT NULL COMMENT '权限标识，如：system:emp:add',
    `type` VARCHAR(20) COMMENT '类型：menu/button/api',
    `parent_id` INT DEFAULT 0 COMMENT '父权限ID',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ============================================
-- 3. 员工-角色关联表
-- ============================================
CREATE TABLE IF NOT EXISTS `emp_role` (
    `emp_id` INT NOT NULL COMMENT '员工ID',
    `role_id` INT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`emp_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工-角色关联表';

-- ============================================
-- 4. 角色-权限关联表
-- ============================================
CREATE TABLE IF NOT EXISTS `role_permission` (
    `role_id` INT NOT NULL COMMENT '角色ID',
    `permission_id` INT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- ============================================
-- 5. 刷新令牌表
-- ============================================
CREATE TABLE IF NOT EXISTS `refresh_token` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `emp_id` INT NOT NULL COMMENT '用户ID',
    `token` VARCHAR(500) UNIQUE NOT NULL COMMENT '刷新令牌',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `revoked` TINYINT DEFAULT 0 COMMENT '是否已吊销：0-未吊销，1-已吊销',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_emp_id` (`emp_id`),
    INDEX `idx_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='刷新令牌表';

-- ============================================
-- 6. 数据字典表
-- ============================================
CREATE TABLE IF NOT EXISTS `dict_data` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID',
    `type` VARCHAR(50) NOT NULL COMMENT '字典类型，如 emp_job',
    `label` VARCHAR(100) NOT NULL COMMENT '显示标签',
    `value` VARCHAR(100) NOT NULL COMMENT '字典值',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_type_value` (`type`, `value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入角色数据
INSERT INTO `role` (`name`, `code`, `description`) VALUES
('超级管理员', 'admin', '拥有所有权限'),
('教师', 'teacher', '学生管理 + 报表查看'),
('访客', 'viewer', '只读权限');

-- 插入权限数据
INSERT INTO `permission` (`name`, `code`, `type`, `parent_id`, `sort`) VALUES
-- 部门管理权限
('部门管理', 'system:dept', 'menu', 0, 1),
('部门列表', 'system:dept:list', 'api', 1, 1),
('新增部门', 'system:dept:add', 'api', 1, 2),
('修改部门', 'system:dept:edit', 'api', 1, 3),
('删除部门', 'system:dept:delete', 'api', 1, 4),

-- 员工管理权限
('员工管理', 'system:emp', 'menu', 0, 2),
('员工列表', 'system:emp:list', 'api', 6, 1),
('新增员工', 'system:emp:add', 'api', 6, 2),
('修改员工', 'system:emp:edit', 'api', 6, 3),
('删除员工', 'system:emp:delete', 'api', 6, 4),

-- 班级管理权限
('班级管理', 'stu:clazz', 'menu', 0, 3),
('班级列表', 'stu:clazz:list', 'api', 11, 1),
('新增班级', 'stu:clazz:add', 'api', 11, 2),
('修改班级', 'stu:clazz:edit', 'api', 11, 3),
('删除班级', 'stu:clazz:delete', 'api', 11, 4),

-- 学员管理权限
('学员管理', 'stu:stu', 'menu', 0, 4),
('学员列表', 'stu:stu:list', 'api', 16, 1),
('新增学员', 'stu:stu:add', 'api', 16, 2),
('修改学员', 'stu:stu:edit', 'api', 16, 3),
('删除学员', 'stu:stu:delete', 'api', 16, 4),

-- 报表统计权限
('报表统计', 'report', 'menu', 0, 5),
('员工报表', 'report:emp:view', 'api', 21, 1),
('学员报表', 'report:stu:view', 'api', 21, 2),
('日志报表', 'report:log:view', 'api', 21, 3),

-- 全部权限（admin专用）
('全部权限', '*', 'api', 0, 999);

-- 管理员角色拥有全部权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, `id` FROM `permission` WHERE `code` = '*';

-- 教师角色权限：学生管理 + 报表查看
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 2, `id` FROM `permission` WHERE `code` IN (
    'stu:clazz:list', 'stu:clazz:add', 'stu:clazz:edit', 'stu:clazz:delete',
    'stu:stu:list', 'stu:stu:add', 'stu:stu:edit', 'stu:stu:delete',
    'report:emp:view', 'report:stu:view', 'report:log:view'
);

-- 访客角色权限：只读（查看列表和报表）
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 3, `id` FROM `permission` WHERE `code` IN (
    'system:dept:list',
    'system:emp:list',
    'stu:clazz:list',
    'stu:stu:list',
    'report:emp:view', 'report:stu:view', 'report:log:view'
);

-- 将 emp 表中 id=1 的员工（通常是admin）分配给管理员角色
INSERT IGNORE INTO `emp_role` (`emp_id`, `role_id`) VALUES (1, 1);

-- 员工职位字典
INSERT INTO `dict_data` (`type`, `label`, `value`, `sort`) VALUES
('emp_job', '班主任', '1', 1),
('emp_job', '讲师', '2', 2),
('emp_job', '学工主管', '3', 3),
('emp_job', '教研主管', '4', 4),
('emp_job', '咨询师', '5', 5);

-- 学员学历字典
INSERT INTO `dict_data` (`type`, `label`, `value`, `sort`) VALUES
('stu_degree', '大专', '1', 1),
('stu_degree', '本科', '2', 2),
('stu_degree', '硕士', '3', 3),
('stu_degree', '博士', '4', 4);

-- 性别字典
INSERT INTO `dict_data` (`type`, `label`, `value`, `sort`) VALUES
('gender', '男', '1', 1),
('gender', '女', '2', 2);

-- ============================================
-- 7. 前端日志表
-- ============================================
CREATE TABLE IF NOT EXISTS `frontend_log` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `emp_id` INT COMMENT '操作用户ID',
    `type` VARCHAR(20) NOT NULL COMMENT '日志类型：error/performance/behavior',
    `level` VARCHAR(10) COMMENT '日志级别：info/warn/error',
    `message` TEXT COMMENT '日志内容',
    `url` VARCHAR(500) COMMENT '请求URL',
    `user_agent` VARCHAR(500) COMMENT '浏览器UA',
    `operate_ip` VARCHAR(50) COMMENT '操作IP',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_type` (`type`),
    INDEX `idx_emp_id` (`emp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='前端日志表';
