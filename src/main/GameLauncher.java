package main;

import core.GamePanel;
import javax.swing.*;

public class GameLauncher {
    public static void main(String[] args) {
        JFrame window = new JFrame("ZeldaRPG - Chronicles of the Lost Kingdom");
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setResizable(true);

        GamePanel gamePanel = new GamePanel();
        
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (gamePanel.saveManager != null && gamePanel.player != null && gamePanel.player.alive) {
                    gamePanel.saveManager.save(0);
                }
                System.exit(0);
            }
        });
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.requestFocus();
        gamePanel.startGameThread();
    }
}
