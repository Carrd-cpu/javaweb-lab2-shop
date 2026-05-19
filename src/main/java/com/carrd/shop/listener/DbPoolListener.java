package com.carrd.shop.listener;

import com.alibaba.druid.pool.DruidDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DbPoolListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DruidDataSource ds = new DruidDataSource();

        // TODO-DB: 修改数据库连接信息（库名、账号、密码、时区等）
        ds.setUrl("jdbc:mysql://localhost:3306/shop_mvc?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
        ds.setUsername("root");         // TODO-DB
        ds.setPassword("20120104");     // TODO-DB

        ds.setInitialSize(5);
        ds.setMaxActive(20);
        ds.setMinIdle(5);

        sce.getServletContext().setAttribute("dataSource", ds);
        System.out.println("[DbPoolListener] DataSource initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Object obj = sce.getServletContext().getAttribute("dataSource");
        if (obj instanceof DruidDataSource ds) {
            ds.close();
            System.out.println("[DbPoolListener] DataSource closed.");
        }
    }
}
