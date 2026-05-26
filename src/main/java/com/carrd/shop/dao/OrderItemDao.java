package com.carrd.shop.dao;

import com.carrd.shop.entity.OrderItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDao {
    private final DataSource ds;

    public OrderItemDao(DataSource ds) {
        this.ds = ds;
    }

    public void insertBatch(Connection conn, List<OrderItem> items) {
        String sql = "INSERT INTO t_order_item(order_id, product_id, product_name_snapshot, price_snapshot, quantity) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderItem item : items) {
                ps.setLong(1, item.getOrderId());
                ps.setLong(2, item.getProductId());
                ps.setString(3, item.getProductNameSnapshot());
                ps.setBigDecimal(4, item.getPriceSnapshot());
                ps.setInt(5, item.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        String sql = """
            SELECT id, order_id, product_id, product_name_snapshot, price_snapshot, quantity
            FROM t_order_item
            WHERE order_id=?
            ORDER BY id ASC
            """;
        List<OrderItem> list = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setProductNameSnapshot(rs.getString("product_name_snapshot"));
                    item.setPriceSnapshot(rs.getBigDecimal("price_snapshot"));
                    item.setQuantity(rs.getInt("quantity"));
                    list.add(item);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteByOrderId(Connection conn, Long orderId) {
        String sql = "DELETE FROM t_order_item WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
