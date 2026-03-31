package main;

import core.GamePanel;
import javax.swing.*;

public class GameLauncher {
    public static void main(String[] args) {
        JFrame window = new JFrame("ZeldaRPG - Chronicles of the Lost Kingdom");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.requestFocus();
        gamePanel.startGameThread();
    }
}
