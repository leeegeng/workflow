# 请假流程 Demo 使用说明

## 一、流程概述

这是一个简单的请假审批流程，根据请假天数自动路由到不同的审批人：

```
开始 → 提交申请 → 判断天数 → [≤3天] → 部门经理审批 → 审批结果 → [批准] → 人事备案 → 结束
                              [>3天] → 总经理审批 ───────┘
                                                  [拒绝] → 结束
```

### 流程特点
- **自动路由**：根据请假天数自动选择审批人
- **条件分支**：≤3天部门经理审批，>3天需总经理审批
- **审批结果**：支持批准和拒绝两种结果
- **人事备案**：审批通过后自动流转到人事部门备案

## 二、文件说明

### 1. BPMN流程定义
**文件**: `flow-admin/src/main/resources/processes/leave-simple.bpmn20.xml`

流程节点说明：
| 节点ID | 节点名称 | 类型 | 处理人 | 说明 |
|--------|----------|------|--------|------|
| start | 开始 | 开始事件 | - | 流程起点 |
| submit-leave | 提交请假申请 | 用户任务 | ${startUserId} | 申请人填写表单 |
| days-gateway | 判断请假天数 | 排他网关 | - | 根据天数路由 |
| manager-approval | 部门经理审批 | 用户任务 | ${managerId} | 短假审批 |
| gm-approval | 总经理审批 | 用户任务 | ${gmId} | 长假审批 |
| result-gateway | 审批结果 | 排他网关 | - | 判断审批结果 |
| hr-record | 人事备案 | 用户任务 | ${hrId} | 备案归档 |
| end-approve | 审批通过 | 结束事件 | - | 正常结束 |
| end-reject | 审批拒绝 | 结束事件 | - | 拒绝结束 |

### 2. 数据库初始化脚本
**文件**: `flow-admin/src/main/resources/db/leave-process-init.sql`

包含：
- 请假申请表单定义（包含请假类型、时间、原因等字段）
- 审批意见表单定义
- 流程模型基础数据

### 3. 自动部署配置
**文件**: `flow-admin/src/main/java/com/flow/admin/config/WorkflowInitConfig.java`

应用启动时自动检查并部署请假流程。

## 三、快速开始

### 步骤1：执行数据库脚本
```sql
-- 在MySQL数据库中执行
source flow-admin/src/main/resources/db/leave-process-init.sql
```

### 步骤2：启动应用
```bash
# 启动后端服务
cd flow-admin
mvn spring-boot:run

# 启动前端服务
cd flow-ui
npm run dev
```

### 步骤3：验证流程部署
1. 登录系统
2. 进入【流程管理】→【流程定义】
3. 查看是否存在"简单请假流程"

## 四、使用流程

### 1. 发起请假流程
1. 进入【流程管理】→【流程实例】
2. 点击【发起流程】按钮
3. 选择"简单请假流程"
4. 填写请假信息：
   - 请假类型（事假/病假/年假等）
   - 开始时间、结束时间
   - 请假天数
   - 请假原因
   - 工作交接说明
5. 点击【确定】提交申请

### 2. 审批流程
根据请假天数自动路由：

**场景1：请假≤3天**
```
申请人 → 部门经理审批 → 人事备案 → 完成
```

**场景2：请假>3天**
```
申请人 → 总经理审批 → 人事备案 → 完成
```

### 3. 查看待办任务
1. 进入【流程管理】→【我的待办】
2. 查看需要处理的审批任务
3. 点击【处理】按钮
4. 填写审批意见：
   - 选择审批结果（批准/拒绝）
   - 填写审批意见
5. 点击【提交】

### 4. 查看流程进度
1. 进入【流程管理】→【流程实例】
2. 找到对应的请假申请
3. 点击【查看】按钮
4. 查看流程详情和当前节点

## 五、流程变量说明

启动流程时需要设置的变量：

| 变量名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startUserId | Long | 是 | 申请人ID（系统自动设置）|
| managerId | Long | 是 | 部门经理ID |
| gmId | Long | 是 | 总经理ID |
| hrId | Long | 是 | 人事专员ID |
| leaveDays | Double | 是 | 请假天数（从表单计算）|
| title | String | 否 | 流程标题 |

