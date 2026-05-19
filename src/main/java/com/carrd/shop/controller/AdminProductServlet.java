package com.carrd.shop.controller;

import com.carrd.shop.dao.ProductDao;
import com.carrd.shop.entity.Product;
import com.carrd.shop.service.AdminProductService;
import com.carrd.shop.util.DbUtil;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/api/admin/products/*")
public class AdminProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getPathInfo();
        if ("/list".equals(path)) {
            AdminProductService service = new AdminProductService(new ProductDao(ds()));
            resp.getWriter().write(JsonUtil.toJson(Result.ok(service.listAll())));
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
        try {
            switch (path) {
                case "/create" -> handleCreate(req, resp);
                case "/update" -> handleUpdate(req, resp);
                case "/delete" -> handleDelete(req, resp);
                default -> resp.getWriter().write(JsonUtil.toJson(Result.fail(404, "Not Found")));
            }
        } catch (IllegalArgumentException e) {
            resp.getWriter().write(JsonUtil.toJson(Result.fail(400, e.getMessage())));
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Product product = buildProduct(req);
        AdminProductService service = new AdminProductService(new ProductDao(ds()));
        long id = service.create(product);
        resp.getWriter().write(JsonUtil.toJson(Result.ok(id)));
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Product product = buildProduct(req);
        product.setId(parseLong(req.getParameter("id")));
        AdminProductService service = new AdminProductService(new ProductDao(ds()));
        service.update(product);
        resp.getWriter().write(JsonUtil.toJson(Result.ok("updated")));
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        AdminProductService service = new AdminProductService(new ProductDao(ds()));
        service.delete(id);
        resp.getWriter().write(JsonUtil.toJson(Result.ok("deleted")));
    }

    private Product buildProduct(HttpServletRequest req) {
        Product p = new Product();
        p.setName(req.getParameter("name"));
        p.setPrice(parseBigDecimal(req.getParameter("price")));
        p.setStock(parseInt(req.getParameter("stock"), -1));
        p.setCoverUrl(req.getParameter("coverUrl"));
        return p;
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

    private BigDecimal parseBigDecimal(String s) {
        try {
            return new BigDecimal(s);
        } catch (Exception e) {
            return null;
        }
    }
}
