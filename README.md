# 大事件 · 内容管理系统

一个基于 **Spring Boot 3 + Vue 3** 的前后端分离内容管理平台，包含用户认证、文章分类、文章发布/草稿、条件分页、文件上传、个人中心和管理员在线用户管理等功能。

> 项目定位：在“大事件”课程项目的基础上，完成前端页面、前后端联调与安全加固，作为 Java 后端学习与求职作品使用。当前为单机部署版本，不是生产级系统。

## 功能特性

### 用户与认证

- 注册、登录、退出登录
- 密码使用 BCrypt 存储；兼容历史 MD5 数据，老用户登录成功后自动升级为 BCrypt
- JWT（HMAC256）签发令牌，Redis 维护登录白名单，令牌有效期 12 小时
- 单会话登录：同一账号重复登录会使旧令牌失效
- 修改密码、退出登录、管理员强制下线会立即删除 Redis 中的令牌
- 个人资料修改、头像更换、密码重置
- 管理员可查看用户列表、在线状态并强制用户下线

### 文章分类

- 分类新增、修改、删除、列表、详情
- 所有操作按当前登录用户隔离，不能操作他人的分类
- 更新/删除失败时返回明确错误，不再“假成功”

### 文章管理

- 文章新增、修改、删除、详情
- 按分类、发布状态筛选，分页查询
- 支持“已发布 / 草稿”两种状态
- 富文本内容、封面上传
- 文章按创建人隔离，详情、更新、删除均校验归属

### 文件上传

- 后缀白名单：`jpg`、`jpeg`、`png`、`pdf`
- MIME 类型校验
- 文件头魔数校验，防止“改后缀传脚本”
- 单文件 5MB 限制
- UUID 重命名，按配置目录落盘，目录不存在时自动创建
- 返回基于当前请求域名/端口的访问 URL，不写死 `localhost`

### 前端

- Vue 3 + Element Plus + Pinia + Vue Router + Axios
- 登录/注册、文章分类、文章管理、个人中心、头像、重置密码
- Axios 请求拦截器统一注入 token，响应拦截器统一处理业务状态码与 401
- 管理员菜单按角色显示

## 技术栈

| 层次 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.0.2、Spring MVC |
| 持久层 | MyBatis（注解 + XML 动态 SQL）、PageHelper |
| 数据库 | MySQL 8 |
| 缓存/会话 | Redis、StringRedisTemplate |
| 安全 | JWT（java-jwt 4.4.0）、BCrypt（spring-security-crypto） |
| 参数校验 | Jakarta Validation、自定义 `@State` 校验注解 |
| 构建 | Maven |
| 前端 | Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios、Vue Quill |
| 工具 | Git、IDEA、Navicat/SQLyog |

## 安全设计

这部分是相对课程原版的主要改造点。

### 1. 密码存储

- 注册、改密统一使用 `BCryptPasswordEncoder`。
- 登录时根据密码哈希前缀识别 BCrypt；老 MD5 用户登录成功后，把密码升级为 BCrypt 并写回数据库。
- `password` 字段长度按 BCrypt 哈希调整为 `VARCHAR(100)`。

### 2. 登录态

- JWT 使用 HMAC256 签名，密钥来自环境变量 `JWT_SECRET`，不再硬编码在代码里。
- Redis key 为 `login:token:{userId}`，TTL 与 JWT 一致，均为 12 小时。
- 登录成功后写入 token；拦截器解析 JWT 得到 userId，再从 Redis 取 token 比对。
- 退出登录、修改密码、管理员强制下线都会删除对应 key，使旧 token 立即失效。
- 同一账号只保留一个有效 token，重复登录会覆盖旧 token。

### 3. 权限与越权

- 文章、分类的详情、更新、删除 SQL 都带 `create_user` 条件。
- 更新/删除返回影响行数，0 行时返回错误，避免“越权操作返回成功”。
- 管理员接口（用户列表、强制下线）在 Controller 层校验 `role = admin`。

### 4. 上传安全

- 后缀、MIME、文件头魔数、大小四层校验。
- UUID 重命名，避免覆盖和路径猜测。
- 上传目录通过 `REAL_PATH` 配置，不再写死在 Controller 里。
- 返回 URL 使用当前请求的 scheme/host/port，部署后不会指向 localhost。

### 5. 配置安全

- 数据库密码、Redis 密码、JWT 密钥、上传路径全部走 `.env`。
- `.env` 已被 `.gitignore` 忽略，仓库只提交 `.env.example`。
- 生产环境建议使用环境变量或配置中心，不要提交真实密钥。

### 6. 统一响应

