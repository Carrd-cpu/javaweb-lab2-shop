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
}
