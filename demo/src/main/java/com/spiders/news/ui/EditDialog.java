// EditDialog.java
package com.spiders.news.ui;

import com.spiders.news.controller.NewsController;
import com.spiders.news.model.News;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class EditDialog extends JDialog {
    private boolean success = false;
    private NewsController newsController;
    private News news;
    
    private JTextField titleField;
    private JTextField contributorField;
    private JTextField sourceField;
    private JTextField readCountField;
    private JTextField publishTimeField;
    private JTextField reviewerField;
    private JTextArea contentArea;

    public EditDialog(JFrame parent, News news) {
        super(parent, "编辑新闻", true);
        this.news = news;
        newsController = new NewsController();
        initUI();
    }

    private void initUI() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        
        titleField = new JTextField(news.getTitle());
        contributorField = new JTextField(news.getContributor());
        sourceField = new JTextField(news.getSource());
        readCountField = new JTextField(String.valueOf(news.getReadCount()));
        publishTimeField = new JTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(news.getPublishTime()));
        reviewerField = new JTextField(news.getReviewer());
        contentArea = new JTextArea(news.getContent(), 5, 20);
        
        formPanel.add(new JLabel("标题:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("通讯员:"));
        formPanel.add(contributorField);
        formPanel.add(new JLabel("来源:"));
        formPanel.add(sourceField);
        formPanel.add(new JLabel("阅读数:"));
        formPanel.add(readCountField);
        formPanel.add(new JLabel("发布时间:"));
        formPanel.add(publishTimeField);
        formPanel.add(new JLabel("审核人:"));
        formPanel.add(reviewerField);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            try {
                news.setTitle(titleField.getText());
                news.setContributor(contributorField.getText());
                news.setSource(sourceField.getText());
                news.setReadCount(Integer.parseInt(readCountField.getText()));
                news.setPublishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(publishTimeField.getText()));
                news.setReviewer(reviewerField.getText());
                news.setContent(contentArea.getText());
                
                newsController.updateNews(news);
                success = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    public boolean isSuccess() { return success; }
}