package entity.enemy;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Slime extends Enemy {
    private float bobOffset = 0;
    private Color slimeColor;

    public Slime(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, gp.tileSize * 5);
        type = "Slime";
        maxLife = 30;
        life = maxLife;
        speed = 1;
        attackDamage = 6;
        attackRange = gp.tileSize * 2;
        attackCooldownMax = 80;
        xpReward = 10;
        goldReward = 5;
        slimeColor = new Color(
            60 + (int)(Math.random() * 80),
            140 + (int)(Math.random() * 80),
            60 + (int)(Math.random() * 60)
        );
        buildSprites();
    }

    private void buildSprites() {
        for (int i = 0; i < 4; i++) {
            walkDown[i]  = drawSlime(i);
            walkUp[i]    = drawSlime(i);
            walkLeft[i]  = drawSlime(i);
            walkRight[i] = drawSlime(i);
        }
        image = walkDown[0];
    }

    private BufferedImage drawSlime(int frame) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Bob up/down
        int bob = (frame % 2 == 0) ? 0 : 3;

        // Shadow
        g.setColor(new Color(0, 0, 0, 60));
        g.fillOval(8, ts - 12 + bob, ts - 16, 8);

        // Body
        g.setColor(slimeColor);
        g.fillOval(6, 16 + bob, ts - 12, ts - 22 - bob);

        // Shine
        g.setColor(new Color(255, 255, 255, 100));
        g.fillOval(12, 18 + bob, 10, 7);

        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(14, 22 + bob, 8, 8);
        g.fillOval(26, 22 + bob, 8, 8);
        g.setColor(new Color(30, 30, 80));
        g.fillOval(16, 24 + bob, 5, 5);
        g.fillOval(28, 24 + bob, 5, 5);

        // Drips
        g.setColor(new Color(slimeColor.getRed(), slimeColor.getGreen(), slimeColor.getBlue(), 180));
        g.fillOval(8, ts - 16 + bob, 4, 6);
        g.fillOval(ts - 14, ts - 18 + bob, 3, 5);

        g.dispose();
        return img;
    }
}
