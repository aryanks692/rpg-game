package object;

import core.GamePanel;
import entity.Player;
import java.awt.*;

public class OBJ_Chest extends SuperObject {
    public boolean opened = false;
    public String lootItem;
    public int lootGold;

    public OBJ_Chest(GamePanel gp, String lootItem, int gold) {
        super(gp);
        name = "Chest";
        this.lootItem = lootItem;
        this.lootGold = gold;
        collision = true;
        description = "A mysterious chest. Press E to open.";
        image = createChestImage(false);
    }

    private java.awt.image.BufferedImage createChestImage(boolean isOpen) {
        int ts = gp.tileSize;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(ts, ts, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Chest body
        g.setColor(new Color(120, 70, 20));
        g.fillRoundRect(4, 16, ts - 8, ts - 20, 4, 4);
        // Lid
        g.setColor(isOpen ? new Color(100, 55, 15) : new Color(150, 90, 30));
        g.fillRoundRect(4, 8, ts - 8, 14, 4, 4);
        // Metal bands
        g.setColor(new Color(180, 150, 60));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(4, 8, ts - 8, ts - 12, 4, 4);
        g.drawLine(ts / 2, 8, ts / 2, ts - 4);
        // Lock
        g.setColor(new Color(220, 190, 80));
        g.fillOval(ts / 2 - 5, 19, 10, 10);
        g.setColor(new Color(100, 80, 20));
        g.fillOval(ts / 2 - 3, 21, 6, 6);

        if (isOpen) {
            // Sparkles
            g.setColor(Color.YELLOW);
            g.fillOval(ts / 2 - 2, 4, 5, 5);
            g.fillOval(10, 6, 4, 4);
            g.fillOval(ts - 14, 5, 4, 4);
        }
        g.dispose();
        return img;
    }

    @Override
    public void onPickup(Player player) {
        if (opened) return;
        opened = true;
        image = createChestImage(true);
        player.gold += lootGold;
        // spawn loot items handled by AssetSetter
    }
}
