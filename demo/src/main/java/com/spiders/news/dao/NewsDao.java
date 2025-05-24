// NewsDao.java
package com.spiders.news.dao;

import com.spiders.news.model.News;
import java.util.List;

public interface NewsDao {
    void insert(News news);
    void update(News news);
    void delete(int id);
    News findById(int id);
    List<News> findAll();
    List<News> findByKeyword(String keyword);
}