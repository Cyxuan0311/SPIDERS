package com.spiders.news.ui;

import com.spiders.news.controller.NewsController;
import com.spiders.news.model.News;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.net.URL;

public class MainFrame extends JFrame {
    // 声明所有按钮成员变量
    private JButton crawlButton;
    private JButton dynamicCrawlButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton importButton;
    private JButton exportButton;
    private JButton queryButton;
    private JButton refreshButton;
    private JButton exitButton;
    private JButton detailCrawlButton;

    private NewsController newsController;
    private JTable newsTable;
    private DefaultTableModel tableModel;

    public MainFrame() {
        newsController = new NewsController();
        initUI();
        loadNewsData();
    }

    private void initUI() {
        setTitle("SPIDERS");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 设置窗口图标
        try {
            URL iconUrl = getClass().getResource("/image/spiders.png");
            if (iconUrl != null) {
                ImageIcon spiderIcon = new ImageIcon(iconUrl);
                this.setIconImage(spiderIcon.getImage());

                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    try {
                        Taskbar.getTaskbar().setIconImage(spiderIcon.getImage());
                    } catch (UnsupportedOperationException e) {
                        System.err.println("当前系统不支持任务栏图标设置");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "图标文件未找到：/image/spiders.png",
                        "资源错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 9));
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        // 创建并初始化所有按钮
        crawlButton = createSmallButton("静态爬取");
        dynamicCrawlButton = createSmallButton("动态爬取");
        detailCrawlButton = createSmallButton("详情页爬取");
        addButton = createSmallButton("添加新闻");
        editButton = createSmallButton("编辑新闻");
        deleteButton = createSmallButton("删除新闻");
        importButton = createSmallButton("导入数据");
        exportButton = createSmallButton("导出数据");
        queryButton = createSmallButton("查询新闻");
        refreshButton = createSmallButton("刷新");
        exitButton = createSmallButton("退出");

        // 添加按钮到面板
        topButtonPanel.add(crawlButton);
        topButtonPanel.add(dynamicCrawlButton);
        topButtonPanel.add(detailCrawlButton);
        topButtonPanel.add(addButton);
        topButtonPanel.add(editButton);
        topButtonPanel.add(deleteButton);
        topButtonPanel.add(importButton);
        topButtonPanel.add(exportButton);
        topButtonPanel.add(queryButton);

        bottomButtonPanel.add(refreshButton);
        bottomButtonPanel.add(exitButton);

        buttonPanel.add(topButtonPanel, BorderLayout.NORTH);
        buttonPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        // 初始化表格
        String[] columnNames = { "ID", "标题", "通讯员", "来源", "阅读数", "发布时间", "审核人" };
        tableModel = new DefaultTableModel(columnNames, 0);
        newsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(newsTable);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // 绑定事件监听器
        bindEventListeners();
    }

    private void bindEventListeners() {
        // 爬取新闻按钮事件
        crawlButton.addActionListener(e -> {
            String baseUrl = JOptionPane.showInputDialog(this, "请输入要爬取的网站基础URL(如https://news.sina.com.cn/):");
            if (baseUrl != null && !baseUrl.isEmpty()) {
                String input = JOptionPane.showInputDialog(this, "请输入要爬取的页数:", "1");
                if (input != null && !input.isEmpty()) {
                    try {
                        int pages = Integer.parseInt(input);
                        newsController.crawlNews(baseUrl, pages);
                        loadNewsData();
                        JOptionPane.showMessageDialog(this, "爬取完成!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "爬取失败: " + ex.getMessage(),
                                "错误", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });

        // 动态爬取按钮事件
        dynamicCrawlButton.addActionListener(e -> {
            String url = JOptionPane.showInputDialog(this, "请输入动态页面URL:");
            if (url != null && !url.isEmpty()) {
                try {
                    newsController.crawlDynamicNews(url);
                    loadNewsData();
                    JOptionPane.showMessageDialog(this, "动态爬取完成!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "爬取失败: " + ex.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 添加新闻按钮事件
        addButton.addActionListener(e -> {
            AddDialog dialog = new AddDialog(this);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadNewsData();
            }
        });

        // 编辑新闻按钮事件
        editButton.addActionListener(e -> {
            int selectedRow = newsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                News news = newsController.getNewsById(id);
                EditDialog dialog = new EditDialog(this, news);
                dialog.setVisible(true);
                if (dialog.isSuccess()) {
                    loadNewsData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一条新闻",
                        "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 删除新闻按钮事件
        deleteButton.addActionListener(e -> {
            int selectedRow = newsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "确定要删除这条新闻吗?", "确认删除", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    newsController.deleteNews(id);
                    loadNewsData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一条新闻",
                        "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 导入数据按钮事件
        importButton.addActionListener(e -> {
            ImportDialog dialog = new ImportDialog(this);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadNewsData();
            }
        });

        // 导出数据按钮事件
        exportButton.addActionListener(e -> {
            ExportDialog dialog = new ExportDialog(this);
            dialog.setVisible(true);
        });

        // 查询新闻按钮事件
        queryButton.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog(this, "请输入标题关键词:");
            if (keyword != null && !keyword.isEmpty()) {
                List<News> result = newsController.queryNews(keyword);
                updateTable(result);
            }
        });

        // 退出按钮事件
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定要退出程序吗?",
                    "确认退出",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        detailCrawlButton.addActionListener(e -> {
            String url = JOptionPane.showInputDialog(this, "请输入详情页URL:");
            if (url != null && !url.isEmpty()) {
                try {
                    newsController.crawlDetailPage(url);
                    loadNewsData();
                    JOptionPane.showMessageDialog(this, "详情页爬取完成!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "详情页爬取失败: " + ex.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 刷新按钮事件
        refreshButton.addActionListener(e -> loadNewsData());
    }

    private void loadNewsData() {
        List<News> newsList = newsController.getAllNews();
        updateTable(newsList);
    }

    private void updateTable(List<News> newsList) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (News news : newsList) {
            Object[] rowData = {
                    news.getId(),
                    news.getTitle(),
                    news.getContributor(),
                    news.getSource(),
                    news.getReadCount(),
                    sdf.format(news.getPublishTime()),
                    news.getReviewer()
            };
            tableModel.addRow(rowData);
        }
    }

    // 创建按钮工厂方法
    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(80, 25));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        button.setMargin(new Insets(2, 5, 2, 5));

        // 如果是退出按钮，设置红色背景和白色文字
        if ("退出".equals(text)) {
            button.setForeground(Color.RED);
        }

        return button;
    }

}