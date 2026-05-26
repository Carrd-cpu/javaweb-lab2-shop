package com.carrd.shop.service;

import com.carrd.shop.dao.ProductDao;
import com.carrd.shop.entity.Product;

import java.util.List;

public class AdminProductService {
    private final ProductDao productDao;

    public AdminProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> listAll() {
        return productDao.findAll();
    }

    public long create(Product product) {
        validate(product);
        return productDao.insert(product);
    }

    public void update(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        validate(product);
        if (productDao.update(product) != 1) {
            throw new IllegalArgumentException("商品不存在");
        }
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        productDao.deleteById(id);
    }

    private void validate(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("商品名不能为空");
        }
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new IllegalArgumentException("价格不合法");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new IllegalArgumentException("库存不合法");
        }
    }
}
