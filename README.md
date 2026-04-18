# Flow Workflow - 企业级工作流管理系统

基于 Spring Boot + Flowable + Vue 3 的工作流管理系统

## 技术栈

### 后端
- Spring Boot 3.x
- Flowable 7.x (工作流引擎)
- MyBatis-Plus (ORM框架)
- Spring Security + JWT (安全认证)
- MySQL 8.x (数据库)
- Redis (缓存)

### 前端
- Vue 3
- Element Plus (UI组件库)
- Pinia (状态管理)
- Axios (HTTP客户端)
- BPMN.js (流程设计器)

## 项目结构

```
flow-workflow/
├── flow-common/              # 公共模块
│   ├── result/               # 统一返回结果
│   ├── exception/            # 全局异常处理
│   ├── utils/                # 工具类
│   └── entity/               # 基础实体类
├── flow-workflow-core/       # 工作流核心模块
│   ├── service/              # 工作流服务
│   ├── dto/                  # 数据传输对象
│   └── vo/                   # 视图对象
├── flow-admin/               # 管理后台模块
│   ├── controller/           # 控制器
│   ├── service/              # 业务服务
│   ├── entity/               # 实体类
│   ├── mapper/               # 数据访问层
│   └── security/             # 安全配置
└── flow-ui/                  # 前端Vue项目
    ├── src/
    │   ├── views/            # 页面
    │   ├── api/              # API接口
    │   ├── store/            # 状态管理
    │   └── router/           # 路由
    └── package.json
```

## 功能模块

### 基础权限模块
- [x] 部门管理（树形结构）
- [x] 用户管理
- [x] 角色管理
- [x] 菜单管理
- [x] JWT认证

### 工作流核心模块
- [x] 流程模型管理
- [x] 流程定义管理
- [x] 流程实例管理
- [x] 待办/已办任务
- [x] 任务办理（同意/拒绝/驳回）
- [x] 任务委托/转办
- [x] BPMN流程设计器

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+

### 后端启动

1. 创建数据库
```sql
CREATE DATABASE flow_workflow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改数据库配置
编辑 `flow-admin/src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/flow_workflow?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

3. 运行数据库脚本
```sql
source flow-admin/src/main/resources/db/schema.sql
```

4. 启动应用
```bash
cd flow-admin
mvn spring-boot:run
```

后端服务将启动在 http://localhost:8080

### 前端启动

```bash
cd flow-ui
npm install
npm run dev
```

前端服务将启动在 http://localhost:3000

### 默认账号
- 用户名：admin
- 密码：123456

## API文档

启动后端服务后，可以通过以下地址访问API：

- 登录接口：`POST /auth/login`
- 部门管理：`/system/dept/**`
- 用户管理：`/system/user/**`
- 角色管理：`/system/role/**`
- 流程模型：`/workflow/model/**`
- 流程实例：`/workflow/instance/**`
- 任务管理：`/workflow/task/**`

## 核心功能说明

### 1. 流程模型管理
- 创建流程模型
- 使用BPMN.js设计流程图
- 部署流程定义
- 版本管理

### 2. 流程实例管理
- 发起流程
- 查看流程进度
- 终止流程
- 删除流程

### 3. 任务管理
- 待办任务列表
- 已办任务列表
- 任务办理（同意/拒绝）
- 任务委托
- 任务转办
- 任务驳回

### 4. 权限控制
- 基于角色的权限控制
- 基于权限标识的细粒度控制
- JWT Token认证

## 数据库表结构

### 系统表
- sys_user - 用户表
- sys_dept - 部门表
- sys_role - 角色表
- sys_menu - 菜单表
- sys_user_role - 用户角色关联表
- sys_role_menu - 角色菜单关联表

### 工作流扩展表
- bpm_model - 流程模型扩展表
- bpm_form - 流程表单表
- bpm_process_instance_ext - 流程实例扩展表
- bpm_task_ext - 任务扩展表

### Flowable系统表
- ACT_RE_* - 资源库表
- ACT_RU_* - 运行时表
- ACT_HI_* - 历史数据表
- ACT_ID_* - 身份认证表

## 开发计划

- [x] 基础框架搭建
- [x] 用户/部门/角色管理
- [x] 流程模型管理
- [x] 流程实例管理
- [x] 任务管理
- [x] BPMN流程设计器
- [ ] 动态表单设计器
- [ ] 会签功能
- [ ] 流程监控
- [ ] 报表统计

## 许可证

MIT License
