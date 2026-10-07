package ui.menu;

import core.GamePanel;
import java.awt.*;

public class WinScreen {

    private Font hudFont = new Font("Arial", Font.BOLD, 13);

    public void draw(Graphics2D g2, GamePanel gp) {
        GradientPaint gp2 = new GradientPaint(0, 0, new Color(20, 10, 40), 0, gp.screenHeight, new Color(80, 40, 120));
        g2.setPaint(gp2);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        g2.setFont(new Font("Georgia", Font.BOLD, 48));
        GradientPaint gold = new GradientPaint(0, gp.screenHeight/2 - 60, new Color(255, 220, 80), 0, gp.screenHeight/2, new Color(200, 120, 30));
        g2.setPaint(gold);
        String text = "VICTORY!";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, gp.screenWidth / 2 - fm.stringWidth(text) / 2, gp.screenHeight / 2 - 20);
        
        g2.setFont(hudFont);
        g2.setColor(new Color(220, 220, 255));
        g2.drawString("You have conquered the Lost Kingdom!", gp.screenWidth / 2 - 130, gp.screenHeight / 2 + 30);
        g2.drawString("Press ENTER to return to title", gp.screenWidth / 2 - 100, gp.screenHeight / 2 + 60);
    }
}
