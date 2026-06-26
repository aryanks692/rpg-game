package entity;

import core.GamePanel;
import core.GameState;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NPC extends Entity {
    public String[] dialogues;
    private int dialogueIndex = 0;
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
        this.dialogues = dialogues;
        this.maxLife = 50;
        this.life = 50;
        this.speed = 1;
        this.width = gp.tileSize;
        this.height = gp.tileSize;
        collisionBox = new Rectangle(8, 16, 32, 28);

        if ("merchant".equals(role))  bodyColor = new Color(180, 100, 40);
        else if ("elder".equals(role)) bodyColor = new Color(100, 100, 160);
        else                           bodyColor = new Color(180, 70, 70);
        skinColor = new Color(255, 210, 170);
        buildSprites();
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

        // --- BODY ---
        g.setColor(bodyColor);
        g.fillRoundRect(10, 20, 28, 20, 6, 6);
        // Belt/waist shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRect(10, 36, 28, 4);

        // --- ARMS ---
        g.setColor(bodyColor);
        g.fillRoundRect(4, 22 + legOffset / 2, 8, 12, 4, 4);
        g.fillRoundRect(36, 22 - legOffset / 2, 8, 12, 4, 4);
        
        // Arm shading gradient
        GradientPaint armShade = new GradientPaint(0, 30, new Color(0,0,0,0), 0, 34, new Color(0,0,0,100));
        g.setPaint(armShade);
        g.fillRoundRect(4, 22 + legOffset / 2, 8, 12, 4, 4);
        g.fillRoundRect(36, 22 - legOffset / 2, 8, 12, 4, 4);

        // --- HEAD ---
        g.setColor(skinColor);
        g.fillOval(12, 8, 24, 20);
        // Neck shadow (ambient occlusion)
        g.setColor(new Color(0, 0, 0, 60));
        g.fillArc(12, 8, 24, 20, 180, 180); 

        // --- HAIR ---
        g.setColor(role.equals("elder") ? Color.LIGHT_GRAY : new Color(100, 60, 20));
        g.fillOval(12, 6, 24, 14);

        // --- EYES ---
        if (!dir.equals("up")) {
            g.setColor(Color.DARK_GRAY);
            int eyeOff = dir.equals("left") ? -3 : (dir.equals("right") ? 3 : 0);
            g.fillOval(16 + eyeOff, 17, 3, 4);
            g.fillOval(28 + eyeOff, 17, 3, 4);
        }

        // --- MERCHANT HAT ---
        if (role.equals("merchant")) {
            g.setColor(new Color(80, 50, 20));
            // Back brim
            g.fillRect(10, 5, 28, 5);
            // Top hat portion
            g.fillRoundRect(14, 0, 20, 10, 4, 4);
            // Hat band
            g.setColor(new Color(150, 30, 30));
            g.fillRect(14, 7, 20, 3);
        }

        // --- ELDER STAFF ---
        if (role.equals("elder")) {
            g.setColor(new Color(100, 80, 40));
            g.fillRect(38, 14, 4, 28);
            
            g.setColor(new Color(150, 220, 255));
            g.fillOval(35, 10, 10, 10);
            
            // Magic glow on staff
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
        dialogueIndex = 0;
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
