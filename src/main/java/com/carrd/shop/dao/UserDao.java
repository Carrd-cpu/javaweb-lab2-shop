package com.carrd.shop.dao;

import com.carrd.shop.entity.User;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {
    private final DataSource ds;

    public UserDao(DataSource ds) {
        this.ds = ds;
    }

    // TODO-DB: 表名/字段若与你不同，改这里 SQL
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT id, username FROM t_user WHERE username=? AND password=?";

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                return u;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
