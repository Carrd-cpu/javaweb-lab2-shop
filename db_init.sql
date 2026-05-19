-- ================
-- 实验二：MVC在线购物系统
-- 数据库初始化脚本
-- ================

CREATE DATABASE IF NOT EXISTS shop_mvc DEFAULT CHARSET utf8mb4;
USE shop_mvc;

DROP TABLE IF EXISTS t_cart_item;
DROP TABLE IF EXISTS t_product;
DROP TABLE IF EXISTS t_user;

-- 1) 用户表
CREATE TABLE t_user (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  username   VARCHAR(50) NOT NULL UNIQUE,
  password   VARCHAR(100) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2) 商品表
CREATE TABLE t_product (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  name       VARCHAR(100) NOT NULL,
  price      DECIMAL(10,2) NOT NULL,
  stock      INT NOT NULL DEFAULT 0,
  cover_url  VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3) 购物车项表
CREATE TABLE t_cart_item (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity   INT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_product (user_id, product_id),
  CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES t_user(id),
  CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES t_product(id)
) ENGINE=InnoDB;

-- 4) 初始化用户
-- 默认账号：admin / 123456
INSERT INTO t_user(username, password) VALUES
('admin', '123456'),
('zhangsan', '123456');

-- 5) 初始化商品
INSERT INTO t_product(name, price, stock, cover_url) VALUES
('小米手机', 1999.00, 50, NULL),
('机械键盘', 299.00, 120, NULL),
('无线鼠标', 99.00, 200, NULL),
('显示器 27英寸', 1099.00, 30, NULL),
('耳机', 199.00, 80, NULL),
('U盘 64G', 59.00, 300, NULL),
('移动硬盘 1T', 399.00, 60, NULL),
('路由器', 129.00, 100, NULL),
('Type-C 数据线', 29.00, 500, NULL),
('笔记本电脑', 5999.00, 20, NULL);
