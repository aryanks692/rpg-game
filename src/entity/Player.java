package entity;

import core.GamePanel;
import core.GameState;
import core.KeyHandler;
import combat.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
    private KeyHandler key;

    // Stats
    public int attackDamage = 10;
    public int defense = 0;
    public int gold = 50;
    public int potionCount = 0;
    public int level = 1;
    public int xp = 0;
    public int xpToLevel = 100;
    public boolean hasWeapon = false;
    public boolean hasShield = false;

    // Attack state
    public boolean attacking = false;
    public int attackTimer = 0;
    public static final int ATTACK_DURATION = 20;
    public List<AttackHitbox> activeHitboxes = new ArrayList<>();

    public int fireCooldown = 0;
    public static final int FIRE_RATE = 20;

    // Dash/roll
    private boolean dashing = false;
    private int dashTimer = 0;
    private int dashCooldown = 0;
    private static final int DASH_DURATION = 12;
    private static final int DASH_COOLDOWN_MAX = 45;
    private static final int DASH_SPEED = 7;

    // Shield block
    public boolean blocking = false;

    // Zone tracking
    public String currentZone = "Verdant Village";

    // Inventory (simple list)
    public List<String> inventory = new ArrayList<>();

    public Player(GamePanel gp, KeyHandler key) {
        super(gp);
        this.key = key;
        maxLife = 100;
        life = maxLife;
        speed = 3;
        width = gp.tileSize;
        height = gp.tileSize;
        collisionBox = new Rectangle(8, 16, 32, 28);
        invincibleDuration = 90;

        // Start position (village)
        worldX = gp.tileSize * 10;
        worldY = gp.tileSize * 40;

        buildSprites();
    }

    private void buildSprites() {
        // Build animated walk sprites programmatically using colored shapes
        for (int i = 0; i < 4; i++) {
            walkDown[i]  = createPlayerSprite("down",  i);
            walkUp[i]    = createPlayerSprite("up",    i);
            walkLeft[i]  = createPlayerSprite("left",  i);
            walkRight[i] = createPlayerSprite("right", i);
        }
        for (int i = 0; i < 2; i++) {
            attackDown[i]  = createAttackSprite("down",  i);
            attackUp[i]    = createAttackSprite("up",    i);
            attackLeft[i]  = createAttackSprite("left",  i);
            attackRight[i] = createAttackSprite("right", i);
        }
        image = walkDown[0];
    }

    private BufferedImage createPlayerSprite(String dir, int frame) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Use anti-aliasing for HD Stardew/RPG Maker sprite look
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color skinColor = new Color(245, 200, 160);
        Color skinShadow = new Color(210, 160, 120);
        Color hairColor = new Color(80, 50, 30);
        Color tunicColor = new Color(50, 120, 70); // Return to classic RPG green
        Color pantsColor = new Color(100, 70, 40); // Brown adventurer pants
        Color bootColor = new Color(60, 40, 20);

        int legOffset = (frame % 2 == 0) ? 2 : -2;

        // --- SHOES ---
        int lShoeX = 12, rShoeX = 26;
        int lShoeY = ts - 14 + legOffset, rShoeY = ts - 14 - legOffset;
        if (dir.equals("left") || dir.equals("right")) {
            lShoeX = 12 + legOffset; rShoeX = 26 - legOffset;
            lShoeY = ts - 14; rShoeY = ts - 14;
        }
        g.setColor(bootColor);
        g.fillRoundRect(lShoeX, lShoeY, 10, 8, 3, 3);
        g.fillRoundRect(rShoeX, rShoeY, 10, 8, 3, 3);

        // --- PANTS ---
        g.setColor(pantsColor);
        g.fillRoundRect(14, 28, 20, 12, 4, 4);
        g.setColor(new Color(0, 0, 0, 80)); // Inner drop-shadow for depth
        g.fillRect(14, 28, 20, 4);

        // --- TUNIC (BODY) ---
        g.setColor(tunicColor);
        g.fillRoundRect(12, 18, 24, 16, 4, 4);
        
        // Tunic belt / buckle
        g.setColor(new Color(60, 40, 20));
        g.fillRect(12, 30, 24, 3);
        if (dir.equals("down")) {
            g.setColor(Color.YELLOW);
            g.fillRect(22, 29, 4, 5);
        }

        // --- ARMS (SLEEVES) ---
        int armSwing = (frame % 2 == 0) ? 3 : -3;
        int lArmX=8, rArmX=32;
        int lArmY=20+armSwing, rArmY=20-armSwing;
        
        if (dir.equals("left")) { rArmX=28; lArmX=28; }
        else if (dir.equals("right")) { rArmX=12; lArmX=12; }
        
        g.setColor(tunicColor);
        g.fillRoundRect(lArmX, lArmY, 8, 12, 4, 4);
        // Arm shading gradient
        GradientPaint armShade = new GradientPaint(0, lArmY + 8, new Color(0,0,0,0), 0, lArmY + 12, new Color(0,0,0,100));
        g.setPaint(armShade);
        g.fillRoundRect(lArmX, lArmY, 8, 12, 4, 4);
        g.fillRoundRect(rArmX, rArmY, 8, 12, 4, 4);

        // --- HEAD ---
        g.setColor(skinColor);
        g.fillOval(12, 8, 24, 20);
        // Neck shadow
        g.setColor(skinShadow);
        g.fillArc(12, 8, 24, 20, 180, 180); 

        // --- EYES ---
        if (!dir.equals("up")) {
            g.setColor(new Color(40, 40, 50));
            if (dir.equals("down")) { g.fillOval(16, 17, 4, 5); g.fillOval(28, 17, 4, 5); }
            else if (dir.equals("left")) { g.fillOval(14, 17, 4, 5); }
            else if (dir.equals("right")) { g.fillOval(30, 17, 4, 5); }
        }

        // --- HAIR ---
        g.setColor(hairColor);
        if (dir.equals("up")) {
            g.fillOval(12, 6, 24, 18); // fully cover back
        } else {
            g.fillArc(12, 6, 24, 16, 0, 180); // top hair
            if (dir.equals("down")) {
                // bangs
                g.fillPolygon(new int[]{12, 18, 15}, new int[]{14, 14, 20}, 3);
                g.fillPolygon(new int[]{36, 30, 33}, new int[]{14, 14, 20}, 3);
            } else if (dir.equals("left")) {
                g.fillPolygon(new int[]{12, 18, 15}, new int[]{14, 14, 20}, 3);
            } else if (dir.equals("right")) {
                g.fillPolygon(new int[]{36, 30, 33}, new int[]{14, 14, 20}, 3);
            }
        }

        g.dispose();
        return img;
    }

    private BufferedImage createAttackSprite(String dir, int frame) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw base player
        g.drawImage(createPlayerSprite(dir, 0), 0, 0, null);

        // Draw sword sweep effect
        g.setColor(new Color(200, 220, 255, frame == 0 ? 200 : 100));
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = ts / 2, cy = ts / 2;
        switch (dir) {
            case "down":  { g.drawLine(cx, cy, cx + (frame == 0 ? 16 : 8), cy + 20); break; }
            case "up":    { g.drawLine(cx, cy, cx - (frame == 0 ? 16 : 8), cy - 20); break; }
            case "left":  { g.drawLine(cx, cy, cx - 20, cy + (frame == 0 ? 10 : 5)); break; }
            case "right": { g.drawLine(cx, cy, cx + 20, cy - (frame == 0 ? 10 : 5)); break; }
        }
        g.dispose();
        return img;
    }

    @Override
    public void update() {
        updateInvincibility();
        updateKnockback();
        handleInput();
        advanceAnimation();
        detectPickups();
        updateZone();
    }

    private void handleInput() {
        // Pause toggle
        if (key.pauseJustPressed) {
            if (gp.gameState == GameState.PLAY) gp.gameState = GameState.PAUSE;
            else if (gp.gameState == GameState.PAUSE) gp.gameState = GameState.PLAY;
        }

        // Inventory toggle
        if (key.inventoryJustPressed) {
            if (gp.gameState == GameState.PLAY) gp.gameState = GameState.INVENTORY;
            else if (gp.gameState == GameState.INVENTORY) gp.gameState = GameState.PLAY;
        }

        // Quest log
        if (key.questJustPressed) {
            if (gp.gameState == GameState.PLAY) gp.gameState = GameState.QUEST_LOG;
            else if (gp.gameState == GameState.QUEST_LOG) gp.gameState = GameState.PLAY;
        }

        if (gp.gameState != GameState.PLAY) {
            key.clearJustPressed();
            return;
        }

        // Attack
        if (key.attackJustPressed) {
            startAttack();
        }

        // Fire logic
        if (fireCooldown > 0) fireCooldown--;
        if (key.firePressed && fireCooldown <= 0) {
            fireCooldown = FIRE_RATE;
            int px = worldX + width/2 - 8;
            int py = worldY + height/2 - 8;
            gp.projectiles.add(new Projectile(px, py, direction, attackDamage, this));
        }
        if (attacking) {
            attackTimer++;
            if (attackTimer >= ATTACK_DURATION) {
                attacking = false;
                attackTimer = 0;
                activeHitboxes.clear();
            }
        }

        // Shield
        blocking = key.shieldPressed && hasShield;

        // Dash
        if (dashCooldown > 0) dashCooldown--;
        if (key.dashPressed && !dashing && dashCooldown == 0) {
            dashing = true;
            dashTimer = DASH_DURATION;
            dashCooldown = DASH_COOLDOWN_MAX;
        }
        if (dashing) {
            dashTimer--;
            if (dashTimer <= 0) dashing = false;
        }

        // Movement
        boolean moved = false;
        int moveSpeed = dashing ? DASH_SPEED : speed;

        if (key.upPressed)    { direction = "up";    moved = true; }
        if (key.downPressed)  { direction = "down";  moved = true; }
        if (key.leftPressed)  { direction = "left";  moved = true; }
        if (key.rightPressed) { direction = "right"; moved = true; }

        moving = moved;

        if (moved && !attacking) {
            gp.collisionChecker.checkTile(this);
            if (!collisionOn) {
                switch (direction) {
                    case "up":    worldY -= moveSpeed; break;
                    case "down":  worldY += moveSpeed; break;
                    case "left":  worldX -= moveSpeed; break;
                    case "right": worldX += moveSpeed; break;
                }
            }
            // Clamp to world
            worldX = Math.max(0, Math.min(worldX, gp.maxWorldCol * gp.tileSize - width));
            worldY = Math.max(0, Math.min(worldY, gp.maxWorldRow * gp.tileSize - height));
        }

        // Interact
        if (key.interactJustPressed) {
            checkInteract();
        }

        // Use potion
        if (key.enterJustPressed && potionCount > 0) {
            life = Math.min(maxLife, life + 30);
            potionCount--;
        }

        key.clearJustPressed();
    }

    private void startAttack() {
        if (attacking) return;
        attacking = true;
        attackTimer = 0;
        activeHitboxes.clear();

        int hx = worldX, hy = worldY;
        // Increased hitbox size to make hitting enemies easier
        int hw = 60, hh = 60;
        switch (direction) {
            case "up":    { hx = worldX - 6;        hy = worldY - 45;            break; }
            case "down":  { hx = worldX - 6;        hy = worldY + height - 10;   break; }
            case "left":  { hx = worldX - 45;       hy = worldY - 6;             break; }
            case "right": { hx = worldX + width - 10; hy = worldY - 6;           break; }
        }
        int maxTargets = gp.enemies != null ? gp.enemies.length : 10;
        AttackHitbox hitbox = new AttackHitbox(hx, hy, hw, hh, attackDamage, this, maxTargets);
        activeHitboxes.add(hitbox);
    }

    private void checkInteract() {
        // Check NPC proximity
        if (gp.npcs == null) return;
        Rectangle interactZone = new Rectangle(
            worldX + collisionBox.x - 8, worldY + collisionBox.y - 8,
            collisionBox.width + 16, collisionBox.height + 16
        );
        for (NPC npc : gp.npcs) {
            if (npc == null) continue;
            if (interactZone.intersects(npc.getWorldCollisionBox())) {
                npc.startDialogue();
                gp.gameState = GameState.DIALOGUE;
                return;
            }
        }
        // Check chest
        if (gp.objects != null) {
            for (object.SuperObject obj : gp.objects) {
                if (obj == null || obj.pickedUp) continue;
                if (obj instanceof object.OBJ_Chest) {
                    object.OBJ_Chest chest = (object.OBJ_Chest) obj;
                    if (!chest.opened) {
                        if (interactZone.intersects(obj.getWorldCollisionBox())) {
                            chest.onPickup(this);
                        }
                    }
                }
            }
        }
    }

    private void detectPickups() {
        if (gp.objects == null) return;
        Rectangle playerBox = getWorldCollisionBox();
        for (object.SuperObject obj : gp.objects) {
            if (obj == null || obj.pickedUp || obj instanceof object.OBJ_Chest) continue;
            if (playerBox.intersects(obj.getWorldCollisionBox())) {
                obj.onPickup(this);
                inventory.add(obj.name);
            }
        }
    }

    private void updateZone() {
        int col = (worldX + width / 2) / gp.tileSize;
        int row = (worldY + height / 2) / gp.tileSize;
        String zone;

        // --- NEW ZONE COORDINATES (POST 30-ROW SHIFT) ---
        if (row < 30) {
            zone = "Great Savannah";
        } else if (row >= 30 && row < 39) {
            zone = "Golden Meadows";
        } else if (row >= 39 && row < 62) {
            if (col < 33) zone = "Verdant Village";
            else zone = "Darkwood Forest";
        } else {
            if (col < 28) zone = "Crystal Caves";
            else zone = "Ancient Ruins";
        }

        if (!zone.equals(currentZone)) {
            currentZone = zone;
            gp.questManager.onZoneEntered(zone);
            if (gp.sound != null) {
                gp.sound.play(zone);
            }
        }
    }

    public void gainXP(int amount) {
        xp += amount;
        while (xp >= xpToLevel) {
            xp -= xpToLevel;
            level++;
            xpToLevel = (int)(xpToLevel * 1.4);
            maxLife += 15;
            life = maxLife;
            attackDamage += 3;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;

        // Invincibility flash
        if (invincible && (invincibleTimer / 6) % 2 == 0) return;

        // Shield block aura
        if (blocking) {
            g2.setColor(new Color(100, 150, 255, 80));
            g2.fillOval(screenX - 4, screenY - 4, width + 8, height + 8);
        }

        // Dash trail
        if (dashing) {
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRect(screenX + 4, screenY + 4, width - 8, height - 8);
        }

        // Draw sprite
        BufferedImage frame;
        if (attacking) {
            int afr = (attackTimer < ATTACK_DURATION / 2) ? 0 : 1;
            BufferedImage[] af;
            switch (direction) {
                case "up":    af = attackUp;    break;
                case "left":  af = attackLeft;  break;
                case "right": af = attackRight; break;
                default:      af = attackDown;  break;
            }
            frame = af[afr];
        } else if (moving) {
            frame = getWalkFrame();
        } else {
            switch (direction) {
                case "up":    frame = walkUp[0];    break;
                case "left":  frame = walkLeft[0];  break;
                case "right": frame = walkRight[0]; break;
                default:      frame = walkDown[0];  break;
            }
        }
        if (frame != null) g2.drawImage(frame, screenX, screenY, width, height, null);

        // Attack hitbox visual (debug hint)
        if (attacking && !activeHitboxes.isEmpty()) {
            AttackHitbox hb = activeHitboxes.get(0);
            g2.setColor(new Color(255, 220, 50, 80));
            g2.fillRect(hb.box.x - gp.camera.x, hb.box.y - gp.camera.y, hb.box.width, hb.box.height);
            g2.setColor(new Color(255, 220, 50, 200));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(hb.box.x - gp.camera.x, hb.box.y - gp.camera.y, hb.box.width, hb.box.height);
            g2.setStroke(new BasicStroke(1));
        }
    }
}
