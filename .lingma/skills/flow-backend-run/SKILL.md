---
name: flow-backend-run
description: 启动Flow工作流管理系统后端服务。使用Java 17和Maven运行Spring Boot应用，包含环境变量设置和跳过测试的配置。
---

# Flow后端服务启动

## 启动命令

在Windows PowerShell中启动后端服务：

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\java\jdk-17.0.9+9"; $env:Path = "$env:JAVA_HOME\bin;$env:Path"; cd d:\09-app\flow\flow-workflow\flow-admin; mvn spring-boot:run -DskipTests
```

## 命令说明

| 部分 | 说明 |
|------|------|
| `$env:JAVA_HOME = ...` | 设置Java 17的HOME路径 |
| `$env:Path = ...` | 将Java 17的bin目录添加到PATH |
| `cd d:\09-app\flow\flow-workflow\flow-admin` | 进入后端项目目录 |
| `mvn spring-boot:run -DskipTests` | 使用Maven运行Spring Boot应用，跳过测试 |

## 环境要求

- Java 17 (JDK 17.0.9)
- Maven 3.9+
- MySQL数据库已启动
- 数据库 `flow_workflow` 已创建

## 常见问题

### 1. 编译错误：无效的目标发行版
**原因**: 使用了错误的Java版本
**解决**: 确保正确设置JAVA_HOME和Path环境变量

### 2. 数据库连接失败
**原因**: MySQL未启动或数据库不存在
**解决**: 启动MySQL并创建数据库 `flow_workflow`

### 3. 权限访问被拒绝 (AccessDeniedException)
**原因**: JWT Token中不包含权限信息
**解决**: 重新登录系统获取新的Token

## 项目配置

- **Spring Boot版本**: 3.1.8
- **Java版本**: 17
- **Flowable版本**: 7.0.0
- **服务端口号**: 8080
