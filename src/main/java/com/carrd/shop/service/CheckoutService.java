package com.carrd.shop.service;

import com.carrd.shop.dao.CartDao;
import com.carrd.shop.dao.OrderDao;
import com.carrd.shop.dao.OrderItemDao;
import com.carrd.shop.dao.ProductDao;
import com.carrd.shop.entity.CartItemVO;
import com.carrd.shop.entity.Order;
import com.carrd.shop.entity.OrderItem;
import com.carrd.shop.entity.Product;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CheckoutService {
    private final DataSource ds;
    private final CartDao cartDao;
    private final ProductDao productDao;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;

    public CheckoutService(DataSource ds, CartDao cartDao, ProductDao productDao, OrderDao orderDao, OrderItemDao orderItemDao) {
        this.ds = ds;
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }

    public Long checkout(Long userId) {
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<CartItemVO> cartItems = cartDao.findAllByUserId(conn, userId);
                if (cartItems.isEmpty()) {
                    throw new IllegalArgumentException("购物车为空");
                }

                BigDecimal total = BigDecimal.ZERO;
                List<OrderItem> orderItems = new ArrayList<>();

                for (CartItemVO cartItem : cartItems) {
                    int qty = cartItem.getQuantity() == null ? 0 : cartItem.getQuantity();
                    if (qty <= 0) {
                        throw new IllegalArgumentException("购物车数量无效");
                    }
                    Product product = productDao.findById(conn, cartItem.getProductId());
                    if (product == null) {
                        throw new IllegalStateException("商品不存在: " + cartItem.getProductId());
                    }
                    if (product.getStock() < qty) {
                        throw new IllegalStateException("库存不足: " + product.getName());
                    }
                    if (productDao.decreaseStock(conn, cartItem.getProductId(), qty) != 1) {
                        throw new IllegalStateException("库存不足: " + product.getName());
                    }

                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(product.getId());
                    orderItem.setProductNameSnapshot(product.getName());
                    orderItem.setPriceSnapshot(product.getPrice());
                    orderItem.setQuantity(qty);
                    orderItems.add(orderItem);
                    total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
                }

                Order order = new Order();
                order.setUserId(userId);
                order.setTotalAmount(total);
                order.setStatus("CREATED");
                Long orderId = orderDao.insert(conn, order);
                if (orderId == null || orderId <= 0) {
                    throw new IllegalStateException("创建订单失败");
                }

                for (OrderItem orderItem : orderItems) {
                    orderItem.setOrderId(orderId);
                }
                orderItemDao.insertBatch(conn, orderItems);
                cartDao.clearByUserId(conn, userId);

                conn.commit();
                return orderId;
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
