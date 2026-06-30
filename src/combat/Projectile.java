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

        switch (direction) {
            case "up":    worldY -= speed; break;
            case "down":  worldY += speed; break;
            case "left":  worldX -= speed; break;
            case "right": worldX += speed; break;
        }
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        if (!alive) return;
        int screenX = worldX - cameraX;
        int screenY = worldY - cameraY;

        // Draw fireball
        g2.setColor(new Color(255, 100, 30));
        g2.fillOval(screenX, screenY, 16, 16);
        g2.setColor(new Color(255, 200, 50));
        g2.fillOval(screenX + 3, screenY + 3, 10, 10);
        g2.setColor(new Color(255, 255, 200));
        g2.fillOval(screenX + 6, screenY + 6, 4, 4);
    }

    public Rectangle getWorldCollisionBox() {
        return new Rectangle(worldX, worldY, 16, 16);
    }
}
