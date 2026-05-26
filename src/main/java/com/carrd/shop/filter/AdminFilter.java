package com.carrd.shop.filter;

import com.carrd.shop.entity.User;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/api/admin/*")
public class AdminFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        User user = (User) request.getSession().getAttribute("loginUser");
        resp.setContentType("application/json;charset=UTF-8");
        if (user == null) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(401, "未登录，请先登录")));
            return;
        }
        if (!"admin".equals(user.getRole())) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(403, "无权限访问")));
            return;
        }
        chain.doFilter(req, resp);
    }
}
