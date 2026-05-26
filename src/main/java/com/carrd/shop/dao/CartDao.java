package com.carrd.shop.dao;

import com.carrd.shop.entity.CartItemVO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDao {
    private final DataSource ds;

    public CartDao(DataSource ds) {
        this.ds = ds;
    }

    public Integer findQuantity(Long userId, Long productId) {
        // TODO-DB: 表名字段若不同改 SQL
        String sql = "SELECT quantity FROM t_cart_item WHERE user_id=? AND product_id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getInt("quantity");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Long userId, Long productId, int quantity) {
        String sql = "INSERT INTO t_cart_item(user_id, product_id, quantity) VALUES(?,?,?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateQuantity(Long userId, Long productId, int quantity) {
        String sql = "UPDATE t_cart_item SET quantity=? WHERE user_id=? AND product_id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, userId);
            ps.setLong(3, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Long userId, Long productId) {
        String sql = "DELETE FROM t_cart_item WHERE user_id=? AND product_id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long count(Long userId) {
        String sql = "SELECT COUNT(*) FROM t_cart_item WHERE user_id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CartItemVO> findPage(Long userId, int offset, int pageSize) {
        // TODO-DB: 表名字段若不同改 SQL
        String sql = """
            SELECT p.id AS productId, p.name AS productName, p.price AS price, c.quantity AS quantity
            FROM t_cart_item c
            JOIN t_product p ON c.product_id = p.id
            WHERE c.user_id = ?
            ORDER BY c.id DESC
            LIMIT ?,?
            """;

        List<CartItemVO> list = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItemVO vo = new CartItemVO();
                    vo.setProductId(rs.getLong("productId"));
                    vo.setProductName(rs.getString("productName"));
                    vo.setPrice(rs.getBigDecimal("price"));
                    vo.setQuantity(rs.getInt("quantity"));
                    list.add(vo);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CartItemVO> findAllByUserId(Connection conn, Long userId) {
        String sql = """
            SELECT p.id AS productId, p.name AS productName, p.price AS price, c.quantity AS quantity
            FROM t_cart_item c
            JOIN t_product p ON c.product_id = p.id
            WHERE c.user_id = ?
            ORDER BY c.id DESC
            """;
        List<CartItemVO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItemVO vo = new CartItemVO();
                    vo.setProductId(rs.getLong("productId"));
                    vo.setProductName(rs.getString("productName"));
                    vo.setPrice(rs.getBigDecimal("price"));
                    vo.setQuantity(rs.getInt("quantity"));
                    list.add(vo);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearByUserId(Connection conn, Long userId) {
        String sql = "DELETE FROM t_cart_item WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
