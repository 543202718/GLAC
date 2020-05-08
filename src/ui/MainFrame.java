/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * 主界面，采用单例模式。
 * @author Wang
 */
public class MainFrame extends JFrame {

    private static MainFrame instance;

    private final JTabbedPane tabbedPane;
    
    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    private MainFrame() {
        setTitle("GLAC");
        setBounds(100, 100, 800, 800);
        tabbedPane=new JTabbedPane();
        tabbedPane.setFont(new Font("Calibri", Font.BOLD, 18));
        tabbedPane.addTab("Online Tracking",null, OnlinePanel.getInstance());
        tabbedPane.addTab("Offline Tracking", null, OfflinePanel.getInstance());
        tabbedPane.addTab("Simulation", null, SimulationPanel.getInstance());
        tabbedPane.addTab("Batch Processing", null, BatchPanel.getInstance());
        tabbedPane.addTab("Config Setting", null, SettingPanel.getInstance());
        setContentPane(tabbedPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
