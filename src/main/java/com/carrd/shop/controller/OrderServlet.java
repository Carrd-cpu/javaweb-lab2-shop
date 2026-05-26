package com.carrd.shop.controller;

import com.carrd.shop.dao.CartDao;
import com.carrd.shop.dao.OrderDao;
import com.carrd.shop.dao.OrderItemDao;
import com.carrd.shop.dao.ProductDao;
import com.carrd.shop.entity.User;
import com.carrd.shop.service.CheckoutService;
import com.carrd.shop.service.OrderService;
import com.carrd.shop.util.DbUtil;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/order/*")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();
        if ("/my".equals(path)) {
            handleMyOrders(req, resp);
            return;
        }
        if ("/detail".equals(path)) {
            handleMyOrderDetail(req, resp);
            return;
        }
        resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "Not Found")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();
        if ("/checkout".equals(path)) {
            handleCheckout(req, resp);
            return;
        }
        resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "Not Found")));
    }

    private void handleCheckout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("loginUser");
        CheckoutService service = new CheckoutService(ds(), new CartDao(ds()), new ProductDao(ds()), new OrderDao(ds()), new OrderItemDao(ds()));
        try {
            Long orderId = service.checkout(user.getId());
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("orderId", orderId);
            data.put("status", "CREATED");
            resp.getWriter().write(JsonUtil.toJson(Result.ok(data)));
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(400, e.getMessage())));
        }
    }

    private void handleMyOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("loginUser");
        OrderService service = new OrderService(new OrderDao(ds()), new OrderItemDao(ds()));
        resp.getWriter().write(JsonUtil.toJson(Result.ok(service.listMyOrders(user.getId()))));
    }

    private void handleMyOrderDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("loginUser");
        Long orderId = parseLong(req.getParameter("orderId"));
        if (orderId == null) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(400, "orderId不能为空")));
            return;
        }
        OrderService service = new OrderService(new OrderDao(ds()), new OrderItemDao(ds()));
        Map<String, Object> detail = service.getMyOrderDetail(user.getId(), orderId);
        if (detail == null) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "订单不存在")));
            return;
        }
        resp.getWriter().write(JsonUtil.toJson(Result.ok(detail)));
    }

    private DataSource ds() {
        return DbUtil.getDataSource(getServletContext());
    }

    private Long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }
}