表单数据存储在 `formData` 变量中，包含：
- leaveType: 请假类型
- startTime: 开始时间
- endTime: 结束时间
- leaveDays: 请假天数
- reason: 请假原因
- handover: 工作交接

审批时设置的变量：
- approved: Boolean（true=批准，false=拒绝）
- comment: String（审批意见）

## 六、API调用示例

### 1. 发起请假流程
```http
POST /workflow/instance/start
Content-Type: application/json

{
    "processDefinitionKey": "leave-simple",
    "title": "张三的请假申请",
    "businessKey": "LEAVE-2024-001",
    "formData": {
        "leaveType": "annual",
        "startTime": "2024-01-15 09:00",
        "endTime": "2024-01-17 18:00",
        "leaveDays": 3,
        "reason": "回家探亲",
        "handover": "工作已交接给李四"
    },
    "variables": {
        "managerId": 2,
        "gmId": 3,
        "hrId": 4,
        "leaveDays": 3
    }
}
```

### 2. 完成任务（审批）
```http
POST /workflow/task/complete/{taskId}
Content-Type: application/json

{
    "approved": true,
    "comment": "同意请假，注意安排好工作"
}
```

## 七、流程设计要点

### 1. 排他网关（Exclusive Gateway）
用于根据条件进行分支判断：
```xml
<exclusiveGateway id="days-gateway" name="判断请假天数" />

<!-- ≤3天走部门经理审批 -->
<sequenceFlow id="flow3" sourceRef="days-gateway" targetRef="manager-approval"
              conditionExpression="${leaveDays <= 3}" />

<!-- >3天走总经理审批 -->
<sequenceFlow id="flow4" sourceRef="days-gateway" targetRef="gm-approval"
              conditionExpression="${leaveDays > 3}" />
```

### 2. 用户任务（User Task）
指定任务处理人：
```xml
<userTask id="manager-approval" name="部门经理审批"
          flowable:assignee="${managerId}" />
```

### 3. 流程变量传递
表单数据通过变量传递：
```java
Map<String, Object> variables = new HashMap<>();
variables.put("formData", formData);
variables.put("leaveDays", leaveDays);
variables.put("managerId", managerId);
// ...
runtimeService.startProcessInstanceByKey("leave-simple", variables);
```

## 八、扩展建议

### 1. 添加会签功能
对于重要审批，可以添加多人会签：
```xml
<userTask id="multi-approval" name="会签审批">
  <multiInstanceLoopCharacteristics
    isSequential="false"
    flowable:collection="${assigneeList}"
    flowable:elementVariable="assignee">
    <completionCondition>${passCount / assigneeList.size() >= 0.5}</completionCondition>
  </multiInstanceLoopCharacteristics>
</userTask>
```

### 2. 添加抄送功能
在流程结束时添加抄送节点：
```xml
<serviceTask id="notify-cc" name="抄送通知"
             flowable:class="com.flow.workflow.delegate.NotifyDelegate" />
```

### 3. 添加超时提醒
设置任务超时提醒：
```xml
<userTask id="manager-approval" name="部门经理审批">
  <extensionElements>
    <flowable:taskListener event="create"
                           class="com.flow.workflow.listener.TimeoutReminderListener" />
  </extensionElements>
</userTask>
```

## 九、常见问题

### Q1: 流程部署失败怎么办？
A: 检查以下几点：
- BPMN文件语法是否正确
- 数据库连接是否正常
- Flowable引擎是否初始化成功

### Q2: 如何修改流程？
A: 
1. 修改 `leave-simple.bpmn20.xml` 文件
2. 重启应用自动部署新版本
3. 或在【流程模型】中设计后部署

### Q3: 如何查看流程图？
A: 进入【流程管理】→【流程定义】→ 点击【查看】按钮

## 十、相关链接

- [Flowable官方文档](https://www.flowable.com/open-source/docs/)
- [BPMN 2.0规范](https://www.omg.org/spec/BPMN/2.0/)
