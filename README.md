# 宿舍管理系统

基于 Vue 3 + Spring Boot 的宿舍管理系统，现已支持三端分流：

- 管理员端（后台管理）
- 学生端（查看宿舍 + 提交报修）
- 维修师傅端（接单 + 完结）

## 功能概览

### 管理员端

- 仪表盘：学生数、楼栋数、房间数、剩余床位
- 学生管理：列表、搜索、新增、编辑、删除（学生字段文案统一为“系部”）
- 楼栋管理：新增、编辑、删除（删除时自动解绑学生并清理楼栋下房间）
- 房间管理：筛选、详情、新建房间、详情中移除学生
- 楼栋筛选下拉：实时同步后端楼栋数据
- 宿舍分配：
  - 仅显示可分配房间
  - 按学生性别自动过滤可选宿舍
  - 后端强校验性别匹配
- 数据统计：
  - 卡片数据来自后端实时统计
  - 楼栋分布按真实入住数量排序
- 维修管理：工单列表、筛选、新增、编辑、完结、删除

### 学生端

- 学号 + 姓名登录
- 查看个人宿舍信息（含系部/专业）
- 提交报修（仅允许提交到当前分配宿舍）
- 查看我的报修记录

### 维修师傅端

- 独立登录
- 查看工单列表（状态/关键字筛选）
- 接单（待处理 -> 处理中）
- 完结工单（-> 已完成）

## 技术栈

- 前端：Vue 3、Vue Router、Pinia、Vite
- 后端：Spring Boot 3、Spring Data JPA
- 数据库：MySQL（当前主用）

## 快速开始

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

要求：JDK 17+、Maven 3.6+

后端地址：`http://localhost:8080`

### 2. 启动前端

```bash
npm install
npm run dev
```

前端地址：`http://localhost:5173`

开发代理：`/api -> http://localhost:8080`

## 登录账号说明

### 管理员端

- 登录页：`/login`
- 默认账号：`admin`
- 默认密码：`admin123`

可通过后端配置覆盖：

- `auth.admin.username`
- `auth.admin.password`

### 学生端

- 登录页：`/student/login`
- 登录方式：学号 + 姓名（需与数据库学生信息匹配）

### 维修师傅端

- 登录页：`/worker/login`
- 默认账号：`worker`
- 默认密码：`123456`

## 数据库配置（MySQL）

- 配置文件：`backend/src/main/resources/application-mysql.yml`
- 先创建数据库：

```sql
CREATE DATABASE dormitory DEFAULT CHARSET utf8mb4;
```

- 修改 `application-mysql.yml` 的账号密码
- 当前策略：
  - `ddl-auto: update`
  - `spring.sql.init.mode: never`（重启不自动插入示例数据）

## 接口分流

### 公共认证

- `POST /api/auth/login` 管理员登录
- `POST /api/auth/student-login` 学生登录
- `POST /api/auth/worker-login` 维修师傅登录

### 管理员接口（后台）

- 学生：`/api/students/**`
- 楼栋：`/api/dormitories/**`
- 房间：`/api/rooms/**`
- 统计：`/api/stats`
- 工单管理：`/api/repairs/**`

说明：路由守卫已做精确前缀匹配，`/students` 不会误跳转到学生端登录。

### 学生端接口

- `GET /api/student/profile/{studentId}`
- `GET /api/student/repairs/{studentId}?status=`
- `POST /api/student/repairs`（自动绑定当前宿舍）

### 维修师傅端接口

- `GET /api/worker/repairs?status=&keyword=`
- `POST /api/worker/repairs/{id}/accept`
- `POST /api/worker/repairs/{id}/complete`

## 项目结构

```text
.
├── backend/
│   └── src/main/
│       ├── java/com/dormitory/
│       │   ├── controller/
│       │   ├── service/
│       │   ├── repository/
│       │   ├── entity/
│       │   └── dto/
│       └── resources/
│           ├── application.yml
│           ├── application-mysql.yml
│           ├── data.sql
│           └── data-mysql.sql
├── src/
│   ├── api/
│   ├── components/
│   ├── layouts/
│   ├── router/
│   ├── styles/
│   └── views/
└── package.json
```
