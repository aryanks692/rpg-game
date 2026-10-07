package ui.hud;

import java.awt.*;

public class HeartRenderer {

    public void drawHeart(Graphics2D g2, int x, int y, int size, boolean filled) {
        Color fillColor = filled ? new Color(220, 50, 50) : new Color(60, 30, 30, 160);
        Color border    = filled ? new Color(255, 150, 150) : new Color(100, 60, 60, 120);
        
        g2.setColor(fillColor);
        // Heart shape using two circles + triangle
        int r = size / 3;
        g2.fillOval(x, y, r * 2, r * 2);
        g2.fillOval(x + r, y, r * 2, r * 2);
        int[] hx = {x, x + size, x + size/2};
        int[] hy = {y + r, y + r, y + size};
        g2.fillPolygon(hx, hy, 3);
        
        // Shine
        if (filled) {
            g2.setColor(new Color(255, 180, 180, 180));
            g2.fillOval(x + 2, y + 2, r - 2, r - 3);
        }
        
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(x, y, r * 2, r * 2);
        g2.drawOval(x + r, y, r * 2, r * 2);
        g2.drawPolygon(hx, hy, 3);
    }
}
