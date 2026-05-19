package com.carrd.shop.filter;

import com.carrd.shop.entity.User;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/api/cart/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        User u = (User) request.getSession().getAttribute("loginUser");
        if (u == null) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(JsonUtil.toJson(Result.fail(401, "未登录，请先登录")));
            return;
        }
        chain.doFilter(req, resp);
    }
}
