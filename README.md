# javaweb-lab2-shop

实验二：基于 MVC 设计模式的在线购物系统（JSP 不分离 + ElementPlus + axios + Servlet + Druid + MySQL）。

## 运行环境
- JDK 21
- Tomcat 10+
- MySQL 8+

## 初始化数据库
1. 在 MySQL 中执行 `db_init.sql`
2. 修改数据库连接信息：`src/main/java/com/carrd/shop/listener/DbPoolListener.java`
   - url / username / password（目前 password=20120104）

## 启动
1. IDEA 以 Maven 导入
2. 配置 Tomcat 运行该 war
3. 访问：`http://localhost:8080/shop-mvc/pages/index.jsp`

## 测试账号
- admin / 123456

## 功能
- 商品分页浏览：GET `/api/products?page=1&pageSize=8`
- 登录：POST `/api/auth/login`
- 购物车分页：GET `/api/cart/list`
- 加入购物车：POST `/api/cart/add`
- 修改数量：POST `/api/cart/update`
- 删除：POST `/api/cart/delete`

