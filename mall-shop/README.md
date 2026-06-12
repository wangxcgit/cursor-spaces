# Mall Shop - Spring Boot + Vue 商城项目

完整的电商商城项目，包含 Spring Boot 后端和 Vue 3 前端。

## 技术栈

### 后端 (mall-server)
- Java 17 + Spring Boot 3.2
- Spring Security + JWT 认证
- MyBatis-Plus 数据访问
- MySQL 8 数据库
- Redis 购物车缓存
- Knife4j API 文档

### 前端 (mall-web)
- Vue 3 + Vite
- Element Plus UI
- Pinia 状态管理
- Vue Router 路由
- Axios HTTP 请求

## 功能模块

| 模块 | 功能 |
|------|------|
| 用户认证 | 注册、登录、JWT Token |
| 商品管理 | 分类列表、商品列表、商品详情 |
| 购物车 | 添加/删除/修改数量（Redis存储） |
| 订单管理 | 创建订单、支付、取消、确认收货 |
| 收货地址 | 增删改查、设置默认地址 |
| 后台管理 | 商品/分类/订单管理（需管理员权限） |

## 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+

### 2. 初始化数据库
```bash
mysql -u root -p < sql/mall_db.sql
```

### 3. 修改配置
编辑 `mall-server/src/main/resources/application.yml`，修改数据库和 Redis 连接信息。

### 4. 启动后端
```bash
cd mall-server
mvn spring-boot:run
```

后端启动后访问：
- API 文档: http://localhost:8080/api/doc.html
- Swagger: http://localhost:8080/api/swagger-ui.html

### 5. 启动前端
```bash
cd mall-web
npm install
npm run dev
```

前端访问: http://localhost:5173

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user1 | admin123 |

## API 接口概览

### 公开接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/products` - 商品列表
- `GET /api/products/{id}` - 商品详情
- `GET /api/categories` - 分类列表

### 用户接口（需登录）
- `GET /api/user/profile` - 用户信息
- `GET /api/cart` - 购物车
- `POST /api/cart` - 添加购物车
- `POST /api/orders` - 创建订单
- `POST /api/orders/{id}/pay` - 支付订单
- `GET /api/addresses` - 地址列表

### 管理接口（需管理员）
- `POST /api/admin/products` - 新增商品
- `PUT /api/admin/products/{id}` - 更新商品
- `POST /api/admin/orders/{id}/ship` - 订单发货

## 项目结构

```
mall-shop/
├── mall-server/          # Spring Boot 后端
│   └── src/main/java/com/mall/
│       ├── common/       # 公共模块
│       ├── config/       # 配置类
│       ├── security/     # 安全认证
│       └── module/       # 业务模块
│           ├── user/     # 用户
│           ├── product/  # 商品
│           ├── cart/     # 购物车
│           ├── order/    # 订单
│           ├── address/  # 地址
│           └── admin/    # 后台管理
├── mall-web/             # Vue 3 前端
├── sql/                  # 数据库脚本
└── README.md
```

## 订单状态流转

```
待付款(0) → 支付 → 待发货(1) → 发货 → 待收货(2) → 确认收货 → 已完成(3)
    ↓
  取消 → 已取消(4)
```
