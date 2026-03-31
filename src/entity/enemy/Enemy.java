package entity.enemy;

import core.GamePanel;
import entity.Entity;
import entity.Player;
import combat.AttackHitbox;
import combat.DamageNumber;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy extends Entity {
    public enum State { PATROL, AGGRO, ATTACK, DEAD }
    public State aiState = State.PATROL;

    protected int aggroRange;    // pixels
    protected int attackRange;   // pixels
    protected int attackDamage;
    protected int attackCooldown = 0;
    protected int attackCooldownMax = 90;

    protected int patrolTimer = 0;
    protected int patrolDirTimer = 0;
    protected String patrolDir = "down";

    public int xpReward;
    public int goldReward;
    public String type;

    // Death animation
    protected float deathAlpha = 1.0f;
    public boolean readyToRemove = false;

    // Flash on hit
    protected int hitFlashTimer = 0;

    protected BufferedImage[] idleFrames = new BufferedImage[2];
    protected BufferedImage[] attackFrames = new BufferedImage[2];

    public Enemy(GamePanel gp, int worldX, int worldY, int aggroRange) {
        super(gp);
        this.worldX = worldX;
        this.worldY = worldY;
        this.aggroRange = aggroRange;
        this.width = gp.tileSize;
        this.height = gp.tileSize;
        collisionBox = new Rectangle(8, 16, 32, 28);
        invincibleDuration = 30;
    }

    @Override
    public void update() {
        if (!alive) {
            deathAlpha -= 0.03f;
            if (deathAlpha <= 0) readyToRemove = true;
            return;
        }
        if (hitFlashTimer > 0) hitFlashTimer--;
        if (attackCooldown > 0) attackCooldown--;
        updateInvincibility();
        updateKnockback();
        updateAI();
        advanceAnimation();
    }

    protected void updateAI() {
        Player player = gp.player;
        double dx = player.worldX - worldX;
        double dy = player.worldY - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        switch (aiState) {
            case PATROL -> {
                patrol();
                if (dist < aggroRange) aiState = State.AGGRO;
            }
            case AGGRO -> {
                moveTowardPlayer(dx, dy, dist);
                if (dist < attackRange) aiState = State.ATTACK;
                if (dist > aggroRange * 1.5) aiState = State.PATROL;
            }
            case ATTACK -> {
                if (dist > attackRange * 1.2) {
                    aiState = State.AGGRO;
                } else {
                    if (attackCooldown <= 0) {
                        performAttack(dx, dy, (int)dist);
                        attackCooldown = attackCooldownMax;
                    }
                }
            }
            case DEAD -> {}
        }
    }

    protected void patrol() {
        patrolTimer++;
        patrolDirTimer++;
        if (patrolDirTimer > 90) {
            patrolDirTimer = 0;
            String[] dirs = {"up","down","left","right"};
            patrolDir = dirs[(int)(Math.random() * 4)];
        }
        direction = patrolDir;
        gp.collisionChecker.checkTile(this);
        if (!collisionOn) {
            switch (direction) {
                case "up"    -> worldY -= 1;
                case "down"  -> worldY += 1;
                case "left"  -> worldX -= 1;
                case "right" -> worldX += 1;
            }
        }
        moving = true;
    }

    protected void moveTowardPlayer(double dx, double dy, double dist) {
        if (dist < 4) return;
        double nx = dx / dist, ny = dy / dist;
        int mx = (int)(nx * speed), my = (int)(ny * speed);

        if (Math.abs(dx) > Math.abs(dy)) {
            direction = dx > 0 ? "right" : "left";
        } else {
            direction = dy > 0 ? "down" : "up";
        }

        // Move X
        int oldX = worldX;
        worldX += mx;
        collisionBox = new Rectangle(8, 16, 32, 28);
        gp.collisionChecker.checkTile(this);
        if (collisionOn) worldX = oldX;

        // Move Y
        int oldY = worldY;
        worldY += my;
        gp.collisionChecker.checkTile(this);
        if (collisionOn) worldY = oldY;

        moving = true;
    }

    protected void performAttack(double dx, double dy, int dist) {
        // damage player if in range
        if (dist < attackRange) {
            int dmg = Math.max(1, attackDamage - gp.player.defense);
            gp.player.takeDamage(dmg);
            // Knockback toward player's opposite direction
            int kbX = (gp.player.worldX > worldX) ? 4 : -4;
            int kbY = (gp.player.worldY > worldY) ? 4 : -4;
            gp.player.knockbackX = kbX;
            gp.player.knockbackY = kbY;
            gp.player.knockbackTimer = 8;
            // Damage number
            gp.damageNumbers.add(new DamageNumber(
                gp.player.worldX + 16, gp.player.worldY, dmg, false
            ));
        }
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        hitFlashTimer = 10;
        // Knockback away from player
        double dx = worldX - gp.player.worldX;
        double dy = worldY - gp.player.worldY;
        double dist = Math.max(1, Math.sqrt(dx * dx + dy * dy));
        knockbackX = (int)(dx / dist * 5);
        knockbackY = (int)(dy / dist * 5);
        knockbackTimer = 8;

        if (!alive) {
            aiState = State.DEAD;
            gp.player.gainXP(xpReward);
            gp.player.gold += goldReward;
            gp.questManager.onEnemyKilled(type);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;
        if (screenX + width < 0 || screenX > gp.screenWidth) return;
        if (screenY + height < 0 || screenY > gp.screenHeight) return;

        Composite orig = g2.getComposite();
        if (!alive) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, deathAlpha)));
        } else if (hitFlashTimer > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        BufferedImage frame = getWalkFrame();
        if (frame != null) g2.drawImage(frame, screenX, screenY, width, height, null);

        g2.setComposite(orig);

        // Health bar (only when aggro'd)
        if (alive && aiState != State.PATROL) {
            drawHealthBar(g2, screenX, screenY);
        }

        // Aggro indicator
        if (alive && aiState == State.AGGRO) {
            g2.setColor(new Color(255, 80, 80, 180));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("!", screenX + width / 2 - 3, screenY - 4);
        }
    }

    protected void drawHealthBar(Graphics2D g2, int sx, int sy) {
        int barW = width - 8;
        int barH = 5;
        int barX = sx + 4;
        int barY = sy - 10;
        float ratio = (float) life / maxLife;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(barX - 1, barY - 1, barW + 2, barH + 2);
        g2.setColor(new Color(180, 0, 0));
        g2.fillRect(barX, barY, barW, barH);
        g2.setColor(new Color(50, 220, 50));
        g2.fillRect(barX, barY, (int)(barW * ratio), barH);
    }
}
