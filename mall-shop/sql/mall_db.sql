-- 商城数据库初始化脚本
CREATE DATABASE IF NOT EXISTS mall_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mall_db;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像',
    role        VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER' COMMENT '角色',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS t_category (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    parent_id   BIGINT       DEFAULT 0 COMMENT '父分类ID',
    sort        INT          DEFAULT 0 COMMENT '排序',
    icon        VARCHAR(255) DEFAULT NULL COMMENT '图标',
    status      TINYINT      DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS t_product (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id     BIGINT         NOT NULL COMMENT '分类ID',
    name            VARCHAR(100)   NOT NULL COMMENT '商品名称',
    subtitle        VARCHAR(200)   DEFAULT NULL COMMENT '副标题',
    main_image      VARCHAR(255)   DEFAULT NULL COMMENT '主图',
    detail          TEXT           DEFAULT NULL COMMENT '详情',
    price           DECIMAL(10,2)  NOT NULL COMMENT '售价',
    original_price  DECIMAL(10,2)  DEFAULT NULL COMMENT '原价',
    stock           INT            NOT NULL DEFAULT 0 COMMENT '库存',
    sales           INT            NOT NULL DEFAULT 0 COMMENT '销量',
    status          TINYINT        DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT        DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS t_address (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL COMMENT '用户ID',
    receiver_name   VARCHAR(50)  NOT NULL COMMENT '收货人',
    receiver_phone  VARCHAR(20)  NOT NULL COMMENT '联系电话',
    province        VARCHAR(50)  NOT NULL COMMENT '省',
    city            VARCHAR(50)  NOT NULL COMMENT '市',
    district        VARCHAR(50)  NOT NULL COMMENT '区',
    detail          VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default      TINYINT      DEFAULT 0 COMMENT '是否默认 0-否 1-是',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no         VARCHAR(32)    NOT NULL UNIQUE COMMENT '订单号',
    user_id          BIGINT         NOT NULL COMMENT '用户ID',
    total_amount     DECIMAL(10,2)  NOT NULL COMMENT '订单总额',
    pay_amount       DECIMAL(10,2)  NOT NULL COMMENT '实付金额',
    status           TINYINT        NOT NULL DEFAULT 0 COMMENT '状态 0-待付款 1-待发货 2-待收货 3-已完成 4-已取消',
    receiver_name    VARCHAR(50)    NOT NULL COMMENT '收货人',
    receiver_phone   VARCHAR(20)    NOT NULL COMMENT '联系电话',
    receiver_address VARCHAR(300)   NOT NULL COMMENT '收货地址',
    remark           VARCHAR(200)   DEFAULT NULL COMMENT '备注',
    pay_time         DATETIME       DEFAULT NULL COMMENT '支付时间',
    ship_time        DATETIME       DEFAULT NULL COMMENT '发货时间',
    finish_time      DATETIME       DEFAULT NULL COMMENT '完成时间',
    create_time      DATETIME       DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted          TINYINT        DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS t_order_item (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT         NOT NULL COMMENT '订单ID',
    product_id    BIGINT         NOT NULL COMMENT '商品ID',
    product_name  VARCHAR(100)   NOT NULL COMMENT '商品名称',
    product_image VARCHAR(255)   DEFAULT NULL COMMENT '商品图片',
    price         DECIMAL(10,2)  NOT NULL COMMENT '单价',
    quantity      INT            NOT NULL COMMENT '数量',
    subtotal      DECIMAL(10,2)  NOT NULL COMMENT '小计',
    create_time   DATETIME       DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 初始化数据
-- 密码均为: admin123 (BCrypt加密)
INSERT INTO t_user (username, password, nickname, phone, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', '13800000000', 'ROLE_ADMIN', 1),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', '13800000001', 'ROLE_USER', 1);

INSERT INTO t_category (name, parent_id, sort, status) VALUES
('手机数码', 0, 1, 1),
('电脑办公', 0, 2, 1),
('家用电器', 0, 3, 1),
('服装鞋帽', 0, 4, 1),
('食品饮料', 0, 5, 1);

INSERT INTO t_product (category_id, name, subtitle, main_image, price, original_price, stock, sales, status) VALUES
(1, 'iPhone 15 Pro', '钛金属设计，A17 Pro芯片', 'https://picsum.photos/400/400?random=1', 8999.00, 9999.00, 100, 520, 1),
(1, '华为 Mate 60 Pro', '卫星通信，昆仑玻璃', 'https://picsum.photos/400/400?random=2', 6999.00, 7999.00, 80, 380, 1),
(2, 'MacBook Pro 14', 'M3 Pro芯片，Liquid Retina XDR', 'https://picsum.photos/400/400?random=3', 14999.00, 16999.00, 50, 210, 1),
(2, 'ThinkPad X1 Carbon', '轻薄商务本，14英寸', 'https://picsum.photos/400/400?random=4', 9999.00, 11999.00, 60, 150, 1),
(3, '戴森吸尘器 V15', '激光探测，强劲吸力', 'https://picsum.photos/400/400?random=5', 4990.00, 5990.00, 30, 89, 1),
(4, '优衣库羽绒服', '轻薄保暖，多色可选', 'https://picsum.photos/400/400?random=6', 599.00, 899.00, 200, 1200, 1),
(5, '三只松鼠坚果礼盒', '每日坚果，健康零食', 'https://picsum.photos/400/400?random=7', 99.00, 129.00, 500, 3500, 1);
