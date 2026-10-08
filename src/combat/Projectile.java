package combat;

import core.GamePanel;
import entity.Entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Projectile {
    public int worldX, worldY;
    public int speed = 9;
    public int maxLife = 50; 
    public int life = 0;
    public String direction;
    public int damage;
    public Entity user; 
    public boolean alive = true;
    public boolean hitEnemy = false;
    public Rectangle collisionBox;

    public Projectile(int x, int y, String direction, int damage, Entity user) {
        this.worldX = x;
        this.worldY = y;
        this.direction = direction;
        this.damage = damage;
        this.user = user;
        this.collisionBox = new Rectangle(0, 0, 16, 16);
    }

        public void update(GamePanel gp) {
        if (!alive) return;
        
        life++;
        if (life >= maxLife) {
            alive = false;
        }

        // Calculate diagonal speed so diagonal shots travel at the same velocity as straight shots (~6 px)
        int diagSpeed = (int) Math.round(speed / Math.sqrt(2));

        switch (direction) {
            case "up":         worldY -= speed; break;
            case "down":       worldY += speed; break;
            case "left":       worldX -= speed; break;
            case "right":      worldX += speed; break;
            case "up-left":    worldY -= diagSpeed; worldX -= diagSpeed; break;
            case "up-right":   worldY -= diagSpeed; worldX += diagSpeed; break;
            case "down-left":  worldY += diagSpeed; worldX -= diagSpeed; break;
            case "down-right": worldY += diagSpeed; worldX += diagSpeed; break;
        }
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        if (!alive) return;
        int screenX = worldX - cameraX;
        int screenY = worldY - cameraY;

        if (user instanceof entity.enemy.Outlaw) {
            // Draw Revolver Bullet (Golden brass with bright core)
            g2.setColor(new Color(230, 180, 40));
            g2.fillOval(screenX + 3, screenY + 3, 10, 10);
            g2.setColor(new Color(255, 240, 150));
            g2.fillOval(screenX + 5, screenY + 5, 6, 6);
            g2.setColor(Color.WHITE);
            g2.fillOval(screenX + 7, screenY + 7, 2, 2);
        } else {
            // Draw fireball
            g2.setColor(new Color(255, 100, 30));
            g2.fillOval(screenX, screenY, 16, 16);
            g2.setColor(new Color(255, 200, 50));
            g2.fillOval(screenX + 3, screenY + 3, 10, 10);
            g2.setColor(new Color(255, 255, 200));
            g2.fillOval(screenX + 6, screenY + 6, 4, 4);
        }
    }

    public Rectangle getWorldCollisionBox() {
        return new Rectangle(worldX, worldY, 16, 16);
    }
}
