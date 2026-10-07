package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_GeneralStore extends SuperObject {
    private int pixelWidth, pixelHeight;
    public boolean doorLocked = false;

    public OBJ_GeneralStore(GamePanel gp) {
        super(gp);
        name = "General Store";
        pixelWidth = gp.tileSize * 3;
        pixelHeight = gp.tileSize * 3;
        
        collisionBox = new Rectangle((pixelWidth / 2) - gp.tileSize/2, pixelHeight - gp.tileSize, gp.tileSize, gp.tileSize + 8);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color woodBody = new Color(145, 100, 60);
        Color woodDark = new Color(85, 55, 30);
        Color awningRed = new Color(185, 45, 45);
        Color awningWhite = new Color(240, 235, 220);
        Color goldTrim = new Color(245, 205, 75);

        int bX = 6;
        int bY = 10;
        int bW = pixelWidth - 12;
        int bH = pixelHeight - 14;

        // Ground shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(bX - 4, pixelHeight - 12, bW + 8, 10, 8, 8);

        // --- 1. WALLS ---
        g2.setColor(woodBody);
        g2.fillRect(bX, bY, bW, bH);
        g2.setColor(woodDark);
        for (int y = bY; y < bY + bH; y += 7) g2.drawLine(bX, y, bX + bW, y);

        // --- 2. SIGNBOARD ---
        int midX = bX + bW / 2;
        g2.setColor(woodDark);
        g2.fillRect(bX, bY, bW, 22);
        g2.setColor(new Color(40, 25, 15));
        g2.fillRoundRect(midX - 48, bY + 2, 96, 18, 4, 4);
        g2.setColor(goldTrim);
        g2.drawRoundRect(midX - 48, bY + 2, 96, 18, 4, 4);
        g2.setFont(new Font("Georgia", Font.BOLD, 9));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("GENERAL STORE", midX - fm.stringWidth("GENERAL STORE") / 2, bY + 15);

        // --- 3. STRIPED CANVAS AWNING ---
        int awnY = bY + 24;
        int awnH = 20;
        int stripeW = 12;
        for (int x = bX - 2, i = 0; x < bX + bW + 2; x += stripeW, i++) {
            g2.setColor((i % 2 == 0) ? awningRed : awningWhite);
            g2.fillRect(x, awnY, stripeW, awnH);
            // Scalloped bottom edge
            g2.fillOval(x, awnY + awnH - 4, stripeW, 8);
        }

        // --- 4. DISPLAY WINDOW & GOODS ---
        int winX = bX + 10;
        int winY = awnY + awnH + 6;
        int winW = 34;
        int winH = 30;
        g2.setColor(new Color(30, 20, 15));
        g2.fillRect(winX, winY, winW, winH);
        g2.setColor(new Color(255, 230, 160, 200));
        g2.fillRect(winX + 2, winY + 2, winW - 4, winH - 4);
        g2.setColor(woodDark);
        g2.drawRect(winX, winY, winW, winH);
        // Potions / Goods on shelf inside window
        g2.setColor(new Color(220, 40, 40));
        g2.fillOval(winX + 6, winY + winH - 12, 8, 10);
        g2.setColor(new Color(40, 120, 240));
        g2.fillOval(winX + 18, winY + winH - 12, 8, 10);

        // --- 5. STORE DOOR ---
        int doorW = 28;
        int doorH = 44;
        int doorX = bX + bW - doorW - 14;
        int doorY = pixelHeight - doorH - 4;

        g2.setColor(new Color(50, 30, 20));
        g2.fillRect(doorX, doorY, doorW, doorH);
        g2.setColor(new Color(100, 65, 40));
        g2.fillRect(doorX + 2, doorY + 2, doorW - 4, doorH - 2);
        // Door glass
        g2.setColor(new Color(255, 220, 140, 180));
        g2.fillRect(doorX + 5, doorY + 6, doorW - 10, 16);
        g2.setColor(woodDark);
        g2.drawRect(doorX + 5, doorY + 6, doorW - 10, 16);

        g2.dispose();
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;

        if (screenX + pixelWidth > 0 && screenX < gp.screenWidth &&
            screenY + pixelHeight > 0 && screenY < gp.screenHeight) {
            
            g2.drawImage(image, screenX, screenY, null);

            int px = gp.player.worldX + gp.player.width/2;
            int py = gp.player.worldY + gp.player.height/2;
            int doorCenterX = worldX + pixelWidth/2 + 20;
            int doorCenterY = worldY + pixelHeight - gp.tileSize/2;
            
            if (Math.abs(px - doorCenterX) < gp.tileSize && Math.abs(py - doorCenterY) < gp.tileSize) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(screenX + pixelWidth/2 - 40, screenY + pixelHeight - 20, 80, 18, 6, 6);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.setColor(Color.YELLOW);
                g2.drawString("[E] General Store", screenX + pixelWidth/2 - 36, screenY + pixelHeight - 7);
            }
        }
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("Dusty Gulch General Store");
    }
}
