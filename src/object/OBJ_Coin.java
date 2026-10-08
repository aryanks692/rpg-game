package object;

import core.GamePanel;
import entity.Player;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_Coin extends SuperObject {
    public int value;
    private int animTimer = 0;
    private int currentFrame = 0;

    public OBJ_Coin(GamePanel gp, int worldX, int worldY, int value) {
        super(gp);
        this.worldX = worldX;
        this.worldY = worldY;
        this.value = value;
        this.name = "Gold Coin";

        collisionBox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        createCoinSprite();
    }

    private void createCoinSprite() {
        int ts = gp.tileSize;
        image = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Outer glow
        g2.setColor(new Color(255, 215, 0, 70));
        g2.fillOval(4, 4, ts - 8, ts - 8);

        // Gold coin body
        g2.setColor(new Color(255, 215, 0));
        g2.fillOval(8, 8, ts - 16, ts - 16);

        // Outer rim
        g2.setColor(new Color(180, 130, 20));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(8, 8, ts - 16, ts - 16);

        // "G" embossed symbol
        g2.setColor(new Color(190, 130, 20));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("G", ts / 2 - 6, ts / 2 + 6);

        // Inner sparkle highlight
        g2.setColor(Color.WHITE);
        g2.fillOval(14, 12, 5, 5);

        g2.dispose();
    }

    @Override
    public void draw(Graphics2D g2) {
        if (pickedUp) return;
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;

        // Frustum cull
        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth) return;
        if (screenY + gp.tileSize < 0 || screenY > gp.screenHeight) return;

        // Animate floating bob
        animTimer++;
        int bob = (int)(Math.sin(animTimer * 0.1) * 4);

        if (image != null) {
            g2.drawImage(image, screenX, screenY + bob, gp.tileSize, gp.tileSize, null);
        }

        // Floating label
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        String label = "+" + value + "G";
        int textX = screenX + gp.tileSize / 2 - fm.stringWidth(label) / 2;
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(textX - 2, screenY - 12 + bob, fm.stringWidth(label) + 4, 12);
        g2.setColor(new Color(255, 215, 60));
        g2.drawString(label, textX, screenY - 2 + bob);
    }

    @Override
    public void onPickup(Player player) {
        player.gold += value;
        pickedUp = true;
        gp.ui.showNotification("+" + value + " Gold!");
    }
}