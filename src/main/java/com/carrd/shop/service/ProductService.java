package com.carrd.shop.service;

import com.carrd.shop.dao.ProductDao;
import com.carrd.shop.entity.PageResult;
import com.carrd.shop.entity.Product;

import java.util.List;

public class ProductService {
    private final ProductDao dao;

    public ProductService(ProductDao dao) {
        this.dao = dao;
    }

    public PageResult<Product> page(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 8;
        if (pageSize > 50) pageSize = 50;

        int offset = (page - 1) * pageSize;

        long total = dao.countAll();
        List<Product> list = dao.findPage(offset, pageSize);

        PageResult<Product> pr = new PageResult<>();
        pr.setPage(page);
        pr.setPageSize(pageSize);
        pr.setTotal(total);
        pr.setList(list);
        return pr;
    }
}
