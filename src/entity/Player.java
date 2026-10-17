package entity;

import core.GamePanel;
import core.GameState;
import core.KeyHandler;
import combat.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Player extends Entity {
    private KeyHandler key;

    // Stats
    public int attackDamage = 10;
    public int defense = 0;
    public int gold = 2500;
    public int potionCount = 0;
    public int level = 1;
    public int xp = 0;
    public int xpToLevel = 100;
    public boolean hasWeapon = false;
    public boolean hasShield = false;

    // Horse (owned once, ride with C)
    public boolean hasHorse = false;
    public boolean onHorse = false;
    public static final int SPEED_WALK = 3;
    public static final int SPEED_RIDE = 6;

    // Horse riding sprites (built when mounted)
    private BufferedImage[] rideDown  = new BufferedImage[4];
    private BufferedImage[] rideUp    = new BufferedImage[4];
    private BufferedImage[] rideLeft  = new BufferedImage[4];
    private BufferedImage[] rideRight = new BufferedImage[4];

    // Credit Score (Social Standing)
    public int creditScore = 0;
    public int totalSpent = 0;
    public static final int CREDIT_BRONZE = 500;
    public static final int CREDIT_SILVER = 2000;
    public static final int CREDIT_GOLD = 5000;
    public static final int CREDIT_PLATINUM = 10000;

    // Attack state
    public boolean attacking = false;
    public int attackTimer = 0;
    public static final int ATTACK_DURATION = 20;
    public List<AttackHitbox> activeHitboxes = new LinkedList<>();

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
        speed = SPEED_WALK;
        width = gp.tileSize;
        height = gp.tileSize;
        collisionBox = new Rectangle(8, 16, 32, 28);
        invincibleDuration = 90;

        // Start position (village)
        worldX = gp.tileSize * 10;
        worldY = gp.tileSize * 40;

        buildSprites();
    }

    /** Grant the player ownership of a horse (bought once at the Horse Stall). */
    public void buyHorse() {
        hasHorse = true;
    }

    /** Toggle riding the horse with [C]. Requires owning one. */
    public void toggleMount() {
        if (!hasHorse) {
            gp.ui.showNotification("You need to buy a Horse first! (Horse Stall)");
            return;
        }
        onHorse = !onHorse;
        speed = onHorse ? SPEED_RIDE : SPEED_WALK;
        if (onHorse) {
            gp.ui.showNotification("Mounted your Horse! Speed " + SPEED_RIDE + " - press [C] to dismount.");
        } else {
            gp.ui.showNotification("Dismounted. Back to walking on foot.");
        }
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
        for (int i = 0; i < 4; i++) {
            rideDown[i]  = createRideSprite("down",  i);
            rideUp[i]    = createRideSprite("up",    i);
            rideLeft[i]  = createRideSprite("left",  i);
            rideRight[i] = createRideSprite("right", i);
        }
        image = walkDown[0];
    }

    private BufferedImage getRideFrame() {
        BufferedImage[] f;
        if ("up".equals(direction))         f = rideUp;
        else if ("left".equals(direction))  f = rideLeft;
        else if ("right".equals(direction)) f = rideRight;
        else                                f = rideDown;
        return f[spriteNum % f.length];
    }

    private BufferedImage getRideIdle() {
        if ("up".equals(direction))         return rideUp[0];
        else if ("left".equals(direction))  return rideLeft[0];
        else if ("right".equals(direction)) return rideRight[0];
        return rideDown[0];
    }

    /**
     * Procedural top-down horse + rider sprite. The horse's legs, tail and the
     * rider bob/swing with the animation frame, giving a visible riding gait.
     */
    private BufferedImage createRideSprite(String dir, int frame) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color horseBody = new Color(139, 90, 50);
        Color horseDark = new Color(96, 60, 33);
        Color horseMane = new Color(58, 36, 20);
        Color hoof      = new Color(38, 24, 13);
        Color muzzle    = new Color(70, 45, 26);
        Color saddle    = new Color(122, 44, 42);
        Color skin      = new Color(245, 200, 160);
        Color hair      = new Color(80, 50, 30);
        Color tunic     = new Color(50, 120, 70);

        int cx = ts / 2;
        int bob  = (frame % 2 == 0) ? 0 : 1;   // gentle body bob
        int legA = (frame % 2 == 0) ? 3 : -3;  // leg swing

        boolean side  = dir.equals("left") || dir.equals("right");
        boolean right = dir.equals("right");

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(cx - 16, ts - 9, 32, 7);

        // ---- LEGS (drawn first, behind the body) ----
        int legTop = ts - 20;
        int[] legXs;
        if (side) legXs = new int[]{cx - 12, cx - 4, cx + 4, cx + 12};
        else      legXs = new int[]{cx - 13, cx - 6, cx + 2, cx + 10};
        for (int i = 0; i < legXs.length; i++) {
            int off = (i % 2 == 0) ? legA : -legA;
            g.setColor(horseDark);
            g.fillRoundRect(legXs[i], legTop + off, 6, 12, 3, 3);
            g.setColor(hoof);
            g.fillRoundRect(legXs[i], legTop + off + 8, 6, 4, 2, 2);
        }

        // ---- TAIL (behind, opposite the head) ----
        g.setColor(horseMane);
        if (side) {
            int tx = right ? cx - 21 : cx + 14;
            int toff = (frame % 2 == 0) ? 0 : 2;
            g.fillRoundRect(tx, 20 + bob + toff, 7, 15, 5, 5);
        } else if (dir.equals("down")) {
            g.fillRoundRect(cx - 4, 14 + bob, 8, 12, 5, 5);
        } else { // up
            g.fillRoundRect(cx - 4, ts - 24 + bob, 8, 12, 5, 5);
        }

        // ---- NECK + HEAD ----
        if (side) {
            int hx = right ? cx + 9 : cx - 21;
            g.setColor(horseDark);
            g.fillRoundRect(hx, 15 + bob, 13, 15, 5, 5);
            g.setColor(horseBody);
            g.fillRoundRect(hx + 1, 16 + bob, 11, 13, 5, 5);
            // muzzle
            g.setColor(muzzle);
            g.fillRoundRect(right ? hx + 6 : hx + 2, 25 + bob, 8, 6, 3, 3);
            // mane
            g.setColor(horseMane);
            g.fillRect(right ? hx + 2 : hx + 9, 15 + bob, 3, 13);
            // ear
            g.setColor(horseBody);
            g.fillPolygon(new int[]{hx + 6, hx + 9, hx + 12},
                          new int[]{16 + bob, 9 + bob, 16 + bob}, 3);
        } else {
            boolean down = dir.equals("down");
            int hy = down ? 33 + bob : 1 + bob;
            g.setColor(horseDark);
            g.fillRoundRect(cx - 6, hy, 12, 14, 5, 5);
            g.setColor(horseBody);
            g.fillRoundRect(cx - 5, hy + 1, 10, 12, 5, 5);
            // muzzle
            g.setColor(muzzle);
            g.fillRoundRect(cx - 4, down ? hy + 9 : hy + 1, 8, 4, 2, 2);
            // ears
            g.setColor(horseBody);
            g.fillPolygon(new int[]{cx - 6, cx - 3, cx}, new int[]{down ? hy : hy + 12, down ? hy - 5 : hy + 17, down ? hy : hy + 12}, 3);
            g.fillPolygon(new int[]{cx, cx + 3, cx + 6}, new int[]{down ? hy : hy + 12, down ? hy - 5 : hy + 17, down ? hy : hy + 12}, 3);
            // mane
            g.setColor(horseMane);
            g.fillRect(cx - 4, down ? hy + 2 : hy + 11, 8, 3);
            // eyes (only when facing the camera)
            if (down) {
                g.setColor(Color.WHITE);
                g.fillOval(cx - 4, hy + 3, 3, 3);
                g.fillOval(cx + 1, hy + 3, 3, 3);
                g.setColor(Color.BLACK);
                g.fillOval(cx - 3, hy + 4, 2, 2);
                g.fillOval(cx + 2, hy + 4, 2, 2);
            }
        }

        // ---- BODY ----
        g.setColor(horseDark);
        g.fillRoundRect(cx - 15, 20 + bob, 30, 19, 10, 10);
        g.setColor(horseBody);
        g.fillRoundRect(cx - 14, 21 + bob, 28, 16, 10, 10);
        // shading along the flank
        g.setColor(new Color(0, 0, 0, 55));
        g.fillRoundRect(cx - 14, 32 + bob, 28, 5, 8, 8);

        // ---- SADDLE ----
        g.setColor(saddle);
        g.fillRoundRect(cx - 8, 21 + bob, 16, 7, 4, 4);
        g.setColor(new Color(255, 210, 120));
        g.fillRect(cx - 8, 24 + bob, 16, 1);

        // ---- RIDER ----
        int rBob = bob;
        // torso
        g.setColor(tunic);
        g.fillRoundRect(cx - 7, 9 + rBob, 15, 13, 5, 5);
        g.setColor(new Color(60, 40, 20));
        g.fillRect(cx - 7, 18 + rBob, 15, 3);
        // arms (hold reins toward the head side)
        g.setColor(tunic);
        int armOff = (frame % 2 == 0) ? 1 : -1;
        if (side) {
            int ax = right ? cx + 4 : cx - 10;
            g.fillRoundRect(ax, 12 + rBob + armOff, 6, 9, 3, 3);
        } else {
            g.fillRoundRect(cx - 11, 12 + rBob + armOff, 5, 9, 3, 3);
            g.fillRoundRect(cx + 7, 12 + rBob - armOff, 5, 9, 3, 3);
        }
        // head
        g.setColor(skin);
        g.fillOval(cx - 7, 1 + rBob, 14, 13);
        g.setColor(hair);
        if (dir.equals("up")) {
            g.fillOval(cx - 7, 0 + rBob, 14, 12);           // back of the head
        } else {
            g.fillArc(cx - 7, 0 + rBob, 14, 10, 0, 180);    // hair on top
            if (dir.equals("down")) {
                g.setColor(new Color(40, 40, 50));
                g.fillOval(cx - 5, 5 + rBob, 3, 4);
                g.fillOval(cx + 2, 5 + rBob, 3, 4);
            } else if (dir.equals("left")) {
                g.setColor(new Color(40, 40, 50));
                g.fillOval(cx - 6, 5 + rBob, 3, 4);
            } else if (dir.equals("right")) {
                g.setColor(new Color(40, 40, 50));
                g.fillOval(cx + 3, 5 + rBob, 3, 4);
            }
        }

        g.dispose();
        return img;
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

        // Mount / dismount horse with [C]
        if (key.mountJustPressed) {
            toggleMount();
        }

        // Fire logic
                // Fire logic
        if (fireCooldown > 0) fireCooldown--;
        if (key.firePressed && fireCooldown <= 0) {
            fireCooldown = FIRE_RATE;
            int px = worldX + width / 2 - 8;
            int py = worldY + height / 2 - 8;

            // Determine shooting direction based on currently held movement keys
            String shootDir = direction; // Fallback to current facing direction if no keys held
            if (key.upPressed && key.leftPressed) {
                shootDir = "up-left";
            } else if (key.upPressed && key.rightPressed) {
                shootDir = "up-right";
            } else if (key.downPressed && key.leftPressed) {
                shootDir = "down-left";
            } else if (key.downPressed && key.rightPressed) {
                shootDir = "down-right";
            } else if (key.upPressed) {
                shootDir = "up";
            } else if (key.downPressed) {
                shootDir = "down";
            } else if (key.leftPressed) {
                shootDir = "left";
            } else if (key.rightPressed) {
                shootDir = "right";
            }

            gp.projectiles.add(new Projectile(px, py, shootDir, attackDamage, this));
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
            gp.collisionChecker.checkObject(this, true);
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
        // Check Dance Club exit door with [E]
        int col = (worldX + width / 2) / gp.tileSize;
        int row = (worldY + height / 2) / gp.tileSize;
        if (col >= 86 && col <= 90 && row >= 96 && row <= 98) {
            gp.exitDanceClub();
            return;
        }
        // Check Horse Stable exit door with [E]
        if (col >= 66 && col <= 68 && row == 95) {
            gp.exitHorseStall();
            return;
        }
        // Check Horse Stall entrance door with [E] (tiles just below the stable)
        if (col >= 77 && col <= 79 && row >= 22 && row <= 23) {
            gp.enterHorseStall();
            return;
        }

        // Check NPC proximity
        if (gp.npcs != null) {
            Rectangle interactZone = new Rectangle(
                worldX + collisionBox.x - 10, worldY + collisionBox.y - 10,
                collisionBox.width + 20, collisionBox.height + 20
            );
            for (NPC npc : gp.npcs) {
                if (npc == null) continue;
                if (interactZone.intersects(npc.getWorldCollisionBox())) {
                    npc.startDialogue();
                    gp.gameState = GameState.DIALOGUE;
                    return;
                }
            }
        }
        // Check Chests and Buildings
        if (gp.objects != null) {
            Rectangle interactZone = new Rectangle(
                worldX + collisionBox.x - 10, worldY + collisionBox.y - 10,
                collisionBox.width + 20, collisionBox.height + 20
            );
            for (object.SuperObject obj : gp.objects) {
                if (obj == null) continue;
                if (obj instanceof object.OBJ_Chest) {
                    object.OBJ_Chest chest = (object.OBJ_Chest) obj;
                    if (!chest.opened && interactZone.intersects(obj.getWorldCollisionBox())) {
                        chest.onPickup(this);
                        return;
                    }
                } else if (obj instanceof object.OBJ_Coin) {
                    if (interactZone.intersects(obj.getWorldCollisionBox())) {
                        obj.onPickup(this);
                        obj.pickedUp = true;
                        return;
                    }
                } else if (!(obj instanceof object.OBJ_Potion || obj instanceof object.OBJ_Sword || obj instanceof object.OBJ_Shield)) {
                    if (interactZone.intersects(obj.getWorldCollisionBox())) {
                        obj.onPickup(this);
                        return;
                    }
                }
            }
        }
    }

    private void detectPickups() {
        if (gp.objects == null) return;
        // Generous pickup zone so touching or stepping near items instantly collects them
        Rectangle playerBox = new Rectangle(worldX - 6, worldY - 6, width + 12, height + 12);
        for (object.SuperObject obj : gp.objects) {
            if (obj == null || obj.pickedUp) continue;
            // Collect items from ground (Potion, Sword, Shield, Coin)
            if (obj instanceof object.OBJ_Potion || obj instanceof object.OBJ_Sword ||
                obj instanceof object.OBJ_Shield || obj instanceof object.OBJ_Coin) {
                if (playerBox.intersects(obj.getWorldCollisionBox())) {
                    obj.onPickup(this);
                    if (!(obj instanceof object.OBJ_Coin)) {
                        inventory.add(obj.name);
                    }
                    obj.pickedUp = true;
                }
            }
        }
    }

    private void updateZone() {
        int col = (worldX + width / 2) / gp.tileSize;
        int row = (worldY + height / 2) / gp.tileSize;
        String zone;

        // --- ZONE COORDINATES ---
        if (col >= 62 && col <= 72 && row >= 88 && row <= 96) {
            zone = "Dusty Gulch"; // Inside the Horse Stable interior
        } else if (col >= 78 && col <= 98 && row >= 86 && row <= 99) {
            zone = "Wild West Dance Club";
        } else if (row < 30) {
            if (col >= 58) zone = "Dusty Gulch";
            else zone = "Great Savannah";
        } else if (row >= 30 && row < 39) {
            zone = "Golden Meadows";
        } else if (row >= 39 && row < 62) {
            if (col < 33) zone = "Verdant Village";
            else zone = "Darkwood Forest";
        } else {
            if (col < 28) zone = "Crystal Caves";
            else zone = "Ancient Ruins";
        }

        boolean musicNotPlaying = gp.sound != null && !zone.equals(gp.sound.getCurrentTrack());

        if (!zone.equals(currentZone) || musicNotPlaying) {
            currentZone = zone;
            gp.questManager.onZoneEntered(zone);
            if (gp.sound != null) {
                if ("Wild West Dance Club".equals(zone)) {
                    gp.sound.play("Dusty Gulch");
                } else {
                    gp.sound.play(zone);
                }
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

    public String lastTierUp = null;

    public void addSpending(int goldAmount) {
        totalSpent += goldAmount;
        String oldTier = getCreditTier();
        creditScore = totalSpent;
        String newTier = getCreditTier();
        if (!newTier.equals(oldTier)) {
            lastTierUp = newTier;
        }
    }

    public String getCreditTier() {
        if (creditScore >= CREDIT_PLATINUM) return "Platinum";
        if (creditScore >= CREDIT_GOLD)     return "Gold";
        if (creditScore >= CREDIT_SILVER)   return "Silver";
        if (creditScore >= CREDIT_BRONZE)   return "Bronze";
        return "New";
    }

    public int getCreditDiscountPercent() {
        if (creditScore >= CREDIT_PLATINUM) return 30;
        if (creditScore >= CREDIT_GOLD)     return 20;
        if (creditScore >= CREDIT_SILVER)   return 10;
        if (creditScore >= CREDIT_BRONZE)   return 5;
        return 0;
    }

    public int getEffectivePrice(int basePrice) {
        int discount = getCreditDiscountPercent();
        return Math.max(1, basePrice * (100 - discount) / 100);
    }

    public int getHpBonusPerPurchase() {
        if (creditScore >= CREDIT_PLATINUM) return 60;
        if (creditScore >= CREDIT_GOLD)     return 50;
        if (creditScore >= CREDIT_SILVER)   return 40;
        if (creditScore >= CREDIT_BRONZE)   return 30;
        return 25;
    }

    // ── Enemy Mastery System (LC 347 Top-K Frequent Pattern) ──
    public static final int MASTERY_K = 3;

    public final java.util.Map<String, Integer> enemyKillCount = new java.util.HashMap<>();

    public final java.util.PriorityQueue<String> topKillHeap = new java.util.PriorityQueue<>(
        MASTERY_K + 1,
        (a, b) -> enemyKillCount.getOrDefault(a, 0) - enemyKillCount.getOrDefault(b, 0)
    );

    public final java.util.Set<String> topMasteredEnemies = new java.util.HashSet<>();
    public final java.util.Map<String, Integer> masteryRank = new java.util.HashMap<>();

    public void onEnemyKilled(String enemyType) {
        if (enemyType == null) return;

        enemyKillCount.put(enemyType, enemyKillCount.getOrDefault(enemyType, 0) + 1);

        topKillHeap.remove(enemyType);
        topKillHeap.offer(enemyType);

        while (topKillHeap.size() > MASTERY_K) {
            topKillHeap.poll();
        }

        topMasteredEnemies.clear();
        masteryRank.clear();
        java.util.List<String> sorted = new java.util.ArrayList<>(topKillHeap);
        sorted.sort((a, b) -> enemyKillCount.getOrDefault(b, 0) - enemyKillCount.getOrDefault(a, 0));
        for (int i = 0; i < sorted.size(); i++) {
            String type = sorted.get(i);
            topMasteredEnemies.add(type);
            masteryRank.put(type, i + 1);
        }
    }

    public double getMasteryDamageMultiplier(String enemyType) {
        if (!topMasteredEnemies.contains(enemyType)) return 1.0;
        int rank = masteryRank.getOrDefault(enemyType, 999);
        if (rank == 1) return 1.30;
        if (rank == 2) return 1.20;
        if (rank == 3) return 1.10;
        return 1.0;
    }

    public String getMasteryTitle() {
        if (masteryRank.isEmpty()) return null;
        for (java.util.Map.Entry<String, Integer> e : masteryRank.entrySet()) {
            if (e.getValue() == 1) return e.getKey() + "bane";
        }
        return null;
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
        if (onHorse) {
            frame = moving ? getRideFrame() : getRideIdle();
        } else if (attacking) {
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

        // Draw [E] Exit prompt when standing near the club door inside
        int c = (worldX + width / 2) / gp.tileSize;
        int r = (worldY + height / 2) / gp.tileSize;
        if (c >= 86 && c <= 90 && r >= 96 && r <= 98) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(screenX - 18, screenY - 22, 84, 18, 6, 6);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(Color.YELLOW);
            g2.drawString("[E] Exit Club", screenX - 12, screenY - 9);
        }

        // Draw [E] Exit prompt when standing at the Horse Stable door inside
        if (c >= 66 && c <= 68 && r == 95) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(screenX - 22, screenY - 22, 92, 18, 6, 6);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(Color.YELLOW);
            g2.drawString("[E] Leave Stable", screenX - 16, screenY - 9);
        }
    }
}
