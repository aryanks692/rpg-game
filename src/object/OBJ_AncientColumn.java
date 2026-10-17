package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Classical ancient stone column inspired by Greco-Roman ruins.
 * Can stand on land or emerge from water.
 */
public class OBJ_AncientColumn extends SuperObject {
    private int pixelWidth, pixelHeight;
    private boolean inWater;

    public OBJ_AncientColumn(GamePanel gp, boolean inWater) {
        super(gp);
        this.name = inWater ? "Sunken Column" : "Ancient Column";
        this.inWater = inWater;
        this.pixelWidth = gp.tileSize;
        this.pixelHeight = gp.tileSize * 2;
        this.collision = true;

        // Collision at base of column
        collisionBox = new Rectangle(8, pixelHeight - gp.tileSize + 8, pixelWidth - 16, gp.tileSize - 12);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ts = gp.tileSize;
        Color stoneLight = new Color(210, 205, 195);
        Color stoneMid   = new Color(175, 170, 160);
        Color stoneDark  = new Color(125, 120, 110);
        Color stoneShadow = new Color(85, 80, 75);
        Color moss       = new Color(85, 130, 75);

        int colW = 20;
        int colX = (pixelWidth - colW) / 2;
        int topY = 12;
        int botY = pixelHeight - (inWater ? 14 : 6);

        // 1. Base Shadow or Water ripple
        if (inWater) {
            // Water ripples
            g2.setColor(new Color(110, 195, 230, 160));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(colX - 8, botY - 4, colW + 16, 12);
            g2.setColor(new Color(255, 255, 255, 140));
            g2.drawArc(colX - 6, botY - 2, colW + 12, 8, 180, 180);
        } else {
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillOval(colX - 6, botY - 2, colW + 12, 10);
        }

        // 2. Column Base Pedestal
        g2.setColor(stoneDark);
        g2.fillRoundRect(colX - 4, botY - 10, colW + 8, 10, 3, 3);
        g2.setColor(stoneMid);
        g2.fillRect(colX - 2, botY - 8, colW + 4, 6);

        // 3. Fluted Column Shaft
        g2.setColor(stoneMid);
        g2.fillRect(colX, topY + 10, colW, botY - topY - 20);

        // Vertical fluting lines & highlights
        for (int x = colX + 3; x < colX + colW - 2; x += 4) {
            g2.setColor(stoneShadow);
            g2.drawLine(x, topY + 11, x, botY - 11);
            g2.setColor(stoneLight);
            g2.drawLine(x + 1, topY + 11, x + 1, botY - 11);
        }

        // 4. Ornate Capital (Head of column)
        g2.setColor(stoneDark);
        g2.fillRoundRect(colX - 5, topY, colW + 10, 12, 4, 4);
        g2.setColor(stoneLight);
        g2.fillRect(colX - 3, topY + 2, colW + 6, 4);

        // Capital Volutes (Scroll details)
        g2.setColor(stoneMid);
        g2.fillOval(colX - 5, topY + 2, 6, 6);
        g2.fillOval(colX + colW - 1, topY + 2, 6, 6);

        // 5. Weathering Cracks & Moss Overgrowth
        g2.setColor(moss);
        g2.fillOval(colX - 1, botY - 18, 7, 5);
        g2.fillOval(colX + 8, topY + 16, 6, 4);
        g2.fillRect(colX + 2, topY + 8, 4, 2);

        // Crack line
        g2.setColor(stoneShadow);
        g2.drawLine(colX + 5, topY + 24, colX + 10, topY + 32);
        g2.drawLine(colX + 10, topY + 32, colX + 8, topY + 40);

        g2.dispose();
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("Ancient column weathered by centuries of time.");
    }
}
