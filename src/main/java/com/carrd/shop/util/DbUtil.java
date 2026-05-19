package com.carrd.shop.util;

import jakarta.servlet.ServletContext;

import javax.sql.DataSource;

public class DbUtil {
    public static DataSource getDataSource(ServletContext ctx) {
        Object ds = ctx.getAttribute("dataSource");
        if (ds == null) throw new IllegalStateException("DataSource not initialized.");
        return (DataSource) ds;
    }
}
