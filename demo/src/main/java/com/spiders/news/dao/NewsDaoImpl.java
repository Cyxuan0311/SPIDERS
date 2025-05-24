// NewsDaoImpl.java
package com.spiders.news.dao;

import com.spiders.news.model.News;
import com.spiders.news.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsDaoImpl implements NewsDao {
    @Override
    public void insert(News news) {
        String sql = "INSERT INTO news(title, contributor, source, read_count, publish_time, reviewer, content) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getContributor());
            ps.setString(3, news.getSource());
            ps.setInt(4, news.getReadCount());
            ps.setTimestamp(5, new Timestamp(news.getPublishTime().getTime()));
            ps.setString(6, news.getReviewer());
            ps.setString(7, news.getContent());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(News news) {
        String sql = "UPDATE news SET title=?, contributor=?, source=?, read_count=?, publish_time=?, reviewer=?, content=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getContributor());
            ps.setString(3, news.getSource());
            ps.setInt(4, news.getReadCount());
            ps.setTimestamp(5, new Timestamp(news.getPublishTime().getTime()));
            ps.setString(6, news.getReviewer());
            ps.setString(7, news.getContent());
            ps.setInt(8, news.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM news WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public News findById(int id) {
        String sql = "SELECT * FROM news WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                News news = mapResultSet(rs);
                return news;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<News> findAll() {
        String sql = "SELECT * FROM news ORDER BY publish_time DESC";
        List<News> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<News> findByKeyword(String keyword) {
        String sql = "SELECT * FROM news WHERE title LIKE ? ORDER BY publish_time DESC";
        List<News> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private News mapResultSet(ResultSet rs) throws SQLException {
        News news = new News();
        news.setId(rs.getInt("id"));
        news.setTitle(rs.getString("title"));
        news.setContributor(rs.getString("contributor"));
        news.setSource(rs.getString("source"));
        news.setReadCount(rs.getInt("read_count"));
        news.setPublishTime(rs.getTimestamp("publish_time"));
        news.setReviewer(rs.getString("reviewer"));
        news.setContent(rs.getString("content"));
        return news;
    }
}