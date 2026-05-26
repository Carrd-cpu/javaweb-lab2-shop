package com.carrd.shop.service;

import com.carrd.shop.dao.OrderDao;
import com.carrd.shop.dao.OrderItemDao;
import com.carrd.shop.entity.Order;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;

    public OrderService(OrderDao orderDao, OrderItemDao orderItemDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }

    public List<Order> listMyOrders(Long userId) {
        return orderDao.findByUserId(userId);
    }

    public Map<String, Object> getMyOrderDetail(Long userId, Long orderId) {
        Order order = orderDao.findById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            return null;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("order", order);
        data.put("items", orderItemDao.findByOrderId(orderId));
        return data;
    }
}
