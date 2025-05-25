package com.spiders.news.service;

import com.spiders.news.model.News;
import org.jsoup.Jsoup;
import java.time.Duration;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;


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
    private WebDriver driver;
    private static final SimpleDateFormat SINA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public CrawlerService() {
        // 初始化WebDriver
        System.setProperty("webdriver.chrome.driver", "demo\\src\\main\\resources\\lib\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 无头模式
        driver = new ChromeDriver(options);
    }

     public List<News> crawlDynamicPage(String url) {
        List<News> newsList = new ArrayList<>();
        try {
            driver.get(url);
            // 等待动态内容加载
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // 示例：获取动态加载的新闻列表
            List<WebElement> newsElements = wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.cssSelector(".news-item")));
            
            for (WebElement element : newsElements) {
                News news = new News();
                news.setTitle(element.findElement(By.cssSelector(".title")).getText());
                news.setContent(element.findElement(By.cssSelector(".content")).getText());
                // 其他字段...
                newsList.add(news);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsList;
    }
    

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

     public News crawlDetailPage(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent(getRandomUserAgent())
                .timeout(TIMEOUT)
                .get();

        News news = new News();
        
        // 提取详情页特有信息
        Element contentEl = doc.selectFirst("#article-content");
        if (contentEl != null) {
            news.setContent(contentEl.text().trim());
        }
        
        // 可以添加更多详情页特有的字段提取逻辑
        Element viewCountEl = doc.selectFirst(".view-count");
        if (viewCountEl != null) {
            news.setViewCount(Integer.parseInt(viewCountEl.text().replaceAll("\\D+", "")));
        }

        return news;
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

    public News crawlArticle(String articleUrl) throws IOException {
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

    public static String getRandomUserAgent() {
        String[] userAgents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"
        };
        return userAgents[random.nextInt(userAgents.length)];
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public CrawlerService(WebDriver driver) {
        this.driver = driver;
    }
}