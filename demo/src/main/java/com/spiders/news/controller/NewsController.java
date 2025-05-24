// NewsController.java
package com.spiders.news.controller;

import com.spiders.news.model.News;
import com.spiders.news.service.NewsService;

import java.util.List;

public class NewsController {
    private NewsService service = new NewsService();

    public void crawlNews(String baseUrl,int pages) throws Exception {
        service.crawlAndSave(baseUrl,pages);
    }

    public void addNews(News news) {
        service.add(news);
    }

    public void updateNews(News news) {
        service.update(news);
    }

    public void deleteNews(int id) {
        service.delete(id);
    }

    public News getNewsById(int id) {
        return service.getById(id);
    }

    public List<News> getAllNews() {
        return service.getAll();
    }

    public List<News> queryNews(String keyword) {
        return service.search(keyword);
    }
}