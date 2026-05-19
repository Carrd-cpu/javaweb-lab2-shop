package com.carrd.shop.controller;

import com.carrd.shop.dao.ProductDao;
import com.carrd.shop.entity.PageResult;
import com.carrd.shop.entity.Product;
import com.carrd.shop.service.ProductService;
import com.carrd.shop.util.DbUtil;
import com.carrd.shop.util.JsonUtil;
import com.carrd.shop.util.Result;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = parseInt(req.getParameter("pageSize"), 8);

        DataSource ds = DbUtil.getDataSource(getServletContext());
        ProductService service = new ProductService(new ProductDao(ds));

        PageResult<Product> pr = service.page(page, pageSize);
        resp.getWriter().write(JsonUtil.toJson(Result.ok(pr)));
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}
