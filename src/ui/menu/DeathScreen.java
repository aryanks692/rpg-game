package ui.menu;

import core.GamePanel;
import java.awt.*;

public class DeathScreen {

    private Font hudFont = new Font("Arial", Font.BOLD, 13);

    public void draw(Graphics2D g2, GamePanel gp) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        g2.setFont(new Font("Georgia", Font.BOLD, 60));
        g2.setColor(new Color(200, 30, 30));
        FontMetrics fm = g2.getFontMetrics();
        String text = "YOU DIED";
        g2.drawString(text, gp.screenWidth / 2 - fm.stringWidth(text) / 2, gp.screenHeight / 2 - 40);
        
        g2.setFont(hudFont);
        if (gp.saveManager.hasAnySave()) {
            g2.setColor(new Color(220, 220, 255));
            String t1 = "Press ENTER to Restore from Save";
            g2.drawString(t1, gp.screenWidth / 2 - fm.stringWidth(t1) / 2, gp.screenHeight / 2 + 20);
        }
        
        g2.setColor(new Color(255, 100, 100));
        String t2 = "Press R to Start a New Game";
        g2.drawString(t2, gp.screenWidth / 2 - fm.stringWidth(t2) / 2, gp.screenHeight / 2 + 50);
    }
}
