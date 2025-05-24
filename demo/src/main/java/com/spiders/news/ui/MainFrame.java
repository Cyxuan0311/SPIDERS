// MainFrame.java
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

        // ============ 新增图标设置代码 ============
        try {
            // 通过类加载器获取资源（兼容打包后的JAR）
            URL iconUrl = getClass().getResource("/image/spiders.png");

            if (iconUrl != null) {
                ImageIcon spiderIcon = new ImageIcon(iconUrl);

                // 设置窗口图标（支持所有平台）
                this.setIconImage(spiderIcon.getImage());

                // MacOS 额外设置任务栏图标
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    try {
                        Taskbar.getTaskbar().setIconImage(spiderIcon.getImage());
                    } catch (UnsupportedOperationException e) {
                        System.err.println("当前系统不支持任务栏图标设置");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "图标文件未找到：/image/sharpicons_Spider.png",
                        "资源错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton crawlButton = new JButton("爬取新闻");
        JButton addButton = new JButton("添加新闻");
        JButton editButton = new JButton("编辑新闻");
        JButton deleteButton = new JButton("删除新闻");
        JButton importButton = new JButton("导入数据");
        JButton exportButton = new JButton("导出数据");
        JButton queryButton = new JButton("查询新闻");
        JButton refreshButton = new JButton("刷新");

        buttonPanel.add(crawlButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(queryButton);
        buttonPanel.add(refreshButton);

        String[] columnNames = { "ID", "标题", "通讯员", "来源", "阅读数", "发布时间", "审核人" };
        tableModel = new DefaultTableModel(columnNames, 0);
        newsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(newsTable);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // 添加事件监听器
        crawlButton.addActionListener(e -> {
            // 先让用户输入网址
            String baseUrl = JOptionPane.showInputDialog(this, "请输入要爬取的网站基础URL(如https://news.sina.com.cn/):");
            if (baseUrl != null && !baseUrl.isEmpty()) {
                // 再让用户输入页数
                String input = JOptionPane.showInputDialog(this, "请输入要爬取的页数:", "1");
                if (input != null && !input.isEmpty()) {
                    try {
                        int pages = Integer.parseInt(input);
                        newsController.crawlNews(baseUrl, pages);
                        loadNewsData();
                        JOptionPane.showMessageDialog(this, "爬取完成!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "爬取失败: " + ex.getMessage(), "错误",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });

        addButton.addActionListener(e -> {
            AddDialog dialog = new AddDialog(this);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadNewsData();
            }
        });

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
                JOptionPane.showMessageDialog(this, "请先选择一条新闻", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = newsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "确定要删除这条新闻吗?", "确认删除", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    newsController.deleteNews(id);
                    loadNewsData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一条新闻", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        importButton.addActionListener(e -> {
            ImportDialog dialog = new ImportDialog(this);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadNewsData();
            }
        });

        exportButton.addActionListener(e -> {
            ExportDialog dialog = new ExportDialog(this);
            dialog.setVisible(true);
        });

        queryButton.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog(this, "请输入标题关键词:");
            if (keyword != null && !keyword.isEmpty()) {
                List<News> result = newsController.queryNews(keyword);
                updateTable(result);
            }
        });

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
}