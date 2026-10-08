package entity;

import core.GamePanel;
import core.GameState;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class NPC extends Entity {
    private int wanderTimer = 0;
    private int wanderDir = 0;

    // NPC appearance
    private Color bodyColor;
    private Color skinColor;
    private String role; // "villager", "merchant", "elder"

    public NPC(GamePanel gp, String name, String role, int worldX, int worldY, String... dialogues) {
        super(gp);
        this.name = name;
        this.role = role;
        this.worldX = worldX;
        this.worldY = worldY;
        this.dialogues = dialogues; // stored in Entity.dialogues
        this.maxLife = 50;
        this.life = 50;
        this.speed = 1;
        this.width = gp.tileSize;
        this.height = gp.tileSize;
        collisionBox = new Rectangle(8, 16, 32, 28);

        if ("merchant".equals(role))       bodyColor = new Color(180, 100, 40);
        else if ("elder".equals(role))     bodyColor = new Color(100, 100, 160);
        else if ("sheriff".equals(role))   bodyColor = new Color(90, 60, 35);
        else if ("police".equals(role))    bodyColor = new Color(45, 60, 95);
        else if ("dancer".equals(role))    bodyColor = new Color(195, 35, 75);
        else if ("bartender".equals(role)) bodyColor = new Color(230, 225, 210);
        else if ("cowboy".equals(role))    bodyColor = new Color(160, 110, 60);
        else                               bodyColor = new Color(180, 70, 70);

        if ("dancer".equals(role)) {
            skinColor = new Color(245, 220, 200); // White/fair skin tone
        } else {
            skinColor = new Color(255, 210, 170);
        }
        buildSprites();
    }

    public NPC setPortrait(String portraitPath) {
        if (portraitPath != null) {
            try {
                File file = new File(portraitPath);
                if (!file.exists()) {
                    File fallback = new File("src/res/npc/" + file.getName());
                    if (fallback.exists()) file = fallback;
                }
                this.portraitImage = ImageIO.read(file);
            } catch (Exception e) {
                System.err.println("Failed to load portrait for " + name + ": " + portraitPath);
            }
        }
        return this;
    }

    private void buildSprites() {
        for (int i = 0; i < 4; i++) {
            walkDown[i] = createNPCSprite("down", i);
            walkUp[i] = createNPCSprite("up", i);
            walkLeft[i] = createNPCSprite("left", i);
            walkRight[i] = createNPCSprite("right", i);
        }
        image = walkDown[0];
    }

    private BufferedImage createNPCSprite(String dir, int frame) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Use anti-aliasing for HD Stardew/RPG Maker sprite look
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int legOffset = (frame % 2 == 0) ? 2 : -2;

        // --- SHOES ---
        g.setColor(new Color(60, 40, 20));
        g.fillRoundRect(12, ts - 14 + legOffset, 10, 8, 3, 3);
        g.fillRoundRect(26, ts - 14 - legOffset, 10, 8, 3, 3);

        // --- BODY / DRESS ---
        if ("dancer".equals(role)) {
            // White/light skin tone
            skinColor = new Color(245, 220, 200);

            // --- Crop Top ---
            g.setColor(new Color(25, 15, 20)); // Black crop top
            g.fillRoundRect(13, 20, 22, 10, 4, 4);

            // Red/pink trim
            g.setColor(new Color(220, 35, 85));
            g.fillRect(14, 21, 20, 2);
            g.fillRect(16, 28, 16, 2);

            // Small center gold detail
            g.setColor(new Color(255, 215, 80));
            g.fillRect(22, 23, 4, 2);

            // --- Bare Midriff ---
            g.setColor(skinColor);
            g.fillRect(14, 30, 20, 5);

            // --- Short dance skirt / bikini-style bottom ---
            g.setColor(new Color(220, 35, 85));
            g.fillPolygon(
                new int[]{12, 36, 39, 9},
                new int[]{34, 34, 41, 41},
                4
            );

            // Black waistband
            g.setColor(new Color(25, 15, 20));
            g.fillRect(13, 34, 22, 3);

            // Pink highlights
            g.setColor(new Color(255, 80, 140));
            g.fillRect(17, 38, 5, 2);
            g.fillRect(26, 38, 5, 2);

        } else {
            g.setColor(bodyColor);
            g.fillRoundRect(10, 20, 28, 20, 6, 6);
            // Belt/waist shadow
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRect(10, 36, 28, 4);
        }

        // Bartender vest & apron
        if ("bartender".equals(role)) {
            g.setColor(new Color(65, 40, 20)); // Vest
            g.fillRect(10, 20, 7, 16);
            g.fillRect(31, 20, 7, 16);
            g.setColor(new Color(240, 240, 240)); // White Apron
            g.fillRect(14, 28, 20, 12);
        }

        // --- ARMS ---
        if ("dancer".equals(role)) {
            // Bare white/light skin arms
            g.setColor(skinColor);
            g.fillRoundRect(5, 20 + legOffset / 2, 7, 12, 3, 3);
            g.fillRoundRect(36, 20 - legOffset / 2, 7, 12, 3, 3);

            // Long evening gloves
            g.setColor(new Color(30, 15, 25));
            g.fillRoundRect(5, 27 + legOffset / 2, 7, 6, 2, 2);
            g.fillRoundRect(36, 27 - legOffset / 2, 7, 6, 2, 2);
        } else {
            g.setColor(bodyColor);
            g.fillRoundRect(4, 22 + legOffset / 2, 8, 12, 4, 4);
            g.fillRoundRect(36, 22 - legOffset / 2, 8, 12, 4, 4);
            
            // Arm shading gradient
            GradientPaint armShade = new GradientPaint(0, 30, new Color(0,0,0,0), 0, 34, new Color(0,0,0,100));
            g.setPaint(armShade);
            g.fillRoundRect(4, 22 + legOffset / 2, 8, 12, 4, 4);
            g.fillRoundRect(36, 22 - legOffset / 2, 8, 12, 4, 4);
        }

        // --- HEAD ---
        g.setColor(skinColor);
        g.fillOval(12, 8, 24, 20);
        // Neck shadow (ambient occlusion)
        g.setColor(new Color(0, 0, 0, 60));
        g.fillArc(12, 8, 24, 20, 180, 180); 

        // --- HAIR ---
        if ("dancer".equals(role)) {
            // Long flowing styled hair
            g.setColor(new Color(55, 25, 20));
            g.fillOval(10, 5, 28, 18);
            // Long side hair locks
            g.fillRoundRect(8, 12, 7, 20, 4, 4);
            g.fillRoundRect(33, 12, 7, 20, 4, 4);
        }
        else if ("elder".equals(role)) g.setColor(Color.LIGHT_GRAY);
        else g.setColor(new Color(100, 60, 20));
        if (!"dancer".equals(role)) g.fillOval(12, 6, 24, 14);

        // --- EYES & MAKEUP ---
        if (!dir.equals("up")) {
            int eyeOff = dir.equals("left") ? -3 : (dir.equals("right") ? 3 : 0);
            if ("dancer".equals(role)) {
                // Feminine eyes with mascara & blush
                g.setColor(new Color(255, 140, 160, 150)); // Blush
                g.fillOval(14 + eyeOff, 20, 4, 3);
                g.fillOval(30 + eyeOff, 20, 4, 3);
                g.setColor(Color.BLACK); // Mascara lashes
                g.fillOval(16 + eyeOff, 15, 4, 4);
                g.fillOval(28 + eyeOff, 15, 4, 4);
                g.setColor(new Color(80, 180, 255)); // Blue/Hazel irises
                g.fillOval(17 + eyeOff, 16, 2, 2);
                g.fillOval(29 + eyeOff, 16, 2, 2);
                g.setColor(new Color(230, 40, 70)); // Red lipstick
                g.fillRect(23 + eyeOff, 23, 3, 2);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillOval(16 + eyeOff, 17, 3, 4);
                g.fillOval(28 + eyeOff, 17, 3, 4);
            }
        }

        // --- ROLE SPECIFIC ACCESSORIES ---

        // 1. MERCHANT HAT
        if ("merchant".equals(role)) {
            g.setColor(new Color(80, 50, 20));
            g.fillRect(10, 5, 28, 5);
            g.fillRoundRect(14, 0, 20, 10, 4, 4);
            g.setColor(new Color(150, 30, 30));
            g.fillRect(14, 7, 20, 3);
        }

        // 2. SHERIFF & POLICE STAR & STETSON
        if ("sheriff".equals(role) || "police".equals(role)) {
            // Brown/Black Stetson
            g.setColor(new Color(45, 30, 15));
            g.fillRoundRect(8, 5, 32, 5, 4, 4);
            g.fillRoundRect(14, 0, 20, 8, 4, 4);
            g.setColor(new Color(220, 180, 40));
            g.fillRect(14, 4, 20, 2);
            // Gold Star Badge on Chest
            g.setColor(new Color(255, 215, 0));
            g.fillOval(14, 23, 6, 6);
            g.setColor(Color.WHITE);
            g.fillRect(16, 25, 2, 2);
        }

        // 3. DANCE CLUB DANCER FEATHERED HEADPIECE
        if ("dancer".equals(role)) {
            // Sparkling tiara / feathers
            g.setColor(new Color(255, 80, 140));
            g.fillOval(20, 0, 8, 10);
            g.fillOval(16, 2, 6, 8);
            g.fillOval(26, 2, 6, 8);
            g.setColor(new Color(255, 215, 0));
            g.fillRect(14, 6, 20, 3);
            // Sparkle
            g.setColor(Color.WHITE);
            g.fillRect(23, 2, 2, 2);
        }

        // 4. COWBOY HAT
        if ("cowboy".equals(role)) {
            g.setColor(new Color(120, 80, 40));
            g.fillRoundRect(8, 5, 32, 5, 4, 4);
            g.fillRoundRect(14, 0, 20, 8, 4, 4);
            g.setColor(new Color(180, 40, 40)); // Red Band
            g.fillRect(14, 4, 20, 2);
        }

        // 5. ELDER STAFF
        if ("elder".equals(role)) {
            g.setColor(new Color(100, 80, 40));
            g.fillRect(38, 14, 4, 28);
            g.setColor(new Color(150, 220, 255));
            g.fillOval(35, 10, 10, 10);
            g.setColor(new Color(255, 255, 255, 150));
            g.fillOval(38, 12, 4, 4);
        }
        
        g.dispose();
        return img;
    }

    @Override
    public void update() {
        // Idle wander
        wanderTimer++;
        if (wanderTimer > 120) {
            wanderTimer = 0;
            wanderDir = (int) (Math.random() * 5); // 0-3 move, 4 = idle
        }
        if (wanderDir < 4) {
            String[] dirs = { "up", "down", "left", "right" };
            direction = dirs[wanderDir];
            gp.collisionChecker.checkTile(this);
            gp.collisionChecker.checkObject(this, false); // <-- ADD THIS LINE
            if (!collisionOn) {
                switch (direction) {
                    case "up":    worldY -= speed; break;
                    case "down":  worldY += speed; break;
                    case "left":  worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
                moving = true;
                advanceAnimation();
            } else {
                wanderDir = 4; // stop
                moving = false;
            }
        } else {
            moving = false;
        }
    }

    public void startDialogue() {
        dialogueIndex = 0; // reset Entity's dialogueIndex
        awaitingChoice = false;
        gp.currentDialogueEntity = this;
        gp.gameState = core.GameState.DIALOGUE;
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;
        if (screenX + width < 0 || screenX > gp.screenWidth)
            return;
        if (screenY + height < 0 || screenY > gp.screenHeight)
            return;

        // Name plate above NPC when nearby
        int px = gp.player.worldX, py = gp.player.worldY;
        double dist = Math.sqrt(Math.pow(worldX - px, 2) + Math.pow(worldY - py, 2));
        if (dist < gp.tileSize * 3) {
            // [E] prompt
            g2.setColor(new Color(0, 0, 0, 180));
            int promptW = 70;
            g2.fillRoundRect(screenX + width / 2 - promptW / 2, screenY - 28, promptW, 18, 8, 8);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(Color.YELLOW);
            g2.drawString("[E] " + name, screenX + width / 2 - 28, screenY - 14);
        }

        BufferedImage frame = moving ? getWalkFrame() : walkDown[0];
        if (frame != null)
            g2.drawImage(frame, screenX, screenY, width, height, null);
    }
}
