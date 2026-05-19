package com.carrd.shop.controller;

import com.carrd.shop.entity.User;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/auth/me")
public class AuthMeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = (User) req.getSession().getAttribute("loginUser");
        if (user == null) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(401, "未登录")));
            return;
        }
        resp.getWriter().write(JsonUtil.toJson(Result.ok(user)));
    }
}
