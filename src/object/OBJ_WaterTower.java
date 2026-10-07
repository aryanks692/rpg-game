package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_WaterTower extends SuperObject {
    private int pixelWidth, pixelHeight;

    public OBJ_WaterTower(GamePanel gp) {
        super(gp);
        name = "Water Tower";
        pixelWidth = gp.tileSize * 2;
        pixelHeight = gp.tileSize * 3;
        
        collision = true;
        collisionBox = new Rectangle(8, pixelHeight - gp.tileSize, pixelWidth - 16, gp.tileSize);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color woodBody = new Color(135, 90, 50);
        Color woodDark = new Color(75, 45, 25);
        Color woodLight = new Color(175, 120, 70);
        Color ironHoop = new Color(50, 55, 60);

        int tankW = pixelWidth - 12;
        int tankH = (int)(gp.tileSize * 1.3);
        int tankX = 6;
        int tankY = 8;

        // Ground shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(4, pixelHeight - 14, pixelWidth - 8, 12);

        // --- 1. TIMBER STILTS & X-BRACES (Legs) ---
        int legBottom = pixelHeight - 6;
        int legTop = tankY + tankH - 4;

        g2.setStroke(new BasicStroke(4));
        g2.setColor(woodDark);
        // 4 legs
        g2.drawLine(tankX + 6, legTop, tankX + 2, legBottom);
        g2.drawLine(tankX + tankW - 6, legTop, tankX + tankW - 2, legBottom);
        // X-Braces
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(tankX + 4, legTop + 10, tankX + tankW - 4, legBottom - 10);
        g2.drawLine(tankX + tankW - 4, legTop + 10, tankX + 4, legBottom - 10);
        // Horizontal struts
        g2.drawLine(tankX + 4, (legTop + legBottom) / 2, tankX + tankW - 4, (legTop + legBottom) / 2);
        g2.drawLine(tankX + 2, legBottom - 4, tankX + tankW - 2, legBottom - 4);

        // --- 2. WOODEN WATER TANK (Barrel) ---
        g2.setColor(woodBody);
        g2.fillRoundRect(tankX, tankY, tankW, tankH, 12, 12);

        // Vertical tank stave slats
        g2.setColor(woodDark);
        g2.setStroke(new BasicStroke(1));
        for (int x = tankX + 6; x < tankX + tankW; x += 8) {
            g2.drawLine(x, tankY, x, tankY + tankH);
        }

        // Conical Roof Lid
        Polygon roof = new Polygon();
        roof.addPoint(tankX - 4, tankY + 4);
        roof.addPoint(tankX + tankW / 2, tankY - 6);
        roof.addPoint(tankX + tankW + 4, tankY + 4);
        g2.setColor(woodDark);
        g2.fillPolygon(roof);
        g2.setColor(woodLight);
        g2.drawLine(tankX - 4, tankY + 4, tankX + tankW / 2, tankY - 6);

        // Metal Barrel Hoops (3 Iron Bands)
        g2.setColor(ironHoop);
        g2.fillRect(tankX, tankY + 8, tankW, 3);
        g2.fillRect(tankX, tankY + tankH / 2, tankW, 3);
        g2.fillRect(tankX, tankY + tankH - 10, tankW, 3);

        // "DUSTY GULCH" text on tank
        g2.setFont(new Font("Arial", Font.BOLD, 7));
        g2.setColor(new Color(255, 230, 150));
        g2.drawString("DUSTY GULCH", tankX + 8, tankY + tankH / 2 - 2);

        g2.dispose();
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;

        if (screenX + pixelWidth > 0 && screenX < gp.screenWidth &&
            screenY + pixelHeight > 0 && screenY < gp.screenHeight) {
            g2.drawImage(image, screenX, screenY, null);
        }
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("Dusty Gulch Water Tower");
    }
}
