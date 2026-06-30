package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class SuperObject {
    public String name;
    public BufferedImage image;
    public boolean collision = false;
    public int worldX, worldY;
    public Rectangle collisionBox;
    public boolean pickedUp = false;
    public String description = "";

    protected GamePanel gp;

    public SuperObject(GamePanel gp) {
        this.gp = gp;
        collisionBox = new Rectangle(4, 4, gp.tileSize - 8, gp.tileSize - 8);
    }

    public void draw(Graphics2D g2) {
        if (pickedUp) return;
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;
        // Frustum cull
        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth) return;
        if (screenY + gp.tileSize < 0 || screenY > gp.screenHeight) return;

        if (image != null) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
        // Label
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int textX = screenX + gp.tileSize / 2 - fm.stringWidth(name) / 2;
        g2.setColor(new Color(0,0,0,160));
        g2.fillRect(textX - 2, screenY - 14, fm.stringWidth(name) + 4, 12);
        g2.setColor(Color.WHITE);
        g2.drawString(name, textX, screenY - 4);
    }

    public Rectangle getWorldCollisionBox() {
        return new Rectangle(
            worldX + collisionBox.x,
            worldY + collisionBox.y,
            collisionBox.width,
            collisionBox.height
        );
    }

    /** Called when player picks up this object */
    public abstract void onPickup(entity.Player player);

    /** Create a simple colored shape image */
    protected BufferedImage createIcon(Color mainColor, Color accentColor, String symbol) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Glow
        g.setColor(new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 60));
        g.fillOval(2, 2, ts - 4, ts - 4);

        // Main shape
        g.setColor(mainColor);
        g.fillRoundRect(6, 6, ts - 12, ts - 12, 10, 10);

        // Border
        g.setColor(accentColor);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(6, 6, ts - 12, ts - 12, 10, 10);

        // Symbol
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.setColor(accentColor);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(symbol, ts / 2 - fm.stringWidth(symbol) / 2, ts / 2 + fm.getAscent() / 2 - 2);

        g.dispose();
        return img;
    }
}
