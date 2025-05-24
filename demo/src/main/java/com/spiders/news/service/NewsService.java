// NewsService.java
package com.spiders.news.service;

import com.spiders.news.dao.NewsDao;
import com.spiders.news.dao.NewsDaoImpl;
import com.spiders.news.model.News;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class NewsService {
    private NewsDao dao = new NewsDaoImpl();
    private CrawlerService crawler = new CrawlerService();

    public void crawlAndSave(String baseUrl, int pages) throws InterruptedException, ExecutionException {
        List<News> list = crawler.crawlNews(baseUrl, pages);
        for (News n : list)
            dao.insert(n);
    }

    public void add(News news) {
        dao.insert(news);
    }

    public void update(News news) {
        dao.update(news);
    }

    public void delete(int id) {
        dao.delete(id);
    }

    public News getById(int id) {
        return dao.findById(id);
    }

    public List<News> getAll() {
        return dao.findAll();
    }

    public List<News> search(String kw) {
        return dao.findAll().stream()
                .filter(news -> news.getTitle().contains(kw))
                .collect(Collectors.toList());
    }

    public void crawlAndSaveDynamic(String url){
        List<News> newsList = crawler.crawlDynamicPage(url);
            for (News news : newsList) {
        dao.insert(news);
        }
    }

    public News crawlAndSaveDetail(String url) throws Exception {
        News news = crawler.crawlDetailPage(url);
        dao.insert(news);
        return news;
    }
}