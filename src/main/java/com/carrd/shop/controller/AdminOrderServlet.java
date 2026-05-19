package com.carrd.shop.controller;

import com.carrd.shop.dao.OrderDao;
import com.carrd.shop.dao.OrderItemDao;
import com.carrd.shop.service.AdminOrderService;
import com.carrd.shop.util.DbUtil;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/admin/orders/*")
public class AdminOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();
        if ("/list".equals(path)) {
            AdminOrderService service = new AdminOrderService(ds(), new OrderDao(ds()), new OrderItemDao(ds()));
            resp.getWriter().write(JsonUtil.toJson(Result.ok(service.listAllOrders())));
            return;
        }
        if ("/detail".equals(path)) {
            Long orderId = parseLong(req.getParameter("orderId"));
            if (orderId == null) {
                resp.getWriter().write(JsonUtil.toJson(Result.fail(400, "orderId不能为空")));
                return;
            }
            AdminOrderService service = new AdminOrderService(ds(), new OrderDao(ds()), new OrderItemDao(ds()));
            Map<String, Object> detail = service.getOrderDetail(orderId);
            if (detail == null) {
                resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "订单不存在")));
                return;
            }
            resp.getWriter().write(JsonUtil.toJson(Result.ok(detail)));
            return;
        }
        resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "Not Found")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();
        if (path == null) {
            path = "";
        }
        AdminOrderService service = new AdminOrderService(ds(), new OrderDao(ds()), new OrderItemDao(ds()));
        try {
            if ("/status".equals(path)) {
                Long orderId = parseLong(req.getParameter("orderId"));
                String status = req.getParameter("status");
                service.updateStatus(orderId, status);
                resp.getWriter().write(JsonUtil.toJson(Result.ok("updated")));
                return;
            }
            if ("/delete".equals(path)) {
                Long orderId = parseLong(req.getParameter("orderId"));
                service.deleteOrder(orderId);
                resp.getWriter().write(JsonUtil.toJson(Result.ok("deleted")));
                return;
            }
            resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "Not Found")));
        } catch (IllegalArgumentException e) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(400, e.getMessage())));
        }
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
