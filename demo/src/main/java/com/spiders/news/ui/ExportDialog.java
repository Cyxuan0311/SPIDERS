// ExportDialog.java
package com.spiders.news.ui;

import com.spiders.news.controller.NewsController;
import com.spiders.news.model.News;
import com.spiders.news.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ExportDialog extends JDialog {
    private JFileChooser fileChooser;
    private NewsController newsController;

    public ExportDialog(JFrame parent) {
        super(parent, "导出数据", true);
        newsController = new NewsController();
        initUI();
    }

    private void initUI() {
        setSize(300, 150);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV文件 (*.csv)", "csv"));
        
        JButton browseButton = new JButton("选择文件");
        JButton exportButton = new JButton("导出");
        JButton cancelButton = new JButton("取消");
        
        JLabel fileLabel = new JLabel("未选择文件");
        fileLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        browseButton.addActionListener(e -> {
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                // 确保文件有.csv扩展名
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }
                fileLabel.setText(file.getAbsolutePath());
            }
        });
        
        exportButton.addActionListener(e -> {
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                try {
                    List<News> newsList = newsController.getAllNews();
                    FileUtil.exportToCsv(newsList, file);
                    JOptionPane.showMessageDialog(this, "导出成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择文件", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(browseButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(fileLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
}