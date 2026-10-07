package ui;

import core.GamePanel;
import java.awt.*;

/**
 * Minimap rendered in the bottom-right corner.
 * Parchment-style decorative border, compass rose, player arrow.
 */
public class MiniMap {

    private final GamePanel gp;
    private static final int MM_W = 120;
    private static final int MM_H = 90;

    public MiniMap(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        int mmX = gp.screenWidth  - MM_W - 10;
        int mmY = gp.screenHeight - MM_H - 10;

        drawFrame(g2, mmX, mmY);
        drawTerrain(g2, mmX, mmY);
        drawEnemyDots(g2, mmX, mmY);
        drawPlayerArrow(g2, mmX, mmY);
        drawLabel(g2, mmX, mmY);
        drawCompassRose(g2, mmX + MM_W - 14, mmY + 14);
    }

    private void drawFrame(Graphics2D g2, int mmX, int mmY) {
        // Outer shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(mmX - 1, mmY - 1, MM_W + 8, MM_H + 8, 10, 10);
        // Dark map bg
        g2.setColor(new Color(12, 18, 10, 210));
        g2.fillRoundRect(mmX, mmY, MM_W, MM_H, 8, 8);
        // Parchment-style border: warm gold outer, dim inner
        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(new Color(160, 130, 50));
        g2.drawRoundRect(mmX - 2, mmY - 2, MM_W + 4, MM_H + 4, 10, 10);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(80, 60, 20, 140));
        g2.drawRoundRect(mmX + 2, mmY + 2, MM_W - 4, MM_H - 4, 6, 6);
        g2.setStroke(UIConstants.STROKE_THIN);
    }

    private void drawTerrain(Graphics2D g2, int mmX, int mmY) {
        float sx = (float) MM_W / (gp.maxWorldCol * gp.tileSize);
        float sy = (float) MM_H / (gp.maxWorldRow * gp.tileSize);

        // Grass base
        g2.setColor(new Color(50, 105, 40));
        g2.fillRect(mmX, mmY, MM_W, MM_H);

        // 1. Great Savannah (Top-Left & Top-Center)
        g2.setColor(new Color(170, 145, 60, 220));
        fillMMRect(g2, mmX, mmY, sx, sy, 1, 1, 55, 29);

        // 2. Dusty Gulch Wild West Town & Badlands (Top-Right)
        g2.setColor(new Color(195, 115, 65, 240));
        fillMMRect(g2, mmX, mmY, sx, sy, 57, 1, gp.maxWorldCol - 58, 29);
        // Town main street boardwalk indicator
        g2.setColor(new Color(230, 180, 100));
        fillMMRect(g2, mmX, mmY, sx, sy, 62, 14, 34, 4);

        // 3. Golden Meadows (Rows 30-38)
        g2.setColor(new Color(125, 160, 55, 180));
        fillMMRect(g2, mmX, mmY, sx, sy, 1, 30, gp.maxWorldCol - 2, 8);

        // 4. Verdant Village & Darkwood Forest (Rows 39-61)
        g2.setColor(new Color(65, 130, 50));
        fillMMRect(g2, mmX, mmY, sx, sy, 1, 39, 32, 22);
        g2.setColor(new Color(18, 52, 18));
        fillMMRect(g2, mmX, mmY, sx, sy, 33, 39, gp.maxWorldCol - 34, 22);

        // 5. Crystal Caves & Ancient Ruins (Rows 62-99)
        g2.setColor(new Color(50, 35, 70));
        fillMMRect(g2, mmX, mmY, sx, sy, 2, 62, 26, gp.maxWorldRow - 63);
        g2.setColor(new Color(75, 65, 45));
        fillMMRect(g2, mmX, mmY, sx, sy, 29, 62, gp.maxWorldCol - 30, gp.maxWorldRow - 63);
    }

    private void drawEnemyDots(Graphics2D g2, int mmX, int mmY) {
        if (gp.enemies == null) return;
        float sx = (float) MM_W / (gp.maxWorldCol * gp.tileSize);
        float sy = (float) MM_H / (gp.maxWorldRow * gp.tileSize);
        for (entity.enemy.Enemy e : gp.enemies) {
            if (e != null && e.alive && !e.readyToRemove) {
                int ex = mmX + (int)(e.worldX * sx);
                int ey = mmY + (int)(e.worldY * sy);
                g2.setColor(e.isBoss ? UIConstants.COL_GOLD : new Color(200, 50, 50, 180));
                g2.fillOval(ex - 1, ey - 1, e.isBoss ? 5 : 3, e.isBoss ? 5 : 3);
            }
        }
    }

    private void drawPlayerArrow(Graphics2D g2, int mmX, int mmY) {
        float sx = (float) MM_W / (gp.maxWorldCol * gp.tileSize);
        float sy = (float) MM_H / (gp.maxWorldRow * gp.tileSize);
        int pdx = mmX + (int)(gp.player.worldX * sx);
        int pdy = mmY + (int)(gp.player.worldY * sy);

        // White outline dot
        g2.setColor(Color.WHITE);
        g2.fillOval(pdx - 4, pdy - 4, 9, 9);
        // Cyan fill
        g2.setColor(new Color(80, 200, 255));
        g2.fillOval(pdx - 3, pdy - 3, 7, 7);
    }

    private void drawLabel(Graphics2D g2, int mmX, int mmY) {
        g2.setFont(UIFonts.small(8));
        g2.setColor(new Color(190, 175, 120));
        g2.drawString("MAP", mmX + 4, mmY + 10);
    }

    private void drawCompassRose(Graphics2D g2, int cx, int cy) {
        g2.setFont(UIFonts.small(7));
        g2.setColor(new Color(180, 165, 110));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("N", cx - fm.stringWidth("N") / 2, cy - 4);
    }

    private void fillMMRect(Graphics2D g2, int mmX, int mmY,
                              float sx, float sy,
                              int col, int row, int cols, int rows) {
        int x = mmX + (int)(col * gp.tileSize * sx);
        int y = mmY + (int)(row * gp.tileSize * sy);
        int w = (int)(cols * gp.tileSize * sx);
        int h = (int)(rows * gp.tileSize * sy);
        g2.fillRect(x, y, Math.max(1, w), Math.max(1, h));
    }
}
