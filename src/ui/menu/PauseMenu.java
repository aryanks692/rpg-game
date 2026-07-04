package ui.menu;

import core.GamePanel;
import ui.components.UIPanel;
import java.awt.*;

public class PauseMenu {

    private Font hudFont = new Font("Arial", Font.BOLD, 13);

    public void draw(Graphics2D g2, GamePanel gp) {
        // Dark overlay
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Panel
        int pw = 300, ph = 200;
        int px = gp.screenWidth / 2 - pw / 2, py = gp.screenHeight / 2 - ph / 2;
        UIPanel.draw(g2, px, py, pw, ph);
        
        g2.setFont(new Font("Georgia", Font.BOLD, 28));
        g2.setColor(new Color(255, 220, 80));
        g2.drawString("⏸  PAUSED", px + 60, py + 50);
        
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("ESC  — Resume", px + 80, py + 90);
        g2.drawString("I    — Inventory", px + 80, py + 112);
        g2.drawString("Q    — Quest Log", px + 80, py + 134);
        g2.drawString("ENTER — Use Potion (" + gp.player.potionCount + ")", px + 80, py + 156);
    }
}
