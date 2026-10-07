package ui.hud;

import core.GamePanel;
import java.awt.*;

public class MinimapRenderer {

    public void draw(Graphics2D g2, GamePanel gp) {
        int mmW = 120, mmH = 90;
        int mmX = gp.screenWidth - mmW - 10;
        int mmY = gp.screenHeight - mmH - 10;

        // Background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(mmX - 2, mmY - 2, mmW + 4, mmH + 4, 8, 8);
        g2.setColor(new Color(100, 80, 40));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(mmX - 2, mmY - 2, mmW + 4, mmH + 4, 8, 8);

        // Draw simplified world
        float scaleX = (float)mmW / (gp.maxWorldCol * gp.tileSize);
        float scaleY = (float)mmH / (gp.maxWorldRow * gp.tileSize);
        
        // Zone colors
        g2.setColor(new Color(60, 120, 50));
        g2.fillRect(mmX, mmY, mmW, mmH);
        
        // Water
        g2.setColor(new Color(30, 80, 180, 200));
        drawMMRect(g2, mmX, mmY, scaleX, scaleY, gp, 22, 4, 10, 8);
        
        // Dark forest
        g2.setColor(new Color(20, 60, 20));
        drawMMRect(g2, mmX, mmY, scaleX, scaleY, gp, 33, 1, gp.maxWorldCol - 34, 24);
        
        // Caves
        g2.setColor(new Color(50, 40, 70));
        drawMMRect(g2, mmX, mmY, scaleX, scaleY, gp, 2, 30, 18, gp.maxWorldRow - 31);
        
        // Ruins
        g2.setColor(new Color(80, 70, 50));
        drawMMRect(g2, mmX, mmY, scaleX, scaleY, gp, 30, 28, gp.maxWorldCol - 31, gp.maxWorldRow - 29);

        // Player dot
        int pdx = mmX + (int)(gp.player.worldX * scaleX);
        int pdy = mmY + (int)(gp.player.worldY * scaleY);
        g2.setColor(Color.WHITE);
        g2.fillOval(pdx - 3, pdy - 3, 7, 7);
        g2.setColor(new Color(100, 200, 255));
        g2.fillOval(pdx - 2, pdy - 2, 5, 5);

        // Label
        g2.setFont(new Font("Arial", Font.BOLD, 8));
        g2.setColor(new Color(200, 190, 150));
        g2.drawString("MAP", mmX + 2, mmY + 9);

        g2.setStroke(new BasicStroke(1));
    }

    private void drawMMRect(Graphics2D g2, int mmX, int mmY, float sx, float sy, GamePanel gp, int col, int row, int cols, int rows) {
        int x = mmX + (int)(col * gp.tileSize * sx);
        int y = mmY + (int)(row * gp.tileSize * sy);
        int w = (int)(cols * gp.tileSize * sx);
        int h = (int)(rows * gp.tileSize * sy);
        g2.fillRect(x, y, w, h);
    }
}
