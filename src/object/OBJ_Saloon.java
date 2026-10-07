package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_Saloon extends SuperObject {
    private int pixelWidth, pixelHeight;
    public boolean doorLocked = false;

    public OBJ_Saloon(GamePanel gp) {
        super(gp);
        name = "Saloon";
        // 4 tiles wide, 3 tiles high
        pixelWidth = gp.tileSize * 4;
        pixelHeight = gp.tileSize * 3;
        
        collisionBox = new Rectangle(8, 12, pixelWidth - 16, pixelHeight - 16);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Western Wood Palette
        Color woodDark = new Color(75, 45, 25);
        Color woodMain = new Color(130, 85, 48);
        Color woodLight = new Color(175, 120, 75);
        Color trimColor = new Color(195, 140, 50);
        Color goldText = new Color(255, 215, 60);
        Color windowGlow = new Color(255, 200, 80, 200);

        int bX = 8;
        int bY = 12;
        int bW = pixelWidth - 16;
        int bH = pixelHeight - 16;

        // Ground shadow
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(bX - 4, pixelHeight - 14, bW + 8, 12, 8, 8);

        // --- 1. MAIN BUILDING BODY (Horizontal Weathered Planks) ---
        g2.setColor(woodMain);
        g2.fillRect(bX, bY, bW, bH);

        // Plank lines
        g2.setColor(woodDark);
        g2.setStroke(new BasicStroke(1));
        for (int y = bY; y < bY + bH; y += 8) {
            g2.drawLine(bX, y, bX + bW, y);
            g2.setColor(new Color(woodLight.getRed(), woodLight.getGreen(), woodLight.getBlue(), 60));
            g2.drawLine(bX, y + 1, bX + bW, y + 1);
            g2.setColor(woodDark);
        }

        // --- 2. WESTERN FALSE-FRONT PEDIMENT (Top Facade) ---
        int pedY = bY;
        int pedH = 34;
        g2.setColor(woodDark);
        g2.fillRect(bX - 4, pedY, bW + 8, 6);
        // Stepped decorative pediment top
        int midX = bX + bW / 2;
        g2.setColor(woodMain);
        g2.fillRect(midX - 45, pedY - 10, 90, 10);
        g2.setColor(trimColor);
        g2.fillRect(midX - 45, pedY - 12, 90, 3);
        g2.drawRect(midX - 45, pedY - 10, 90, 10);

        // "SALOON" Signboard
        g2.setColor(new Color(45, 25, 15));
        g2.fillRoundRect(midX - 40, pedY + 4, 80, 20, 6, 6);
        g2.setColor(trimColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(midX - 40, pedY + 4, 80, 20, 6, 6);

        g2.setFont(new Font("Georgia", Font.BOLD, 12));
        g2.setColor(goldText);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("SALOON", midX - fm.stringWidth("SALOON") / 2, pedY + 18);

        // --- 3. SECOND FLOOR BALCONY & WINDOWS ---
        int balcY = bY + 38;
        // Upper Windows (2 glowing windows)
        g2.setColor(new Color(40, 20, 10));
        g2.fillRect(bX + 24, balcY - 2, 22, 18);
        g2.fillRect(bX + bW - 46, balcY - 2, 22, 18);
        g2.setColor(windowGlow);
        g2.fillRect(bX + 26, balcY, 18, 14);
        g2.fillRect(bX + bW - 44, balcY, 18, 14);
        // Window grilles
        g2.setColor(woodDark);
        g2.drawLine(bX + 35, balcY, bX + 35, balcY + 14);
        g2.drawLine(bX + bW - 35, balcY, bX + bW - 35, balcY + 14);

        // Balcony Overhang & Railing
        g2.setColor(woodDark);
        g2.fillRect(bX - 2, balcY + 18, bW + 4, 5);
        g2.setColor(woodLight);
        g2.fillRect(bX - 2, balcY + 18, bW + 4, 2);
        // Balustrade posts
        g2.setColor(woodDark);
        for (int x = bX + 2; x < bX + bW; x += 10) {
            g2.fillRect(x, balcY + 8, 3, 10);
        }
        g2.fillRect(bX - 2, balcY + 8, bW + 4, 3);

        // --- 4. GROUND FLOOR PORCH PILLARS ---
        int porchY = balcY + 23;
        int groundY = bY + bH;
        g2.setColor(woodDark);
        g2.fillRect(bX + 4, porchY, 6, groundY - porchY);
        g2.fillRect(bX + bW - 10, porchY, 6, groundY - porchY);
        g2.fillRect(bX + bW / 2 - 28, porchY, 5, groundY - porchY);
        g2.fillRect(bX + bW / 2 + 23, porchY, 5, groundY - porchY);

        // --- 5. GROUND FLOOR ENTRANCE & BATWING DOORS ---
        int doorW = 34;
        int doorH = 40;
        int doorX = midX - doorW / 2;
        int doorY = groundY - doorH;

        // Dark doorway interior
        g2.setColor(new Color(25, 15, 10));
        g2.fillRect(doorX, doorY, doorW, doorH);

        // Glowing interior background
        g2.setColor(new Color(255, 180, 60, 160));
        g2.fillRect(doorX + 2, doorY + 4, doorW - 4, doorH - 6);

        // Swinging batwing wooden louvered half-doors
        int bwH = 22;
        int bwY = doorY + 10;
        g2.setColor(new Color(150, 95, 50));
        g2.fillRoundRect(doorX + 2, bwY, 13, bwH, 3, 3);
        g2.fillRoundRect(doorX + doorW - 15, bwY, 13, bwH, 3, 3);
        g2.setColor(woodDark);
        g2.drawRoundRect(doorX + 2, bwY, 13, bwH, 3, 3);
        g2.drawRoundRect(doorX + doorW - 15, bwY, 13, bwH, 3, 3);

        // Porch Lanterns
        drawLantern(g2, bX + 16, porchY + 6);
        drawLantern(g2, bX + bW - 22, porchY + 6);

        g2.dispose();
    }

    private void drawLantern(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(40, 40, 40));
        g2.fillRect(x + 2, y, 4, 3);
        g2.setColor(new Color(255, 220, 100));
        g2.fillOval(x, y + 3, 8, 10);
        g2.setColor(new Color(255, 255, 200, 180));
        g2.fillOval(x + 2, y + 5, 4, 5);
        g2.setColor(new Color(60, 60, 60));
        g2.drawRect(x, y + 3, 8, 10);
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;

        if (screenX + pixelWidth > 0 && screenX < gp.screenWidth &&
            screenY + pixelHeight > 0 && screenY < gp.screenHeight) {
            
            g2.drawImage(image, screenX, screenY, null);

            // Interaction prompt when player is in front
            int px = gp.player.worldX + gp.player.width/2;
            int py = gp.player.worldY + gp.player.height/2;
            int doorCenterX = worldX + pixelWidth/2;
            int doorCenterY = worldY + pixelHeight - gp.tileSize/2;
            
            if (Math.abs(px - doorCenterX) < gp.tileSize && Math.abs(py - doorCenterY) < gp.tileSize) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(screenX + pixelWidth/2 - 40, screenY + pixelHeight - 20, 80, 18, 6, 6);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.setColor(Color.YELLOW);
                g2.drawString("[E] Enter Saloon", screenX + pixelWidth/2 - 36, screenY + pixelHeight - 7);
            }
        }
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("Welcome to the Silver Spur Saloon!");
    }
}
