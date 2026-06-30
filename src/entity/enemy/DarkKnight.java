package entity.enemy;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DarkKnight extends Enemy {
    private int chargeTimer = 0;
    private boolean charging = false;

    public DarkKnight(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, gp.tileSize * 9);
        type = "DarkKnight";
        maxLife = 120;
        life = maxLife;
        speed = 2;
        attackDamage = 20;
        attackRange = gp.tileSize * 3;
        attackCooldownMax = 100;
        xpReward = 60;
        goldReward = 30;
        invincibleDuration = 20;
        buildSprites();
    }

    private void buildSprites() {
        for (int i = 0; i < 4; i++) {
            walkDown[i] = drawKnight("down", i, false);
            walkUp[i] = drawKnight("up", i, false);
            walkLeft[i] = drawKnight("left", i, false);
            walkRight[i] = drawKnight("right", i, false);
        }
        for (int i = 0; i < 2; i++) {
            attackDown[i] = drawKnight("down", i, true);
            attackUp[i] = drawKnight("up", i, true);
            attackLeft[i] = drawKnight("left", i, true);
            attackRight[i] = drawKnight("right", i, true);
        }
        image = walkDown[0];
    }

    private BufferedImage drawKnight(String dir, int frame, boolean attacking) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color armorDark = new Color(30, 30, 50);
        Color armorMid = new Color(60, 60, 90);
        Color armorLight = new Color(90, 90, 130);
        Color gold = new Color(180, 150, 40);
        Color eyeGlow = new Color(200, 50, 255);

        int legBob = (frame % 2 == 0) ? 2 : -2;

        // Greaves / boots
        g.setColor(armorDark);
        g.fillRoundRect(10, ts - 14 + legBob, 12, 10, 3, 3);
        g.fillRoundRect(26, ts - 14 - legBob, 12, 10, 3, 3);
        g.setColor(armorLight);
        g.drawRoundRect(10, ts - 14 + legBob, 12, 10, 3, 3);
        g.drawRoundRect(26, ts - 14 - legBob, 12, 10, 3, 3);

        // Thighs
        g.setColor(armorMid);
        g.fillRoundRect(10, ts - 24 + legBob / 2, 12, 14, 4, 4);
        g.fillRoundRect(26, ts - 24 - legBob / 2, 12, 14, 4, 4);

        // Chest plate
        g.setColor(armorDark);
        g.fillRoundRect(8, 20, 32, 18, 8, 8);
        g.setColor(armorMid);
        g.fillRoundRect(10, 22, 28, 14, 6, 6);
        // Gold trim
        g.setColor(gold);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(10, 22, 28, 14, 6, 6);
        g.drawLine(ts / 2, 22, ts / 2, 36);

        // Shoulder pads
        g.setColor(armorDark);
        g.fillOval(4, 18, 14, 12);
        g.fillOval(30, 18, 14, 12);
        g.setColor(armorLight);
        g.setStroke(new BasicStroke(1));
        g.drawOval(4, 18, 14, 12);
        g.drawOval(30, 18, 14, 12);

        // Arms
        int armSwing = attacking ? 8 : ((frame % 2 == 0) ? 3 : -3);
        g.setColor(armorMid);
        g.fillRoundRect(3, 24 + armSwing, 8, 14, 4, 4);
        g.fillRoundRect(37, 24 - armSwing, 8, 14, 4, 4);

        // Sword (right hand)
        if (!dir.equals("left")) {
            g.setColor(new Color(150, 150, 180));
            int sx = 44, sy = 26 - armSwing;
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(sx, sy, sx - 4, sy + (attacking ? 18 : 14));
            g.setColor(gold);
            g.fillRect(sx - 5, sy + 2, 9, 3);
        }

        // Helmet
        g.setColor(armorDark);
        g.fillOval(9, 4, 30, 22);
        // Visor
        g.setColor(armorMid);
        g.fillRoundRect(11, 14, 26, 10, 4, 4);
        // Gold trim on helmet
        g.setColor(gold);
        g.setStroke(new BasicStroke(2));
        g.drawOval(9, 4, 30, 22);
        // Eye glow through visor
        g.setColor(eyeGlow);
        g.fillOval(14, 15, 8, 7);
        g.fillOval(26, 15, 8, 7);
        // Purple aura on attack
        if (attacking) {
            g.setColor(new Color(150, 50, 255, 80));
            g.fillOval(2, 2, ts - 4, ts - 4);
        }

        g.setStroke(new BasicStroke(1));
        g.dispose();
        return img;
    }

    @Override
    protected void updateAI() {
        // Dark Knight has a charge attack
        if (chargeTimer > 0) {
            chargeTimer--;
            switch (direction) {
                case "up":
                    worldY -= speed * 4;
                    break;
                case "down":
                    worldY += speed * 4;
                    break;
                case "left":
                    worldX -= speed * 4;
                    break;
                case "right":
                    worldX += speed * 4;
                    break;
            }
            return;
        }
        super.updateAI();
    }

    @Override
    protected void performAttack(double dx, double dy, int dist) {
        // 30% chance to charge
        if (Math.random() < 0.3 && chargeTimer == 0) {
            charging = true;
            chargeTimer = 15;
            if (Math.abs(dx) > Math.abs(dy))
                direction = dx > 0 ? "right" : "left";
            else
                direction = dy > 0 ? "down" : "up";
        }
        super.performAttack(dx, dy, dist);
    }
}