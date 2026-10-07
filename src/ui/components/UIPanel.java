package ui.components;

import java.awt.*;

public class UIPanel {

    /**
     * Draws a generic dark glass panel used across various menus.
     */
    public static void draw(Graphics2D g2, int x, int y, int w, int h) {
        // Dark glass panel
        g2.setColor(new Color(10, 10, 30, 220));
        g2.fillRoundRect(x, y, w, h, 16, 16);
        g2.setColor(new Color(80, 70, 140));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, w, h, 16, 16);
        
        // Inner highlight
        g2.setColor(new Color(255, 255, 255, 15));
        g2.fillRoundRect(x + 2, y + 2, w - 4, h / 3, 14, 14);
        
        g2.setStroke(new BasicStroke(1));
    }
}
