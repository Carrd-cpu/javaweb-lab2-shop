package com.carrd.shop.service;

import com.carrd.shop.dao.OrderDao;
import com.carrd.shop.dao.OrderItemDao;
import com.carrd.shop.entity.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminOrderService {
    private static final Set<String> ALLOWED_STATUS = Set.of("CREATED", "PAID", "SHIPPED", "DONE", "CANCELED");

    private final DataSource ds;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;

    public AdminOrderService(DataSource ds, OrderDao orderDao, OrderItemDao orderItemDao) {
        this.ds = ds;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }

    public List<Order> listAllOrders() {
        return orderDao.findAll();
    }

    public Map<String, Object> getOrderDetail(Long orderId) {
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return null;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("order", order);
        data.put("items", orderItemDao.findByOrderId(orderId));
        return data;
    }

    public void updateStatus(Long orderId, String status) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId不能为空");
        }
        if (status == null || !ALLOWED_STATUS.contains(status)) {
            throw new IllegalArgumentException("状态不合法");
        }
        if (orderDao.updateStatus(orderId, status) != 1) {
            throw new IllegalArgumentException("订单不存在");
        }
    }

    public void deleteOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId不能为空");
        }
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try {
                orderItemDao.deleteByOrderId(conn, orderId);
                if (orderDao.deleteById(conn, orderId) != 1) {
                    throw new IllegalArgumentException("订单不存在");
                }
                conn.commit();
            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException(e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