- 后端统一返回 `Result<T>`：`code`、`msg`、`data`。
- 未登录/令牌失效由拦截器返回 HTTP 401，前端响应拦截器清理登录态并跳转登录页。

## 项目结构

```text
big_event/
├── backend/
│   ├── src/main/java/com/example/big_event/
│   │   ├── anno/                 # 自定义校验注解
│   │   ├── config/               # SecurityConfig、WebConfig
│   │   ├── controller/           # User、Article、Category、FileUpload
│   │   ├── exception/            # 全局异常处理
│   │   ├── interceptors/         # 登录拦截器
│   │   ├── mapper/               # MyBatis Mapper
│   │   ├── pojo/                 # 实体、Result、PageBean
│   │   ├── service/              # 业务接口与实现
│   │   ├── utils/                # JwtUtil、Md5Util、ThreadLocalUtil、FileValidationUtil
│   │   └── BigEventApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── com/example/big_event/mapper/ArticleMapper.xml
│   ├── .env.example
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/                  # user.js、article.js
│   │   ├── router/               # 路由表
│   │   ├── stores/               # token、userInfo
│   │   ├── utils/request.js      # Axios 实例与拦截器
│   │   └── views/                # 登录、布局、文章、个人中心、在线用户
│   ├── vite.config.js
│   └── package.json
└── README.md
```

## 快速开始

### 环境要求

| 依赖 | 版本 |
| --- | --- |
| JDK | 17+ |
| Maven | 3.6+ |
| MySQL | 8.x |
| Redis | 5+ |
| Node.js | 22.18+ 或 24.12+ |

### 1. 初始化数据库

创建 `big_event` 库并执行下面的建表 SQL（字段与当前代码一致）：

