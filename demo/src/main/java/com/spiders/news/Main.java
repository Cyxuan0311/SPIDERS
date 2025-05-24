package com.spiders.news;

import com.spiders.news.ui.LoginDialog;
import com.spiders.news.ui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
            
            if (loginDialog.isLoggedIn()) {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}