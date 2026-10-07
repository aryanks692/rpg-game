package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_PoliceStation extends SuperObject {
    private int pixelWidth, pixelHeight;
    public boolean doorLocked = false;

    public OBJ_PoliceStation(GamePanel gp) {
        super(gp);
        name = "Police Station";
        // 3 tiles wide, 3 tiles high
        pixelWidth = gp.tileSize * 3;
        pixelHeight = gp.tileSize * 3;
        
        collisionBox = new Rectangle((pixelWidth / 2) - gp.tileSize/2, pixelHeight - gp.tileSize, gp.tileSize, gp.tileSize + 8);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color woodDark = new Color(60, 40, 25);
        Color woodBody = new Color(110, 75, 45);
        Color woodLight = new Color(150, 105, 65);
        Color metalBar = new Color(40, 45, 50);
        Color starGold = new Color(255, 215, 0);
        Color starShadow = new Color(180, 140, 0);

        int bX = 6;
        int bY = 10;
        int bW = pixelWidth - 12;
        int bH = pixelHeight - 14;

        // Ground shadow
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(bX - 4, pixelHeight - 12, bW + 8, 10, 8, 8);

        // --- 1. WALLS (Sturdy Weathered Dark Timber) ---
        g2.setColor(woodBody);
        g2.fillRect(bX, bY, bW, bH);

        // Horizontal plank lines
        g2.setColor(woodDark);
        for (int y = bY; y < bY + bH; y += 7) {
            g2.drawLine(bX, y, bX + bW, y);
        }

        // Timber Corner Pillars
        g2.setColor(woodDark);
        g2.fillRect(bX, bY, 10, bH);
        g2.fillRect(bX + bW - 10, bY, 10, bH);
        g2.setColor(woodLight);
        g2.fillRect(bX + 2, bY, 3, bH);
        g2.fillRect(bX + bW - 8, bY, 3, bH);

        // --- 2. ROOF FACADE & SHERIFF STAR BADGE ---
        int midX = bX + bW / 2;
        g2.setColor(woodDark);
        g2.fillRect(bX - 2, bY, bW + 4, 24);

        // "POLICE & MARSHAL" Sign
        g2.setColor(new Color(25, 20, 15));
        g2.fillRoundRect(midX - 52, bY + 2, 104, 18, 4, 4);
        g2.setColor(starGold);
        g2.drawRoundRect(midX - 52, bY + 2, 104, 18, 4, 4);
        g2.setFont(new Font("Georgia", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("POLICE STATION", midX - fm.stringWidth("POLICE STATION") / 2, bY + 15);

        // Large 6-pointed Star Badge in Center Pediment
        int starX = midX;
        int starY = bY + 36;
        g2.setColor(starShadow);
        g2.fillOval(starX - 13, starY - 13, 26, 26);
        g2.setColor(starGold);
        g2.fillOval(starX - 11, starY - 11, 22, 22);
        // Star lines
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Serif", Font.BOLD, 14));
        g2.drawString("★", starX - 6, starY + 6);

        // --- 3. BARRED JAIL CELL WINDOW (Left Side) ---
        int winX = bX + 16;
        int winY = bY + 54;
        int winW = 30;
        int winH = 26;

        // Dark cell interior
        g2.setColor(new Color(15, 15, 20));
        g2.fillRect(winX, winY, winW, winH);
        // Stone window frame
        g2.setColor(new Color(100, 95, 90));
        g2.drawRect(winX, winY, winW, winH);
        g2.drawRect(winX - 1, winY - 1, winW + 2, winH + 2);
        // Vertical Iron Bars
        g2.setColor(metalBar);
        g2.setStroke(new BasicStroke(2));
        for (int x = winX + 6; x < winX + winW; x += 6) {
            g2.drawLine(x, winY, x, winY + winH);
        }

        // --- 4. WANTED POSTERS (Right Side) ---
        int postX = bX + bW - 36;
        int postY = bY + 52;
        // Poster 1
        g2.setColor(new Color(235, 220, 180));
        g2.fillRect(postX, postY, 18, 22);
        g2.setColor(new Color(60, 40, 20));
        g2.setFont(new Font("Arial", Font.BOLD, 5));
        g2.drawString("WANTED", postX + 1, postY + 6);
        g2.fillOval(postX + 4, postY + 8, 10, 10); // Sketch face
        // Poster 2 (tilted slightly)
        g2.setColor(new Color(225, 210, 170));
        g2.fillRect(postX + 12, postY + 12, 16, 18);
        g2.setColor(new Color(60, 40, 20));
        g2.drawString("REWARD", postX + 13, postY + 17);

        // --- 5. HEAVY PRISON / STATION DOOR ---
        int doorW = 28;
        int doorH = 44;
        int doorX = midX - doorW / 2;
        int doorY = pixelHeight - doorH - 4;

        g2.setColor(new Color(45, 30, 20));
        g2.fillRect(doorX, doorY, doorW, doorH);
        g2.setColor(new Color(85, 55, 35));
        g2.fillRect(doorX + 2, doorY + 2, doorW - 4, doorH - 2);

        // Iron studs on door
        g2.setColor(Color.DARK_GRAY);
        for (int y = doorY + 8; y < doorY + doorH; y += 12) {
            g2.fillRect(doorX + 5, y, 3, 3);
            g2.fillRect(doorX + doorW - 8, y, 3, 3);
        }
        // Iron door handle
        g2.setColor(Color.BLACK);
        g2.fillRect(doorX + 5, doorY + doorH / 2, 4, 8);

        g2.dispose();
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
                g2.fillRoundRect(screenX + pixelWidth/2 - 44, screenY + pixelHeight - 20, 88, 18, 6, 6);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.setColor(Color.YELLOW);
                g2.drawString("[E] Police Station", screenX + pixelWidth/2 - 40, screenY + pixelHeight - 7);
            }
        }
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("Frontier Police & Marshal Office");
    }
}
