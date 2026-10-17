package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Carved ancient stone statue on an inscribed pedestal, typical of the Ancient Ruins plazas.
 */
public class OBJ_RuinStatue extends SuperObject {
    private int pixelWidth, pixelHeight;

    public OBJ_RuinStatue(GamePanel gp) {
        super(gp);
        this.name = "Ancient Statue";
        this.pixelWidth = gp.tileSize;
        this.pixelHeight = gp.tileSize * 2;
        this.collision = true;

        collisionBox = new Rectangle(6, pixelHeight - gp.tileSize + 6, pixelWidth - 12, gp.tileSize - 10);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ts = gp.tileSize;
        Color stoneLight  = new Color(220, 215, 205);
        Color stoneMid    = new Color(175, 170, 160);
        Color stoneDark   = new Color(125, 120, 110);
        Color shadow      = new Color(0, 0, 0, 80);
        Color moss        = new Color(85, 130, 75);

        int pedW = 32;
        int pedX = (pixelWidth - pedW) / 2;
        int pedY = pixelHeight - 34;

        // 1. Ground Shadow
        g2.setColor(shadow);
        g2.fillOval(pedX - 4, pixelHeight - 12, pedW + 8, 10);

        // 2. Ornate Inscribed Pedestal
        g2.setColor(stoneDark);
        g2.fillRoundRect(pedX, pedY, pedW, 28, 4, 4);
        g2.setColor(stoneMid);
        g2.fillRect(pedX + 2, pedY + 2, pedW - 4, 24);
        // Pedestal molding
        g2.setColor(stoneLight);
        g2.fillRect(pedX + 4, pedY + 3, pedW - 8, 3);
        g2.setColor(stoneDark);
        g2.fillRect(pedX + 4, pedY + 23, pedW - 8, 3);

        // 3. Warrior / Guardian Statue Figure
        int figX = pixelWidth / 2;
        int headY = 16;

        // Cape / Robe drapery
        g2.setColor(stoneDark);
        g2.fillRoundRect(figX - 11, headY + 18, 22, pedY - (headY + 18), 6, 6);
        g2.setColor(stoneMid);
        g2.fillRoundRect(figX - 9, headY + 19, 18, pedY - (headY + 20), 4, 4);

        // Torso / Cuirass
        g2.setColor(stoneLight);
        g2.fillRoundRect(figX - 8, headY + 18, 16, 24, 4, 4);

        // Shield in left hand
        g2.setColor(stoneDark);
        g2.fillOval(figX - 16, headY + 22, 12, 22);
        g2.setColor(stoneLight);
        g2.fillOval(figX - 15, headY + 23, 10, 20);

        // Sword held point-down in right hand
        g2.setColor(stoneLight);
        g2.fillRect(figX + 9, headY + 22, 3, 26);
        // Crossguard
        g2.fillRect(figX + 7, headY + 25, 7, 3);

        // Head & Helmet
        g2.setColor(stoneLight);
        g2.fillOval(figX - 7, headY, 14, 16);
        // Helmet crest
        g2.setColor(stoneMid);
        g2.fillRoundRect(figX - 3, headY - 4, 6, 8, 2, 2);

        // 4. Weathering Moss
        g2.setColor(moss);
        g2.fillOval(pedX + 2, pedY + 12, 6, 4);
        g2.fillOval(figX - 12, headY + 32, 5, 5);

        g2.dispose();
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("Statue of an Ancient Kingdom Guardian.");
    }
}
