package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_CrystalGate extends SuperObject {
    private int pixelWidth, pixelHeight;
    public boolean open = false;

    public OBJ_CrystalGate(GamePanel gp) {
        super(gp);
        name = "Crystal Gate";
        // 3 tiles wide, 3 tiles high
        pixelWidth = gp.tileSize * 3;
        pixelHeight = gp.tileSize * 3;
        
        // Collision covers the side pillars, leaving the center 1-tile wide cavern opening passable
        // But for now, let's make it a solid wall until we decide if it's a transition or just a decorated entrance.
        // User said "entrance door", usually implies you can go through.
        // I'll make the sides solid and the center open.
        collisionBox = new Rectangle(0, gp.tileSize, pixelWidth, pixelHeight - gp.tileSize);
        
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // HD Crystal Palette
        Color deepCave = new Color(20, 15, 30);
        Color rockBase = new Color(50, 45, 65);
        Color rockHi = new Color(80, 75, 100);
        Color crystalPurple = new Color(180, 100, 255);
        Color crystalBlue = new Color(100, 180, 255);
        Color glowColor = new Color(150, 100, 255, 120);

        int ts = gp.tileSize; // Use standard tile size
        if (ts <= 0) ts = 48;

        // --- CAVERN MOUTH (The "Door" part) ---
        int mouthW = gp.tileSize + 20;
        int mouthH = pixelHeight - 20;
        int mouthX = (pixelWidth / 2) - (mouthW / 2);
        int mouthY = 20;

        // Dark interior of the cave
        g2.setColor(deepCave);
        g2.fillArc(mouthX, mouthY, mouthW, mouthH * 2, 0, 180); // Tall arch
        
        // --- ROCKY PILLARS (Sides) ---
        g2.setColor(rockBase);
        // Left jagged pillar
        int[] lpx = {0, 60, 80, 40, 0};
        int[] lpy = {pixelHeight, pixelHeight, 40, 20, 40};
        g2.fillPolygon(lpx, lpy, 5);
        
        // Right jagged pillar
        int[] rpx = {pixelWidth, pixelWidth - 60, pixelWidth - 80, pixelWidth - 40, pixelWidth};
        int[] rpy = {pixelHeight, pixelHeight, 40, 20, 40};
        g2.fillPolygon(rpx, rpy, 5);

        // Rock highlights/shading
        g2.setColor(rockHi);
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(lpx, lpy, 5);
        g2.drawPolygon(rpx, rpy, 5);

        // --- CRYSTALS (Glowing decorations) ---
        drawCrystal(g2, 30, 80, 25, 45, crystalPurple);
        drawCrystal(g2, 50, 110, 20, 35, crystalBlue);
        drawCrystal(g2, pixelWidth - 40, 90, 22, 40, crystalPurple);
        drawCrystal(g2, pixelWidth - 65, 120, 18, 30, crystalBlue);
        
        // Top Arch Crystals
        drawCrystal(g2, pixelWidth/2 - 10, 10, 20, 30, crystalPurple);
        drawCrystal(g2, pixelWidth/2 + 20, 25, 15, 25, crystalBlue);

        // Ambient Glow
        RadialGradientPaint glow = new RadialGradientPaint(
            new Point(pixelWidth/2, pixelHeight/2),
            pixelWidth/2,
            new float[]{0f, 1f},
            new Color[]{glowColor, new Color(0,0,0,0)}
        );
        g2.setPaint(glow);
        g2.fillRect(0, 0, pixelWidth, pixelHeight);

        g2.dispose();
    }

    private void drawCrystal(Graphics2D g2, int x, int y, int w, int h, Color core) {
        int[] cx = {x, x + w/2, x + w, x + w/2};
        int[] cy = {y + h/2, y, y + h/2, y + h};
        
        // Inner glow
        g2.setColor(new Color(core.getRed(), core.getGreen(), core.getBlue(), 100));
        g2.fillPolygon(cx, cy, 4);
        
        // Shards/facets
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(x + w/2, y, x + w/2, y + h); // Core line
        
        g2.setColor(core);
        g2.drawPolygon(cx, cy, 4);
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;
        if (screenX + pixelWidth < 0 || screenX > gp.screenWidth) return;
        if (screenY + pixelHeight < 0 || screenY > gp.screenHeight) return;

        g2.drawImage(image, screenX, screenY, null);
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("The cave air feels chilling...");
    }
}
