package entity.enemy;
import combat.Projectile;
import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Outlaw extends Enemy {
    public Outlaw(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, gp.tileSize * 7);
        type = "Outlaw";
        maxLife = 70;
        life = maxLife;
        speed = 2;
        attackDamage = 14;
        attackRange = gp.tileSize * 6;      // Ranged gun distance (6 tiles)
        attackCooldownMax = 70;            // Cadence between shots (~1.1 seconds)
        xpReward = 40;
        goldReward = 30;
        invincibleDuration = 18;
        buildSprites();
    }

    private void buildSprites() {
        for (int i = 0; i < 4; i++) {
            walkDown[i]  = drawOutlaw("down",  i, false);
            walkUp[i]    = drawOutlaw("up",    i, false);
            walkLeft[i]  = drawOutlaw("left",  i, false);
            walkRight[i] = drawOutlaw("right", i, false);
        }

        for (int i = 0; i < 2; i++) {
            attackDown[i]  = drawOutlaw("down",  i, true);
            attackUp[i]    = drawOutlaw("up",    i, true);
            attackLeft[i]  = drawOutlaw("left",  i, true);
            attackRight[i] = drawOutlaw("right", i, true);
        }

        image = walkDown[0];
    }

    private BufferedImage drawOutlaw(String dir, int frame, boolean attacking) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color dusterBrown = new Color(115, 75, 40);
        Color dusterDark  = new Color(75, 45, 20);
        Color bandanaRed  = new Color(185, 30, 30);
        Color hatColor    = new Color(55, 35, 20);
        Color hatBand     = new Color(180, 140, 50);
        Color skinColor   = new Color(225, 185, 145);
        Color knifeSteel  = new Color(200, 200, 215);

        int legBob = (frame % 2 == 0) ? 2 : -2;
        int armBob = attacking ? 8 : legBob;

        // --- 1. BOOTS & LEGS ---
        g2.setColor(new Color(40, 25, 15));
        g2.fillRoundRect(12, ts - 14 + legBob, 9, 10, 3, 3);
        g2.fillRoundRect(27, ts - 14 - legBob, 9, 10, 3, 3);

        // --- 2. DUSTER COAT BODY ---
        g2.setColor(dusterBrown);
        g2.fillRoundRect(10, 20, 28, 20, 6, 6);
        // Duster Coat Tails
        g2.setColor(dusterDark);
        g2.fillRect(10, 34, 12, 8);
        g2.fillRect(26, 34, 12, 8);

        // Gun Belt & Holster
        g2.setColor(new Color(50, 30, 15));
        g2.fillRect(10, 32, 28, 4);
        g2.fillRect(dir.equals("left") ? 8 : 36, 33, 4, 8); // Holster

        // --- 3. ARMS / WEAPON ---
        g2.setColor(dusterBrown);
        g2.fillRoundRect(dir.equals("left") ? 4 - armBob : 4, 22 + legBob, 8, 12, 4, 4);
        g2.fillRoundRect(dir.equals("right") ? 36 + armBob : 36, 22 - legBob, 8, 12, 4, 4);

        // Bowing Knife / Revolver in Hand
        if (attacking || dir.equals("right") || dir.equals("down")) {
            int wx = dir.equals("left") ? 4 - armBob : 38 + armBob;
            int wy = 24 + armBob;
            g2.setColor(knifeSteel);
            g2.fillRect(wx, wy, 10, 3);
            g2.setColor(Color.WHITE);
            g2.drawLine(wx + 2, wy, wx + 10, wy);
        }

        // --- 4. HEAD & BANDANA ---
        g2.setColor(skinColor);
        g2.fillOval(14, 8, 20, 18);

        // Red Bandana Mask (Lower Face)
        g2.setColor(bandanaRed);
        Polygon bandana = new Polygon();
        bandana.addPoint(14, 16);
        bandana.addPoint(34, 16);
        bandana.addPoint(24, 25);
        g2.fillPolygon(bandana);

        // Bandit Eyes
        if (!dir.equals("up")) {
            g2.setColor(Color.BLACK);
            int eyeOff = dir.equals("left") ? -2 : (dir.equals("right") ? 2 : 0);
            g2.fillRect(18 + eyeOff, 13, 3, 2);
            g2.fillRect(26 + eyeOff, 13, 3, 2);
        }

        // --- 5. COWBOY STETSON HAT ---
        g2.setColor(hatColor);
        // Wide Hat Brim
        g2.fillRoundRect(8, 6, 32, 6, 6, 6);
        // Crown
        g2.fillRoundRect(14, 0, 20, 9, 6, 6);
        // Hat Band
        g2.setColor(hatBand);
        g2.fillRect(14, 5, 20, 2);

        g2.dispose();
        return img;
    }
            @Override
    protected void performAttack(double dx, double dy, int dist) {
        // 1. Aim toward the player (supports both straight & diagonal angles)
        double absDx = Math.abs(dx);
        double absDy = Math.abs(dy);
        String shootDir;

        if (absDx > 0.4 * absDy && absDy > 0.4 * absDx) {
            String v = dy > 0 ? "down" : "up";
            String h = dx > 0 ? "right" : "left";
            shootDir = v + "-" + h; // "up-right", "up-left", "down-right", "down-left"
        } else if (absDx > absDy) {
            shootDir = dx > 0 ? "right" : "left";
        } else {
            shootDir = dy > 0 ? "down" : "up";
        }

        // 2. Turn Outlaw to face player while shooting
        if (absDx > absDy) {
            direction = dx > 0 ? "right" : "left";
        } else {
            direction = dy > 0 ? "down" : "up";
        }

        // 3. Fire bullet from gun position
        int bx = worldX + width / 2 - 8;
        int by = worldY + height / 2 - 8;
        gp.projectiles.add(new Projectile(bx, by, shootDir, attackDamage, this));
    
    }
}