```sql
CREATE DATABASE IF NOT EXISTS big_event DEFAULT CHARACTER SET utf8mb4;
USE big_event;

CREATE TABLE `user` (
  `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role`        VARCHAR(32)  NOT NULL DEFAULT 'user' COMMENT '角色：user/admin',
  `username`    VARCHAR(20)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(100) DEFAULT NULL COMMENT 'BCrypt 密码哈希',
  `nickname`    VARCHAR(10)  DEFAULT NULL COMMENT '昵称',
  `email`       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `user_pic`    VARCHAR(128) DEFAULT NULL COMMENT '头像地址',
  `isOnline`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否在线',
  `create_time` DATETIME     NOT NULL,
  `update_time` DATETIME     NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

CREATE TABLE `category` (
  `id`             INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `category_name`  VARCHAR(10)  NOT NULL,
  `category_alias` VARCHAR(15)  NOT NULL,
  `create_user`    INT UNSIGNED NOT NULL,
  `create_time`    DATETIME     NOT NULL,
  `update_time`    DATETIME     NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_category_user` (`create_user`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章分类表';

CREATE TABLE `article` (
  `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title`       VARCHAR(10)  NOT NULL,
  `content`     TEXT         NOT NULL,
  `cover_img`   VARCHAR(255) NOT NULL,
  `state`       VARCHAR(5)   DEFAULT '草稿',
  `category_id` INT UNSIGNED NOT NULL,
  `create_user` INT UNSIGNED NOT NULL,
  `create_time` DATETIME     NOT NULL,
  `update_time` DATETIME     NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_article_user_category_state` (`create_user`, `category_id`, `state`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章表';
```

> 项目没有引入 Flyway/Liquibase，表结构需要手动初始化。已有数据库只需补上 `role`、`isOnline` 字段，并把 `password` 扩到 `VARCHAR(100)`。

### 2. 配置环境变量

进入 `backend`，复制 `.env.example` 为 `.env`，按本机环境填写：

```bash
cd backend
cp .env.example .env
```

```dotenv
JWT_SECRET=请替换为至少32位的随机字符串
DB_PASSWORD=你的MySQL密码
REDIS_PASSWORD=你的Redis密码
REAL_PATH=E:/Java/vue/projcet/big_event/backend/fileStore/
WEB_PREFIX=/upload/
```

说明：

- `JWT_SECRET` 不要使用示例值，建议 32 位以上随机字符串。
- `REAL_PATH` 是上传文件的本地目录，项目启动时会自动创建。
- `WEB_PREFIX` 是静态资源访问前缀，需以 `/` 开头和结尾。

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认端口 `8080`。也可以直接用 IDEA 打开 `backend`，运行 `BigEventApplication`。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

浏览器打开 <http://localhost:5173>。Vite 会把 `/api` 代理到 `http://localhost:8080`，因此需要先启动后端。

### 5. 创建管理员

注册一个账号后，在数据库中把角色改成 `admin`：

```sql
UPDATE `user` SET `role` = 'admin' WHERE `username` = '你的用户名';
```

重新登录后，左侧菜单会出现“管理在线用户”。

### 6. 构建

```bash
# 后端
cd backend
mvn clean package

# 前端
cd frontend
npm run build
```

前端产物在 `frontend/dist`。后端默认打包为可执行 jar，可用 `java -jar target/big_event-0.0.1-SNAPSHOT.jar` 启动。

## 配置说明

| 配置项 | 说明 | 示例 |
| --- | --- | --- |
| `JWT_SECRET` | JWT HMAC256 签名密钥 | 32 位以上随机字符串 |
| `DB_PASSWORD` | MySQL 密码 | `your_password` |
| `REDIS_PASSWORD` | Redis 密码 | `your_password` |
| `REAL_PATH` | 上传文件保存目录 | `E:/.../backend/fileStore/` |
| `WEB_PREFIX` | 上传文件访问前缀 | `/upload/` |

`application.yml` 中数据库地址、端口、Redis 地址也按本机环境修改。

## 接口概览

统一响应：

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": {}
}
```

`code = 0` 表示成功，`code = 1` 表示业务失败。除登录、注册、`/upload/**` 静态资源外，其余接口都需要请求头：

```http
Authorization: <token>
```

> 当前实现没有 `Bearer ` 前缀，token 原文直接放在请求头中。

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| POST | `/user/register` | 注册（form: username、password） | 公开 |
| POST | `/user/login` | 登录，返回 JWT | 公开 |
| POST | `/user/logout` | 退出登录，删除 Redis token | 登录 |
| GET | `/user/userInfo` | 当前用户信息 | 登录 |
| PUT | `/user/update` | 修改昵称、邮箱 | 登录 |
| PATCH | `/user/updateAvatar?avatarUrl=` | 修改头像 | 登录 |
| PATCH | `/user/updatePwd` | 修改密码（JSON: old_pwd、new_pwd、re_pwd） | 登录 |
| GET | `/user/list?pageNum=&pageSize=` | 用户列表 | 管理员 |
| DELETE | `/user/deleteToken?userId=` | 强制下线 | 管理员 |
| GET | `/category` | 当前用户分类列表 | 登录 |
| GET | `/category/detail?id=` | 分类详情 | 登录 |
| POST | `/category` | 新增分类 | 登录 |
| PUT | `/category` | 修改分类 | 登录 |
| DELETE | `/category?id=` | 删除分类 | 登录 |
| GET | `/article?pageNum=&pageSize=&categoryId=&state=` | 文章分页 | 登录 |
| GET | `/article/detail?id=` | 文章详情 | 登录 |
| POST | `/article` | 新增文章 | 登录 |
| PUT | `/article` | 修改文章 | 登录 |
| DELETE | `/article?id=` | 删除文章 | 登录 |
| POST | `/upload` | 上传文件（multipart: file） | 公开 |

## 已知限制

这个项目是求职作品，不是生产系统，以下问题在使用/面试时需要如实说明：

- **角色模型较简单**：`user.role` 只有 `user/admin` 两种字符串角色，没有菜单/按钮级权限表，不是完整的 RBAC。
- **单会话**：同一账号只保留一个有效 token，重复登录会挤掉旧会话；没有 refresh token。
- **在线状态不自动过期**：登录/退出/被踢会更新 `isOnline`，但 token 自然过期时不会自动置为离线。
- **本地文件存储**：上传文件保存在本机磁盘，未接 OSS/CDN；多实例部署需要改成共享存储。
- **测试不完整**：目前只有 Spring 上下文加载测试，Service 单元测试、接口集成测试尚未补齐。
- **无容器化/监控**：没有 Docker、CI/CD、Prometheus/Grafana；当前按单机方式运行。
- **前端路由守卫不完整**：管理员菜单按角色隐藏，但直接访问 URL 的最终拦截依赖后端接口校验。
- **课程项目背景**：项目骨架来自“大事件”课程案例，前端页面、联调、安全加固和工程整理为个人完成；面试时建议按“课程打底 + 我的改造”说明，不要包装成完全自研。

## 后续计划

- 引入完整的 RBAC（用户-角色-权限表）与接口级权限注解
- 补 Service 单元测试、MockMvc 接口测试与 Testcontainers 集成测试
- 用 Docker Compose 编排 MySQL、Redis、后端与 Nginx
- 上传接 OSS，增加文件访问鉴权与 CDN
- 登录失败次数限制、JWT 刷新令牌、操作审计日志
- 文章列表慢查询分析与索引优化

## 说明

本项目用于学习、实践与求职展示。课程原始案例来自“大事件”实战课程；当前版本在原有骨架上完成了前端页面、接口联调、安全加固与配置整理。如果这个项目对你有帮助，欢迎 Star。
