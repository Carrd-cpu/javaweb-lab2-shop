package com.carrd.shop.service;

import com.carrd.shop.dao.CartDao;
import com.carrd.shop.entity.CartItemVO;
import com.carrd.shop.entity.PageResult;

import java.util.List;

public class CartService {
    private final CartDao dao;

    public CartService(CartDao dao) {
        this.dao = dao;
    }

    public void add(Long userId, Long productId, int quantity) {
        if (quantity <= 0) quantity = 1;

        Integer old = dao.findQuantity(userId, productId);
        if (old == null) {
            dao.insert(userId, productId, quantity);
        } else {
            dao.updateQuantity(userId, productId, old + quantity);
        }
    }

    public void update(Long userId, Long productId, int quantity) {
        if (quantity <= 0) quantity = 1;
        dao.updateQuantity(userId, productId, quantity);
    }

    public void delete(Long userId, Long productId) {
        dao.delete(userId, productId);
    }

    public PageResult<CartItemVO> page(Long userId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 5;
        if (pageSize > 50) pageSize = 50;

        int offset = (page - 1) * pageSize;

        long total = dao.count(userId);
        List<CartItemVO> list = dao.findPage(userId, offset, pageSize);

        PageResult<CartItemVO> pr = new PageResult<>();
        pr.setPage(page);
        pr.setPageSize(pageSize);
        pr.setTotal(total);
        pr.setList(list);
        return pr;
    }
}
