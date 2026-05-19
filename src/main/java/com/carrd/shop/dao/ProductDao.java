package com.carrd.shop.dao;

import com.carrd.shop.entity.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private final DataSource ds;

    public ProductDao(DataSource ds) {
        this.ds = ds;
    }

    // TODO-DB: 表名/字段若不同，改 SQL；cover_url -> coverUrl 映射
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM t_product";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> findPage(int offset, int pageSize) {
        String sql = "SELECT id, name, price, stock, cover_url FROM t_product ORDER BY id DESC LIMIT ?,?";
        List<Product> list = new ArrayList<>();

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, offset);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setStock(rs.getInt("stock"));
                    p.setCoverUrl(rs.getString("cover_url"));
                    list.add(p);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> findAll() {
        String sql = "SELECT id, name, price, stock, cover_url FROM t_product ORDER BY id DESC";
        List<Product> list = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product findById(Long id) {
        try (Connection c = ds.getConnection()) {
            return findById(c, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product findById(Connection conn, Long id) {
        String sql = "SELECT id, name, price, stock, cover_url FROM t_product WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapProduct(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long insert(Product product) {
        String sql = "INSERT INTO t_product(name, price, stock, cover_url) VALUES(?,?,?,?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setString(4, product.getCoverUrl());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(Product product) {
        String sql = "UPDATE t_product SET name=?, price=?, stock=?, cover_url=? WHERE id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setString(4, product.getCoverUrl());
            ps.setLong(5, product.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM t_product WHERE id=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int decreaseStock(Connection conn, Long productId, int quantity) {
        String sql = "UPDATE t_product SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setCoverUrl(rs.getString("cover_url"));
        return p;
    }
}
