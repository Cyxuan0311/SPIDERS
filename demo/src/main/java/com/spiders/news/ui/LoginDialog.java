package com.spiders.news.ui;

import com.spiders.news.dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginDialog extends JDialog {
    private boolean loggedIn = false;
    
    public LoginDialog(JFrame parent) {
        super(parent, "用户登录", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        // 设置窗口图标
        try {
            URL iconUrl = getClass().getResource("/image/spiders.png");
            if (iconUrl != null) {
                ImageIcon spiderIcon = new ImageIcon(iconUrl);
                this.setIconImage(spiderIcon.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        JLabel userLabel = new JLabel("用户名:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("密码:");
        JPasswordField passField = new JPasswordField();
        
        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        
        JButton loginBtn = new JButton("登录");
        JButton registerBtn = new JButton("注册");
        
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                if (UserDAO.validateUser(username, password)) {
                    loggedIn = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                if (UserDAO.checkUserExists(username)) {
                    JOptionPane.showMessageDialog(this, "用户名已存在", "注册失败", JOptionPane.ERROR_MESSAGE);
                } else if (UserDAO.registerUser(username, password)) {
                    JOptionPane.showMessageDialog(this, "注册成功，请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    public boolean isLoggedIn() {
        return loggedIn;
    }
}
