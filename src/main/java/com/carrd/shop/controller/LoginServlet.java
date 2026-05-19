package com.carrd.shop.controller;

import com.carrd.shop.dao.UserDao;
import com.carrd.shop.entity.User;
import com.carrd.shop.service.UserService;
import com.carrd.shop.util.DbUtil;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        DataSource ds = DbUtil.getDataSource(getServletContext());
        UserService service = new UserService(new UserDao(ds));

        User u = service.login(username, password);
        if (u == null) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(400, "账号或密码错误")));
            return;
        }

        req.getSession().setAttribute("loginUser", u);
        resp.getWriter().write(JsonUtil.toJson(Result.ok(u)));
    }
}
