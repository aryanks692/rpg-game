package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_Barrel extends SuperObject {
    public OBJ_Barrel(GamePanel gp) {
        super(gp);
        name = "Barrel";
        collision = true;
        collisionBox = new Rectangle(8, 12, gp.tileSize - 16, gp.tileSize - 16);
        createImage();
    }

    private void createImage() {
        int ts = gp.tileSize;
        image = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Ground shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(6, ts - 10, ts - 12, 8);

        // Barrel Wood Body
        Color wood = new Color(145, 95, 50);
        Color woodDark = new Color(85, 50, 25);
        Color iron = new Color(50, 55, 60);

        int bx = 8;
        int by = 8;
        int bw = ts - 16;
        int bh = ts - 16;

        g2.setColor(wood);
        g2.fillRoundRect(bx, by, bw, bh, 10, 10);

        // Stave vertical curves
        g2.setColor(woodDark);
        g2.drawRoundRect(bx, by, bw, bh, 10, 10);
        g2.drawLine(bx + bw / 3, by + 2, bx + bw / 3 - 1, by + bh - 2);
        g2.drawLine(bx + 2 * bw / 3, by + 2, bx + 2 * bw / 3 + 1, by + bh - 2);

        // Iron Hoops
        g2.setColor(iron);
        g2.fillRect(bx + 1, by + 4, bw - 2, 3);
        g2.fillRect(bx, by + bh / 2 - 1, bw, 3);
        g2.fillRect(bx + 1, by + bh - 7, bw - 2, 3);

        g2.dispose();
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("An old oak barrel.");
    }
}
