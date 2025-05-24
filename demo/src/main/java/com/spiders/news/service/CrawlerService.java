package com.spiders.news.service;

import com.spiders.news.model.News;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class CrawlerService {
    // private static final String BASE_URL = "https://news.sina.com.cn/";
    private static final int THREAD_POOL_SIZE = 3;
    private static final int TIMEOUT = 15000;
    private static final Random random = new Random();
    private static final SimpleDateFormat SINA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public List<News> crawlNews(String baseUrl, int pageCount) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<List<News>>> futures = new ArrayList<>();

        String[] channels = { "china", "world", "tech", "sports", "finance" };
        for (String channel : channels) {
            futures.add(executor.submit((Callable<List<News>>) () -> {
                try {
                    Thread.sleep(random.nextInt(2000));
                    return crawlChannel(baseUrl, channel, Math.min(3, pageCount));
                } catch (Exception e) {
                    System.err.println("Error crawling channel " + channel + ": " + e.getMessage());
                    return Collections.emptyList();
                }
            }));
        }

        List<News> allNews = new ArrayList<>();
        for (Future<List<News>> f : futures) {
            try {
                allNews.addAll(f.get());
            } catch (ExecutionException e) {
                System.err.println("Execution error: " + e.getMessage());
            }
        }
        executor.shutdown();
        return allNews;
    }

    private List<News> crawlChannel(String baseUrl, String channel, int pageCount) throws IOException {
        List<News> newsList = new ArrayList<>();
        String channelUrl = baseUrl + channel + "/";

        for (int i = 1; i <= pageCount; i++) {
            try {
                Document doc = Jsoup.connect(channelUrl)
                        .userAgent(getRandomUserAgent())
                        .timeout(TIMEOUT)
                        .get();

                // 新浪新闻首页的文章链接
                Elements newsLinks = doc.select(".news-item a[href^='https://news.sina.com.cn/']:not([href*='video'])");
                for (Element link : newsLinks) {
                    try {
                        String articleUrl = link.absUrl("href");
                        News news = crawlArticle(articleUrl);
                        if (news != null) {
                            newsList.add(news);
                            Thread.sleep(random.nextInt(1000) + 500); // 随机延迟
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing article: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to crawl channel " + channel + " page " + i + ": " + e.getMessage());
            }
        }
        return newsList;
    }

    private News crawlArticle(String articleUrl) throws IOException {
        Document doc = Jsoup.connect(articleUrl)
                .userAgent(getRandomUserAgent())
                .timeout(TIMEOUT)
                .get();

        News news = new News();

        // 标题
        Element titleEl = doc.selectFirst("h1.main-title");
        if (titleEl == null)
            titleEl = doc.selectFirst("#artibodyTitle");
        if (titleEl != null) {
            news.setTitle(titleEl.text().trim());
        } else {
            return null; // 如果没有标题，跳过这篇文章
        }

        // 日期和来源
        Element dateEl = doc.selectFirst(".date-source .date, .art_info .time-source");
        if (dateEl != null) {
            String dateText = dateEl.text().replace("年", "-").replace("月", "-").replace("日", "");
            try {
                Date date = SINA_DATE_FORMAT.parse(dateText);
                news.setPublishTime(date);
            } catch (ParseException e) {
                news.setPublishTime(new Date());
            }
        }

        Element sourceEl = doc.selectFirst(".date-source .source, .art_info .time-source .source");
        if (sourceEl != null) {
            news.setSource(sourceEl.text().trim());
        } else {
            news.setSource("新浪新闻");
        }

        // 内容
        Element contentEl = doc.selectFirst("#artibody, .article");
        if (contentEl != null) {
            // 移除不需要的元素
            contentEl.select(".img_wrapper, .video-wrapper, script, style").remove();
            news.setContent(contentEl.text().trim());
        }

        // 作者
        Element authorEl = doc.selectFirst(".show_author, .article-editor");
        if (authorEl != null) {
            String authorText = authorEl.text().replace("责任编辑：", "").trim();
            news.setContributor(authorText.isEmpty() ? "新浪记者" : authorText);
        } else {
            news.setContributor("新浪记者");
        }

        // 设置默认值
        news.setReviewer("新浪编辑");
        news.setReadCount(random.nextInt(100000));

        return news;
    }

    private static String getRandomUserAgent() {
        String[] userAgents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"
        };
        return userAgents[random.nextInt(userAgents.length)];
    }
}