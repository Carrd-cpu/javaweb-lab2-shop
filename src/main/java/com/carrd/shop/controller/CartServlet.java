package com.carrd.shop.controller;

import com.carrd.shop.dao.CartDao;
import com.carrd.shop.entity.CartItemVO;
import com.carrd.shop.entity.PageResult;
import com.carrd.shop.entity.User;
import com.carrd.shop.service.CartService;
import com.carrd.shop.util.DbUtil;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getPathInfo();
        if ("/list".equals(path)) {
            handleList(req, resp);
            return;
        }
        resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "Not Found")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getPathInfo();
        if ("/add".equals(path)) {
            handleAdd(req, resp);
            return;
        }
        if ("/update".equals(path)) {
            handleUpdate(req, resp);
            return;
        }
        if ("/delete".equals(path)) {
            handleDelete(req, resp);
            return;
        }
        resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "Not Found")));
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = (User) req.getSession().getAttribute("loginUser");

        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 5);

        CartService service = new CartService(new CartDao(ds()));
        PageResult<CartItemVO> pr = service.page(u.getId(), page, pageSize);
        resp.getWriter().write(JsonUtil.toJson(Result.ok(pr)));
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = (User) req.getSession().getAttribute("loginUser");

        Long productId = parseLong(req.getParameter("productId"));
        int quantity = parseInt(req.getParameter("quantity"), 1);

        if (productId == null) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(400, "productId不能为空")));
            return;
        }

        CartService service = new CartService(new CartDao(ds()));
        service.add(u.getId(), productId, quantity);

        resp.getWriter().write(JsonUtil.toJson(Result.ok("added")));
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = (User) req.getSession().getAttribute("loginUser");

        Long productId = parseLong(req.getParameter("productId"));
        int quantity = parseInt(req.getParameter("quantity"), 1);

        if (productId == null) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(400, "productId不能为空")));
            return;
        }

        CartService service = new CartService(new CartDao(ds()));
        service.update(u.getId(), productId, quantity);

        resp.getWriter().write(JsonUtil.toJson(Result.ok("updated")));
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = (User) req.getSession().getAttribute("loginUser");

        Long productId = parseLong(req.getParameter("productId"));
        if (productId == null) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(400, "productId不能为空")));
            return;
        }

        CartService service = new CartService(new CartDao(ds()));
        service.delete(u.getId(), productId);

        resp.getWriter().write(JsonUtil.toJson(Result.ok("deleted")));
    }

    private DataSource ds() {
        return DbUtil.getDataSource(getServletContext());
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private Long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }
}
