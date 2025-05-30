package com.spiders.news.dao;

import com.spiders.news.util.DBUtil;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static int loginAttempts = 0;
    
    // MD5加密方法
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    public static boolean checkUserExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public static boolean registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, md5(password)); // 密码加密存储
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean validateUser(String username, String password) throws SQLException {
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            System.err.println("登录尝试次数过多，程序即将退出");
            System.exit(1);
        }
        
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            boolean isValid = rs.next() && rs.getString("password").equals(md5(password));
            if (!isValid) {
                loginAttempts++;
                System.err.println("登录失败，剩余尝试次数: " + (MAX_LOGIN_ATTEMPTS - loginAttempts));
            } else {
                loginAttempts = 0; // 登录成功重置计数器
            }
            return isValid;
        }
    }
}
