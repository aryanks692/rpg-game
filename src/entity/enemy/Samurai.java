
package entity.enemy;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Samurai extends Enemy {
    private int dashTimer = 0;
    private boolean dashing = false;

    public Samurai(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, gp.tileSize * 8);
        type = "Samurai";
        maxLife = 100;
        life = maxLife;
        speed = 3;
        attackDamage = 18;
        attackRange = gp.tileSize * 2;
        attackCooldownMax = 80;
        xpReward = 50;
        goldReward = 25;
        invincibleDuration = 15;
        buildSprites();
    }

    private void buildSprites() {
        for (int i = 0; i < 4; i++) {
            walkDown[i] = drawSamurai("down", i, false);
            walkUp[i] = drawSamurai("up", i, false);
            walkLeft[i] = drawSamurai("left", i, false);
            walkRight[i] = drawSamurai("right", i, false);
        }

        for (int i = 0; i < 2; i++) {
            attackDown[i] = drawSamurai("down", i, true);
            attackUp[i] = drawSamurai("up", i, true);
            attackLeft[i] = drawSamurai("left", i, true);
            attackRight[i] = drawSamurai("right", i, true);
        }

        image = walkDown[0];
    }

    private BufferedImage drawSamurai(String dir, int frame, boolean attacking) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color armorRed = new Color(120, 20, 20);
        Color armorDark = new Color(50, 20, 20);
        Color armorGold = new Color(220, 180, 60);
        Color clothBlue = new Color(30, 40, 90);
        Color skin = new Color(220, 180, 140);
        Color swordGray = new Color(180, 180, 190);

        int legMove = (frame % 2 == 0) ? 2 : -2;
        int armMove = attacking ? 8 : ((frame % 2 == 0) ? 2 : -2);

        // Legs
        g.setColor(clothBlue);
        g.fillRoundRect(12, ts - 22 + legMove, 10, 14, 4, 4);
        g.fillRoundRect(26, ts - 22 - legMove, 10, 14, 4, 4);

        // Sandals
        g.setColor(new Color(80, 50, 20));
        g.fillRoundRect(11, ts - 10 + legMove, 12, 5, 3, 3);
        g.fillRoundRect(25, ts - 10 - legMove, 12, 5, 3, 3);

        // Waist cloth
        g.setColor(clothBlue);
        g.fillRect(14, 30, 18, 10);

        // Chest armor
        g.setColor(armorDark);
        g.fillRoundRect(10, 16, 28, 18, 6, 6);

        g.setColor(armorRed);
        g.fillRoundRect(12, 18, 24, 14, 5, 5);

        g.setColor(armorGold);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(12, 18, 24, 14, 5, 5);
        g.drawLine(ts / 2, 18, ts / 2, 32);

        // Shoulder pads
        g.setColor(armorDark);
        g.fillOval(5, 18, 10, 10);
        g.fillOval(33, 18, 10, 10);

        g.setColor(armorRed);
        g.fillOval(6, 19, 8, 8);
        g.fillOval(34, 19, 8, 8);

        // Arms
        g.setColor(skin);
        g.fillRoundRect(6, 24 + armMove, 7, 12, 4, 4);
        g.fillRoundRect(35, 24 - armMove, 7, 12, 4, 4);

        // Katana
        if (!dir.equals("left")) {
            int sx = 40;
            int sy = 24 - armMove;

            g.setStroke(new BasicStroke(3));
            g.setColor(swordGray);
            g.drawLine(sx, sy, sx + 10, sy + (attacking ? -14 : -8));

            g.setColor(armorGold);
            g.fillRect(sx - 2, sy - 1, 6, 3);

            g.setColor(new Color(80, 40, 20));
            g.drawLine(sx, sy, sx - 4, sy + 6);
        }

        // Head
        g.setColor(skin);
        g.fillOval(12, 4, 24, 20);

        // Samurai helmet
        g.setColor(armorDark);
        g.fillArc(9, 1, 30, 18, 0, 180);

        g.setColor(armorGold);
        g.drawArc(9, 1, 30, 18, 0, 180);

        // Helmet crest
        g.setColor(armorGold);
        g.fillRect(21, 0, 6, 8);

        // Eyes
        g.setColor(Color.BLACK);
        g.fillOval(18, 13, 3, 3);
        g.fillOval(27, 13, 3, 3);

        // Angry eyebrows
        g.drawLine(16, 11, 21, 12);
        g.drawLine(26, 12, 31, 11);

        // Attack slash effect
        if (attacking) {
            g.setColor(new Color(255, 255, 255, 120));
            g.setStroke(new BasicStroke(4));
            g.drawArc(28, 8, 20, 20, 300, 80);
        }

        // Dash aura
        if (dashing) {
            g.setColor(new Color(255, 255, 255, 60));
            g.fillOval(0, 0, ts, ts);
        }

        g.dispose();
        return img;
    }

    @Override
    protected void updateAI() {
        // Samurai dash attack movement
        if (dashTimer > 0) {
            dashTimer--;

            switch (direction) {
                case "up" -> worldY -= speed * 5;
                case "down" -> worldY += speed * 5;
                case "left" -> worldX -= speed * 5;
                case "right" -> worldX += speed * 5;
            }

            if (dashTimer == 0) {
                dashing = false;
            }
            return;
        }

        super.updateAI();
    }

    @Override
    protected void performAttack(double dx, double dy, int dist) {
        // 35% chance to perform a dash slash
        if (Math.random() < 0.35 && dashTimer == 0) {
            dashing = true;
            dashTimer = 10;

            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }
        }

        super.performAttack(dx, dy, dist);
    }
}
