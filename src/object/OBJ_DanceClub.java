package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_DanceClub extends SuperObject {
    private int pixelWidth, pixelHeight;
    public boolean doorLocked = false;

    public OBJ_DanceClub(GamePanel gp) {
        super(gp);
        name = "Dance Club";
        // 4 tiles wide, 3 tiles high
        pixelWidth = gp.tileSize * 4;
        pixelHeight = gp.tileSize * 3;
        
        collisionBox = new Rectangle((pixelWidth / 2) - gp.tileSize/2, pixelHeight - gp.tileSize, gp.tileSize, gp.tileSize + 8);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color velvetRed = new Color(160, 30, 45);
        Color velvetDark = new Color(100, 15, 25);
        Color woodTrim = new Color(70, 35, 20);
        Color goldTrim = new Color(255, 215, 60);
        Color goldDark = new Color(180, 140, 20);
        Color glowPink = new Color(255, 120, 160, 220);
        Color glowWarm = new Color(255, 220, 140, 230);

        int bX = 8;
        int bY = 10;
        int bW = pixelWidth - 16;
        int bH = pixelHeight - 14;

        // Ground shadow
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(bX - 4, pixelHeight - 12, bW + 8, 10, 8, 8);

        // --- 1. MAIN CABARET FAÇADE (Rich Crimson Cladding) ---
        g2.setColor(velvetRed);
        g2.fillRect(bX, bY, bW, bH);

        // Subtle wallpaper stripe texture
        g2.setColor(velvetDark);
        for (int x = bX; x < bX + bW; x += 12) {
            g2.fillRect(x, bY, 4, bH);
        }

        // --- 2. ORNATE VICTORIAN PEDIMENT & MARQUEE ---
        int midX = bX + bW / 2;
        g2.setColor(woodTrim);
        g2.fillRect(bX - 2, bY, bW + 4, 30);

        // Top decorative scalloped crown
        g2.setColor(goldTrim);
        g2.fillOval(midX - 35, bY - 10, 70, 20);
        g2.setColor(velvetRed);
        g2.fillOval(midX - 31, bY - 8, 62, 16);
        g2.setColor(goldTrim);
        g2.setFont(new Font("Serif", Font.BOLD, 12));
        g2.drawString("♫ ♬", midX - 10, bY + 4);

        // "DANCE CLUB" Illuminated Marquee
        g2.setColor(new Color(30, 10, 20));
        g2.fillRoundRect(midX - 58, bY + 8, 116, 20, 8, 8);
        g2.setColor(goldTrim);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(midX - 58, bY + 8, 116, 20, 8, 8);

        // Marquee light bulbs
        g2.setColor(Color.WHITE);
        for (int lx = midX - 52; lx <= midX + 52; lx += 10) {
            g2.fillOval(lx, bY + 5, 4, 4);
            g2.fillOval(lx, bY + 28, 4, 4);
        }

        g2.setFont(new Font("Georgia", Font.BOLD, 11));
        g2.setColor(new Color(255, 230, 120));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("DANCE CLUB", midX - fm.stringWidth("DANCE CLUB") / 2, bY + 23);

        // --- 3. ROSE-TINTED STAINED GLASS WINDOWS (Left & Right) ---
        int winW = 28;
        int winH = 34;
        // Left Stained Glass Window
        drawCabaretWindow(g2, bX + 16, bY + 44, winW, winH, glowPink, goldDark);
        // Right Stained Glass Window
        drawCabaretWindow(g2, bX + bW - 44, bY + 44, winW, winH, glowPink, goldDark);

        // --- 4. GRAND ARCHED ENTRANCE & VELVET CURTAINS ---
        int doorW = 44;
        int doorH = 52;
        int doorX = midX - doorW / 2;
        int doorY = pixelHeight - doorH - 4;

        // Arch Frame
        g2.setColor(goldDark);
        g2.fillArc(doorX - 4, doorY - 14, doorW + 8, 30, 0, 180);
        g2.setColor(woodTrim);
        g2.fillRect(doorX - 4, doorY, doorW + 8, doorH);

        // Dark Interior
        g2.setColor(new Color(25, 5, 15));
        g2.fillRect(doorX, doorY, doorW, doorH);
        g2.fillArc(doorX, doorY - 10, doorW, 20, 0, 180);

        // Warm interior glow & dance lights
        g2.setColor(glowWarm);
        g2.fillRect(doorX + 4, doorY + 6, doorW - 8, doorH - 8);

        // Swept velvet curtains
        g2.setColor(velvetRed);
        g2.fillRoundRect(doorX + 2, doorY + 4, 12, doorH - 6, 6, 6);
        g2.fillRoundRect(doorX + doorW - 14, doorY + 4, 12, doorH - 6, 6, 6);
        g2.setColor(goldTrim);
        g2.fillRect(doorX + 10, doorY + 24, 4, 4); // Gold curtain tie
        g2.fillRect(doorX + doorW - 14, doorY + 24, 4, 4);

        // Crimson Carpet Entrance Runner
        g2.setColor(new Color(190, 20, 35));
        g2.fillRect(doorX + 6, pixelHeight - 8, doorW - 12, 6);
        g2.setColor(goldTrim);
        g2.fillRect(doorX + 6, pixelHeight - 8, 2, 6);
        g2.fillRect(doorX + doorW - 8, pixelHeight - 8, 2, 6);

        // Brass Entrance Torches
        drawBrassLamp(g2, doorX - 10, doorY + 12);
        drawBrassLamp(g2, doorX + doorW + 6, doorY + 12);

        g2.dispose();
    }

    private void drawCabaretWindow(Graphics2D g2, int x, int y, int w, int h, Color glow, Color frameCol) {
        g2.setColor(new Color(40, 10, 20));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setColor(glow);
        g2.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 6, 6);
        // Stained glass lattice
        g2.setColor(frameCol);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, w, h, 8, 8);
        g2.drawLine(x + w / 2, y + 2, x + w / 2, y + h - 2);
        g2.drawLine(x + 2, y + h / 2, x + w - 2, y + h / 2);
        // Music note silhouette
        g2.setColor(new Color(80, 20, 40));
        g2.setFont(new Font("Serif", Font.BOLD, 12));
        g2.drawString("♫", x + w/2 - 4, y + h/2 + 4);
    }

    private void drawBrassLamp(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(180, 140, 40));
        g2.fillRect(x + 2, y, 4, 12);
        g2.setColor(new Color(255, 180, 80));
        g2.fillOval(x, y - 4, 8, 8);
        g2.setColor(new Color(255, 255, 200));
        g2.fillOval(x + 2, y - 2, 4, 4);
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;

        if (screenX + pixelWidth > 0 && screenX < gp.screenWidth &&
            screenY + pixelHeight > 0 && screenY < gp.screenHeight) {
            
            g2.drawImage(image, screenX, screenY, null);

            // Interaction prompt
            int px = gp.player.worldX + gp.player.width/2;
            int py = gp.player.worldY + gp.player.height/2;
            int doorCenterX = worldX + pixelWidth/2;
            int doorCenterY = worldY + pixelHeight - gp.tileSize/2;
            
            if (Math.abs(px - doorCenterX) < gp.tileSize && Math.abs(py - doorCenterY) < gp.tileSize) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(screenX + pixelWidth/2 - 46, screenY + pixelHeight - 20, 92, 18, 6, 6);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.setColor(Color.YELLOW);
                g2.drawString("[E] Dance Club", screenX + pixelWidth/2 - 40, screenY + pixelHeight - 7);
            }
        }
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("The Wild West Dance Club & Cabaret!");
    }
}
