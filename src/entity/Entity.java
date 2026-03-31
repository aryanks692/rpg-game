package entity;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {
    protected GamePanel gp;

    // World position
    public int worldX, worldY;
    public int width, height;

    // Movement
    public int speed;
    public String direction = "down";
    public boolean moving = false;

    // Status
    public int maxLife;
    public int life;
    public boolean alive = true;
    public boolean invincible = false;
    public int invincibleTimer = 0;
    public int invincibleDuration = 60; // frames

    // Collision box (relative to worldX, worldY)
    public Rectangle collisionBox;
    public boolean collisionOn = false;

    // Sprites
    protected BufferedImage[] walkDown  = new BufferedImage[4];
    protected BufferedImage[] walkUp    = new BufferedImage[4];
    protected BufferedImage[] walkLeft  = new BufferedImage[4];
    protected BufferedImage[] walkRight = new BufferedImage[4];
    protected BufferedImage[] attackDown  = new BufferedImage[2];
    protected BufferedImage[] attackUp    = new BufferedImage[2];
    protected BufferedImage[] attackLeft  = new BufferedImage[2];
    protected BufferedImage[] attackRight = new BufferedImage[2];

    protected int spriteCounter = 0;
    protected int spriteNum = 0;
    protected int animSpeed = 12;

    // Current image to draw
    public BufferedImage image;

    // Name and dialogue
    public String name = "";
    public String[] dialogues = new String[0];
    public int dialogueIndex = 0;

    // Knockback
    public int knockbackX = 0;
    public int knockbackY = 0;
    public int knockbackTimer = 0;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public abstract void update();
    public abstract void draw(Graphics2D g2);

    public Rectangle getWorldCollisionBox() {
        return new Rectangle(
            worldX + collisionBox.x,
            worldY + collisionBox.y,
            collisionBox.width,
            collisionBox.height
        );
    }

    protected void updateInvincibility() {
        if (invincible) {
            invincibleTimer++;
            if (invincibleTimer >= invincibleDuration) {
                invincible = false;
                invincibleTimer = 0;
            }
        }
    }

    protected void updateKnockback() {
        if (knockbackTimer > 0) {
            worldX += knockbackX;
            worldY += knockbackY;
            knockbackTimer--;
            if (knockbackTimer == 0) {
                knockbackX = 0;
                knockbackY = 0;
            }
        }
    }

    public void takeDamage(int dmg) {
        if (invincible) return;
        life -= dmg;
        invincible = true;
        invincibleTimer = 0;
        if (life <= 0) {
            life = 0;
            alive = false;
        }
    }

    protected void advanceAnimation() {
        spriteCounter++;
        if (spriteCounter >= animSpeed) {
            spriteCounter = 0;
            spriteNum = (spriteNum + 1) % 4;
        }
    }

    protected BufferedImage getWalkFrame() {
        BufferedImage[] frames = switch (direction) {
            case "up"    -> walkUp;
            case "left"  -> walkLeft;
            case "right" -> walkRight;
            default      -> walkDown;
        };
        if (frames == null || frames[spriteNum % frames.length] == null) return null;
        return frames[spriteNum % frames.length];
    }
}
