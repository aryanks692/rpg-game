package ui.menu;

import core.GamePanel;
import java.awt.*;

public class GameOverScreen {

    private Font hudFont = new Font("Arial", Font.BOLD, 13);

    public void draw(Graphics2D g2, GamePanel gp) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        g2.setFont(new Font("Georgia", Font.BOLD, 60));
        g2.setColor(new Color(200, 30, 30));
        FontMetrics fm = g2.getFontMetrics();
        String text = "GAME OVER";
        g2.drawString(text, gp.screenWidth / 2 - fm.stringWidth(text) / 2, gp.screenHeight / 2 - 20);
        
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Press ENTER to return to title", gp.screenWidth / 2 - 90, gp.screenHeight / 2 + 40);
    }
}
