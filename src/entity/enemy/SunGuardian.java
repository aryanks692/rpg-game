package entity.enemy;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * THE SUN-GUARDIAN
 * A massive solar-powered boss logic with custom superhero-style rendering.
 */
public class SunGuardian extends Enemy {
    private int auraTimer = 0;
    private int dashCooldown = 0;

   public SunGuardian(GamePanel gp, int worldX, int worldY) {
    super(gp, worldX, worldY, gp.tileSize * 15);

    this.type = "SunGuardian";
    this.isBoss = true;

    // Boss Stats — challenging but beatable
    this.maxLife = 60;
    this.life = maxLife;

    this.speed = 2;

    this.attackDamage = 7;           // Reduced: won't one-shot the player
    this.attackRange = gp.tileSize * 2; // Slightly shorter reach
    this.attackCooldownMax = 240;    // Longer gap between attacks
    this.invincibleDuration = 15;    // Short i-frames so hits register quickly
    this.xpReward = 150;
    this.goldReward = 80;

    // Massive Size
    this.width = gp.tileSize * 3;
    this.height = gp.tileSize * 3;
    this.collisionBox = new Rectangle(8 * 3, 16 * 3, 32 * 3, 28 * 3);

    this.name = "THE SUN-GUARDIAN";

    this.dialogues = new String[] {
        "Angena gatram.",
        "Nayanen vakratam.",
        "Jñānena rājyam.",
        "Labdhena bhojyam."
    };

    buildSprites();
}
    private void buildSprites() {
        for (int i = 0; i < 4; i++) {
            walkDown[i] = drawSunGuardian("down", i, false);
            walkUp[i] = drawSunGuardian("up", i, false);
            walkLeft[i] = drawSunGuardian("left", i, false);
            walkRight[i] = drawSunGuardian("right", i, false);
        }
        for (int i = 0; i < 2; i++) {
            attackDown[i] = drawSunGuardian("down", i, true);
            attackUp[i] = drawSunGuardian("up", i, true);
            attackLeft[i] = drawSunGuardian("left", i, true);
            attackRight[i] = drawSunGuardian("right", i, true);
        }
        image = walkDown[0];
    }

    private BufferedImage drawSunGuardian(String dir, int frame, boolean attacking) {
        int ts = gp.tileSize * 3; // 3x3 Massive Sprite
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Core Colors
        Color skin = new Color(245, 220, 185);
        Color armorGold = new Color(220, 175, 45);
        Color suitBlue = new Color(25, 35, 75);
        Color capeRed = new Color(180, 20, 20);
        Color eyeGlow = new Color(255, 40, 40);

        int bob = (frame % 2 == 0) ? 3 : -3;

        // 1. Shadow & Aura
        g.setColor(new Color(0, 0, 0, 60));
        g.fillOval(24, ts - 30, 96, 24);

        // 2. The Cape (Superhero style, flowing behind)
        g.setColor(capeRed);
        int[] cx = { 36, 108, 132, 12 };
        int[] cy = { 60, 60, ts - 12 + bob, ts - 12 - bob };
        g.fillPolygon(cx, cy, 4);
        g.setColor(new Color(130, 10, 10)); // inner shadow
        g.drawPolygon(cx, cy, 4);

        // 3. Legs
        g.setColor(suitBlue);
        g.fillRoundRect(39, ts - 60 + bob, 24, 36, 12, 12);
        g.fillRoundRect(81, ts - 60 - bob, 24, 36, 12, 12);
        g.setColor(armorGold); // Boots
        g.fillRoundRect(36, ts - 24 + bob, 30, 18, 6, 6);
        g.fillRoundRect(78, ts - 24 - bob, 30, 18, 6, 6);

        // 4. Torso (Muscular suit)
        g.setColor(suitBlue);
        g.fillRoundRect(30, 54, 84, 66, 30, 30);

        // 5. Heroic Emblem (Sun/Lotus style)
        g.setColor(armorGold);
        int[] ex = { ts / 2, ts / 2 + 24, ts / 2, ts / 2 - 24 };
        int[] ey = { 66, 84, 102, 84 };
        g.fillPolygon(ex, ey, 4);
        g.setColor(new Color(255, 240, 150));
        g.fillOval(ts / 2 - 9, 78, 18, 18);

        // 6. Arms
        g.setColor(suitBlue);
        int armS = attacking ? 18 : bob * 2;
        g.fillRoundRect(12, 66 + armS, 24, 42, 12, 12);
        g.fillRoundRect(108, 66 - armS, 24, 42, 12, 12);
        g.setColor(armorGold); // Gauntlets
        g.fillRoundRect(12, 96 + armS, 24, 18, 6, 6);
        g.fillRoundRect(108, 96 - armS, 24, 18, 6, 6);

        // 7. Head (Regal face)
        g.setColor(skin);
        g.fillOval(42, 12, 60, 60);

        // Hair/Beard (White/Silver)
        g.setColor(new Color(230, 230, 240));
        g.fillOval(42, 12, 60, 18); // top hair
        g.fillArc(42, 42, 60, 36, 180, 180); // beard

        // 8. Glowing Eyes
        g.setColor(eyeGlow);
        g.fillOval(54, 36, 12, 12);
        g.fillOval(78, 36, 12, 12);
        // Small flares
        g.setColor(new Color(255, 100, 100, 150));
        g.setStroke(new BasicStroke(3));
        g.drawLine(54, 42, 30, 42);
        g.drawLine(78, 42, 114, 42);

        g.dispose();
        return img;
    }

    @Override
    protected void updateAI() {
        if (!dialogueCompleted) {
            // Check if player is close to trigger dialogue
            int tx = gp.player.worldX;
            int ty = gp.player.worldY;
            double d = Math.sqrt(Math.pow(tx - worldX, 2) + Math.pow(ty - worldY, 2));
            if (d < gp.tileSize * 6) {
                gp.currentDialogueEntity = this;
                gp.gameState = core.GameState.DIALOGUE;
                // For simplicity, we flag as triggered
                dialogueCompleted = true;
                gp.ui.showNotification("A LEGENDARY FOE APPEARS!");
            }
            return;
        }

        // Standard Boss AI
        super.updateAI();

        // Pulsing Solar Flare logic
        auraTimer++;
        if (auraTimer > 240 && dashCooldown <= 0) {
            // Dash charge — speed capped to 5 so player can still evade
            speed = 5;
            dashCooldown = 50;
            auraTimer = 0;
            gp.ui.showNotification("Sun Guardian charges!");
        } else if (dashCooldown > 0) {
            dashCooldown--;
            if (dashCooldown == 0)
                speed = 2;
        }
    }
}
