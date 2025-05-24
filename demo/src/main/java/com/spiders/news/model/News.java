// News.java
package com.spiders.news.model;

import java.util.Date;

public class News {
    private int id;
    private String title;
    private String contributor;
    private String source;
    private int readCount;
    private Date publishTime;
    private String reviewer;
    private String content;

    public News() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContributor() { return contributor; }
    public void setContributor(String contributor) { this.contributor = contributor; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public int getReadCount() { return readCount; }
    public void setReadCount(int readCount) { this.readCount = readCount; }

    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }

    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}