package entity.enemy;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Skeleton extends Enemy {
    private int boneRattle = 0;

    public Skeleton(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, gp.tileSize * 7);
        type = "Skeleton";
        maxLife = 55;
        life = maxLife;
        speed = 2;
        attackDamage = 12;
        attackRange = gp.tileSize * 2;
        attackCooldownMax = 70;
        xpReward = 25;
        goldReward = 12;
        buildSprites();
    }

    private void buildSprites() {
        for (int i = 0; i < 4; i++) {
            walkDown[i]  = drawSkeleton("down",  i);
            walkUp[i]    = drawSkeleton("up",    i);
            walkLeft[i]  = drawSkeleton("left",  i);
            walkRight[i] = drawSkeleton("right", i);
        }
        image = walkDown[0];
    }

    private BufferedImage drawSkeleton(String dir, int frame) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bone = new Color(220, 215, 200);
        Color shadow = new Color(160, 155, 140);
        int legBob = (frame % 2 == 0) ? 2 : -2;

        // Legs
        g.setColor(bone);
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(16, ts - 10, 16, ts - 20 + legBob);
        g.drawLine(32, ts - 10, 32, ts - 20 - legBob);
        // Feet
        g.fillOval(12, ts - 12 + legBob, 8, 5);
        g.fillOval(28, ts - 12 - legBob, 8, 5);

        // Pelvis
        g.setColor(bone);
        g.fillRoundRect(10, ts - 22, 28, 10, 5, 5);

        // Spine
        g.setColor(shadow);
        g.fillRect(ts/2 - 3, 22, 6, 16);
        // Ribs
        g.setColor(bone);
        for (int r = 0; r < 3; r++) {
            int ry = 22 + r * 5;
            g.drawLine(ts/2, ry, ts/2 - 10, ry + 3);
            g.drawLine(ts/2, ry, ts/2 + 10, ry + 3);
        }

        // Arms
        g.setColor(bone);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int armSwing = (frame % 2 == 0) ? 4 : -4;
        g.drawLine(10, 22, 4, 28 + armSwing);
        g.drawLine(38, 22, 44, 28 - armSwing);

        // Skull
        g.setColor(bone);
        g.fillOval(13, 5, 22, 20);
        // Jaw
        g.fillRoundRect(15, 20, 18, 8, 4, 4);
        // Eye sockets
        g.setColor(new Color(20, 20, 40));
        g.fillOval(16, 10, 7, 7);
        g.fillOval(25, 10, 7, 7);
        // Glowing eyes
        g.setColor(new Color(80, 220, 80, 200));
        g.fillOval(18, 12, 4, 4);
        g.fillOval(27, 12, 4, 4);

        g.setStroke(new BasicStroke(1));
        g.dispose();
        return img;
    }
}
