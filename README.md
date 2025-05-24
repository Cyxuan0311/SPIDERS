# 🕷️ SPIDERS - 新闻爬虫系统 🕸️

![项目图标](demo/src/main/resources/image/spiders.png "蜘蛛图标")

## 📌 项目简介
本项目是一个基于Java的新闻爬虫应用，利用Jsoup库进行网页内容爬取🔍，将爬取到的新闻信息存储到MySQL数据库💾中。使用Swing UI 🖥️ 进行用户界面构建。同时，项目使用JUnit进行单元测试🧪，Lombok简化代码编写✏️。

## 🛠️ 技术栈
- **Jsoup 1.16.1**：用于HTML解析和网页内容爬取🕸️
- **MySQL Connector/J 8.0.33**：MySQL数据库连接驱动🔌
- **JUnit Jupiter 5.10.0**：用于单元测试✅
- **Lombok 1.18.30**：简化Java代码✨

## ⚙️ 环境要求
- ☕ Java 17 或更高版本
- 🧰 Maven 3.x 或更高版本
- 🐬 MySQL 8.0 或更高版本

## 🚀 安装步骤

### 1️⃣ 克隆项目
```bash
git clone [项目仓库地址]
cd Spider/demo
```

### 2️⃣ 数据库准备
执行 `../resources/SQL/news.sql` 脚本创建数据库和表：
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS news_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE news_db;

-- 创建新闻表
CREATE TABLE IF NOT EXISTS news (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title VARCHAR(255) NOT NULL COMMENT '新闻标题',
    url VARCHAR(512) NOT NULL COMMENT '新闻链接',
    content TEXT COMMENT '新闻正文',
    source VARCHAR(100) COMMENT '来源（如学校官网）',
    publish_time DATETIME COMMENT '发布时间',
    crawl_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '爬取时间',
    UNIQUE KEY uk_url (url)  -- 防止重复插入相同链接
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻表';
```

### 3️⃣ 配置数据库连接
在项目代码中配置数据库连接信息，配置文件`DBUtil.java`为：
```properties
url=jdbc:mysql://localhost:3306/news_spider?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
username=your_username
password=your_password
```

### 4️⃣ 构建项目
使用Maven构建项目：
```bash
mvn clean install
```

## 🎯 使用方法
### 🕷️ 运行爬虫
执行主程序启动新闻爬取任务：
```bash
mvn exec:java -Dexec.mainClass="com.spiders.news.Main"
```

### 🧪 运行测试
使用JUnit进行单元测试：
```bash
mvn test
```

### ⚠️ 注意事项
- 🔌 请确保MySQL服务已启动
- 🔍 数据库连接信息配置正确
- 🕸️ 网页结构可能会变化，爬虫代码可能需要调整
- 🤖 遵守网站的 robots.txt 规则
- ⏳ 避免对目标网站造成不必要的负担