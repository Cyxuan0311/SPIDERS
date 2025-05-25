package com.spiders.news.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.spiders.news.model.News;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class CrawlerServiceTest {

    @Mock
    private WebDriver mockDriver;
    
    @Mock
    private WebElement mockWebElement;
    
    @Mock
    private Document mockDoc;
    
    @Mock
    private Element mockElement;
    
    @Mock
    private Connection mockConnection;
    
    private CrawlerService crawlerService;
    
    @BeforeEach
    void setUp() {
        crawlerService = new CrawlerService(mockDriver);
        
        // 初始化链式调用模拟
        when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
        when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
        //when(mockConnection.get()).thenReturn(mockDoc);
    }

    @Test
    void testCrawlDynamicPage() {
        // 模拟WebDriver行为
        when(mockDriver.findElements(any())).thenReturn(List.of(mockWebElement));
        when(mockWebElement.findElement(any())).thenReturn(mockWebElement);
        when(mockWebElement.getText())
            .thenReturn("测试标题")
            .thenReturn("测试内容");
            
        // 执行测试
        assertDoesNotThrow(() -> {
            List<News> result = crawlerService.crawlDynamicPage("http://test.com");
            
            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("测试标题", result.get(0).getTitle());
        });
    }

    @Test
    void testCrawlArticle() {
        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)) {
            // 设置静态方法模拟
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            
            // 设置文档内容模拟
            when(mockDoc.selectFirst(anyString())).thenReturn(mockElement);
            when(mockElement.text()).thenReturn("测试内容");
            
            // 执行测试
            assertDoesNotThrow(() -> {
                News result = crawlerService.crawlArticle("http://test.com");
                
                // 验证结果
                assertNotNull(result);
                assertEquals("测试内容", result.getContent());
                
                // 验证交互
                verify(mockConnection).userAgent(anyString());
                verify(mockConnection).timeout(10000);
            });
        }
    }
    
    @Test
    void testGetRandomUserAgent() {
        // 测试随机性
        Set<String> agents = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            agents.add(CrawlerService.getRandomUserAgent());
        }
        assertTrue(agents.size() > 1, "UserAgent should have variations");
    }
    
    @Test
    void testCrawlArticleWithException() {
        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)) {
            // 模拟异常情况
            mockedJsoup.when(() -> Jsoup.connect(anyString()))
                      .thenThrow(new IOException("Connection failed"));
            
            // 验证异常抛出
            IOException exception = assertThrows(IOException.class, () -> {
                crawlerService.crawlArticle("http://invalid.com");
            });
            assertEquals("Connection failed", exception.getMessage());
        }
    }
}