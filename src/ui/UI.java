package ui;

import core.GamePanel;
import core.GameState;
import entity.Player;
import quest.Quest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class UI {
    private GamePanel gp;
    private Font titleFont    = new Font("Georgia", Font.BOLD, 48);
    private Font subtitleFont = new Font("Georgia", Font.BOLD | Font.ITALIC, 22);
    private Font hudFont      = new Font("Arial", Font.BOLD, 13);
    private Font smallFont    = new Font("Arial", Font.PLAIN, 11);

    // Title screen animation
    private float titleAlpha = 0;
    private int titleTimer = 0;

    // Notification system
    private String notification = "";
    private int notifTimer = 0;
    private static final int NOTIF_DURATION = 180;

    public UI(GamePanel gp) {
        this.gp = gp;
    }

    public void showNotification(String msg) {
        notification = msg;
        notifTimer = NOTIF_DURATION;
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (gp.gameState) {
            case TITLE     -> drawTitle(g2);
            case PLAY      -> { drawHUD(g2); drawNotification(g2); }
            case PAUSE     -> { drawHUD(g2); drawPause(g2); }
            case DIALOGUE  -> { drawHUD(g2); drawDialogue(g2); }
            case INVENTORY -> { drawHUD(g2); drawInventory(g2); }
            case QUEST_LOG -> { drawHUD(g2); drawQuestLog(g2); }
            case GAME_OVER -> drawGameOver(g2);
            case WIN       -> drawWin(g2);
        }
    }

    private void drawTitle(Graphics2D g2) {
        // Sky gradient background
        GradientPaint sky = new GradientPaint(0, 0, new Color(10, 15, 40), 0, gp.screenHeight, new Color(40, 60, 120));
        g2.setPaint(sky);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Stars
        g2.setColor(Color.WHITE);
        long seed = 42;
        for (int i = 0; i < 60; i++) {
            seed = (seed * 1664525L + 1013904223L) & 0xFFFFFFFFL;
            int sx = (int)(seed % gp.screenWidth);
            seed = (seed * 1664525L + 1013904223L) & 0xFFFFFFFFL;
            int sy = (int)(seed % (gp.screenHeight / 2));
            float alpha = 0.4f + ((i % 5) * 0.12f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fillOval(sx, sy, 3, 3);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Moon
        g2.setColor(new Color(255, 250, 230));
        g2.fillOval(gp.screenWidth - 140, 30, 80, 80);
        g2.setColor(new Color(10, 15, 40));
        g2.fillOval(gp.screenWidth - 125, 25, 80, 80);

        // Ground silhouette
        GradientPaint ground = new GradientPaint(0, gp.screenHeight - 100, new Color(20, 50, 20),
            0, gp.screenHeight, new Color(10, 25, 10));
        g2.setPaint(ground);
        g2.fillRect(0, gp.screenHeight - 100, gp.screenWidth, 100);
        // Tree silhouettes
        g2.setColor(new Color(10, 30, 10));
        int[] treeX = {40, 100, 200, 560, 650, 720};
        for (int tx : treeX) {
            fillTriangle(g2, tx, gp.screenHeight - 100, tx + 30, gp.screenHeight, tx - 30, gp.screenHeight);
            fillTriangle(g2, tx + 5, gp.screenHeight - 130, tx + 25, gp.screenHeight - 90, tx - 15, gp.screenHeight - 90);
        }

        // Title fade in
        titleTimer++;
        titleAlpha = Math.min(1.0f, titleTimer / 120f);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));

        // Title shadow
        g2.setFont(titleFont);
        String title = "CHRONICLES OF THE";
        String title2 = "LOST KINGDOM";
        FontMetrics fm = g2.getFontMetrics();
        int tx = gp.screenWidth / 2 - fm.stringWidth(title) / 2;
        int tx2 = gp.screenWidth / 2 - fm.stringWidth(title2) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(title, tx + 3, gp.screenHeight / 2 - 55);
        g2.drawString(title2, tx2 + 3, gp.screenHeight / 2 - 3);

        // Title gradient text
        GradientPaint titlePaint = new GradientPaint(0, gp.screenHeight/2 - 80, new Color(255, 220, 80),
            0, gp.screenHeight/2, new Color(200, 120, 30));
        g2.setPaint(titlePaint);
        g2.drawString(title, tx, gp.screenHeight / 2 - 58);
        g2.drawString(title2, tx2, gp.screenHeight / 2 - 6);

        // Subtitle
        g2.setFont(subtitleFont);
        g2.setColor(new Color(180, 220, 255, 200));
        String sub = "An Open World RPG Adventure";
        fm = g2.getFontMetrics();
        g2.drawString(sub, gp.screenWidth / 2 - fm.stringWidth(sub) / 2, gp.screenHeight / 2 + 30);

        // Press Enter prompt (flashing)
        if ((titleTimer / 30) % 2 == 0) {
            g2.setFont(hudFont);
            g2.setColor(Color.WHITE);
            String prompt = "▶  Press ENTER to Begin  ◀";
            fm = g2.getFontMetrics();
            g2.drawString(prompt, gp.screenWidth / 2 - fm.stringWidth(prompt) / 2, gp.screenHeight / 2 + 80);
        }

        // Controls hint
        g2.setFont(smallFont);
        g2.setColor(new Color(180, 180, 180, 180));
        String controls = "ARROWS/WASD: Move  |  Z: Sword  |  X: Shield  |  V: Fire  |  E: Interact  |  I: Inv  |  Q: Quests";
        fm = g2.getFontMetrics();
        g2.drawString(controls, gp.screenWidth / 2 - fm.stringWidth(controls) / 2, gp.screenHeight - 20);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawHUD(Graphics2D g2) {
        Player p = gp.player;

        // === Left: Hearts / HP ===
        int heartSize = 22;
        int startX = 12, startY = 12;
        int maxHearts = p.maxLife / 10;
        int curHearts = (int)Math.ceil((double)p.life / 10);

        for (int i = 0; i < maxHearts; i++) {
            int hx = startX + i * (heartSize + 2);
            boolean filled = i < curHearts;
            drawHeart(g2, hx, startY, heartSize, filled);
        }

        // HP text
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.setColor(new Color(0,0,0,120));
        g2.fillRoundRect(startX, startY + heartSize + 2, 90, 16, 6, 6);
        g2.setColor(new Color(255, 180, 180));
        g2.drawString("HP: " + p.life + "/" + p.maxLife, startX + 4, startY + heartSize + 14);

        // === Right: Stats panel ===
        int panelX = gp.screenWidth - 160;
        int panelY = 8;
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(panelX, panelY, 150, 80, 12, 12);
        g2.setColor(new Color(180, 150, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(panelX, panelY, 150, 80, 12, 12);

        g2.setFont(hudFont);
        int lx = panelX + 8;
        g2.setColor(new Color(255, 220, 80));
        g2.drawString("⚔ Lvl " + p.level + "  XP: " + p.xp + "/" + p.xpToLevel, lx, panelY + 18);
        g2.setColor(new Color(255, 200, 50));
        g2.drawString("💰 Gold: " + p.gold, lx, panelY + 34);
        g2.setColor(new Color(255, 100, 100));
        g2.drawString("⚔ ATK: " + p.attackDamage, lx, panelY + 50);
        g2.setColor(new Color(100, 180, 255));
        g2.drawString("🛡 DEF: " + p.defense, lx + 70, panelY + 50);
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("🧪 Potions: " + p.potionCount, lx, panelY + 66);

        // XP Bar
        int xpBarX = panelX + 5, xpBarY = panelY + 72;
        int xpBarW = 140;
        g2.setColor(new Color(50, 50, 50));
        g2.fillRoundRect(xpBarX, xpBarY, xpBarW, 6, 3, 3);
        float xpRatio = (float)p.xp / p.xpToLevel;
        g2.setColor(new Color(100, 220, 100));
        g2.fillRoundRect(xpBarX, xpBarY, (int)(xpBarW * xpRatio), 6, 3, 3);

        // === Zone name ===
        g2.setFont(new Font("Georgia", Font.ITALIC, 13));
        g2.setColor(new Color(0,0,0,120));
        String zone = p.currentZone;
        FontMetrics fm = g2.getFontMetrics();
        int zx = gp.screenWidth / 2 - fm.stringWidth(zone) / 2;
        g2.fillRoundRect(zx - 6, gp.screenHeight - 26, fm.stringWidth(zone) + 12, 18, 8, 8);
        g2.setColor(new Color(220, 220, 255));
        g2.drawString(zone, zx, gp.screenHeight - 12);

        // === Quest tracker (top center-left) ===
        var activeQuests = gp.questManager.getActiveQuests();
        if (!activeQuests.isEmpty()) {
            int qtX = gp.screenWidth / 2 - 120, qtY = 10;
            int qtW = 240, qtH = 14 + activeQuests.size() * 28;
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRoundRect(qtX, qtY, qtW, qtH, 10, 10);
            g2.setColor(new Color(255, 200, 60));
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString("★ ACTIVE QUESTS", qtX + 8, qtY + 12);

            int qy = qtY + 26;
            for (Quest q : activeQuests) {
                g2.setColor(new Color(220, 220, 255));
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.drawString("• " + q.title, qtX + 8, qy);
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.setColor(new Color(160, 200, 160));
                g2.drawString("  " + q.getProgress(), qtX + 8, qy + 12);
                qy += 28;
            }
        }

        // === Minimap ===
        drawMinimap(g2);

        g2.setStroke(new BasicStroke(1));
    }

    private void drawHeart(Graphics2D g2, int x, int y, int size, boolean filled) {
        Color fillColor = filled ? new Color(220, 50, 50) : new Color(60, 30, 30, 160);
        Color border    = filled ? new Color(255, 150, 150) : new Color(100, 60, 60, 120);
        g2.setColor(fillColor);
        // Heart shape using two circles + triangle
        int r = size / 3;
        g2.fillOval(x, y, r * 2, r * 2);
        g2.fillOval(x + r, y, r * 2, r * 2);
        int[] hx = {x, x + size, x + size/2};
        int[] hy = {y + r, y + r, y + size};
        g2.fillPolygon(hx, hy, 3);
        // Shine
        if (filled) {
            g2.setColor(new Color(255, 180, 180, 180));
            g2.fillOval(x + 2, y + 2, r - 2, r - 3);
        }
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(x, y, r * 2, r * 2);
        g2.drawOval(x + r, y, r * 2, r * 2);
        g2.drawPolygon(hx, hy, 3);
    }

    private void drawMinimap(Graphics2D g2) {
        int mmW = 120, mmH = 90;
        int mmX = gp.screenWidth - mmW - 10;
        int mmY = gp.screenHeight - mmH - 10;

        // Background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(mmX - 2, mmY - 2, mmW + 4, mmH + 4, 8, 8);
        g2.setColor(new Color(100, 80, 40));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(mmX - 2, mmY - 2, mmW + 4, mmH + 4, 8, 8);

        // Draw simplified world
        float scaleX = (float)mmW / (gp.maxWorldCol * gp.tileSize);
        float scaleY = (float)mmH / (gp.maxWorldRow * gp.tileSize);
        // Zone colors
        g2.setColor(new Color(60, 120, 50));
        g2.fillRect(mmX, mmY, mmW, mmH);
        // Water
        g2.setColor(new Color(30, 80, 180, 200));
        drawMMRect(g2, mmX, mmY, scaleX, scaleY, 22, 4, 10, 8);
        // Dark forest
        g2.setColor(new Color(20, 60, 20));
        drawMMRect(g2, mmX, mmY, scaleX, scaleY, 33, 1, gp.maxWorldCol - 34, 24);
        // Caves
        g2.setColor(new Color(50, 40, 70));
        drawMMRect(g2, mmX, mmY, scaleX, scaleY, 2, 30, 18, gp.maxWorldRow - 31);
        // Ruins
        g2.setColor(new Color(80, 70, 50));
        drawMMRect(g2, mmX, mmY, scaleX, scaleY, 30, 28, gp.maxWorldCol - 31, gp.maxWorldRow - 29);

        // Player dot
        int pdx = mmX + (int)(gp.player.worldX * scaleX);
        int pdy = mmY + (int)(gp.player.worldY * scaleY);
        g2.setColor(Color.WHITE);
        g2.fillOval(pdx - 3, pdy - 3, 7, 7);
        g2.setColor(new Color(100, 200, 255));
        g2.fillOval(pdx - 2, pdy - 2, 5, 5);

        // Label
        g2.setFont(new Font("Arial", Font.BOLD, 8));
        g2.setColor(new Color(200, 190, 150));
        g2.drawString("MAP", mmX + 2, mmY + 9);

        g2.setStroke(new BasicStroke(1));
    }

    private void drawMMRect(Graphics2D g2, int mmX, int mmY, float sx, float sy, int col, int row, int cols, int rows) {
        int x = mmX + (int)(col * gp.tileSize * sx);
        int y = mmY + (int)(row * gp.tileSize * sy);
        int w = (int)(cols * gp.tileSize * sx);
        int h = (int)(rows * gp.tileSize * sy);
        g2.fillRect(x, y, w, h);
    }

    private void drawPause(Graphics2D g2) {
        // Dark overlay
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        // Panel
        int pw = 300, ph = 200;
        int px = gp.screenWidth / 2 - pw / 2, py = gp.screenHeight / 2 - ph / 2;
        drawPanel(g2, px, py, pw, ph);
        g2.setFont(new Font("Georgia", Font.BOLD, 28));
        g2.setColor(new Color(255, 220, 80));
        g2.drawString("⏸  PAUSED", px + 60, py + 50);
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("ESC  — Resume", px + 80, py + 90);
        g2.drawString("I    — Inventory", px + 80, py + 112);
        g2.drawString("Q    — Quest Log", px + 80, py + 134);
        g2.drawString("ENTER — Use Potion (" + gp.player.potionCount + ")", px + 80, py + 156);
    }

    private void drawDialogue(Graphics2D g2) {
        if (gp.currentNPC == null) return;
        int boxH = 120;
        int boxY = gp.screenHeight - boxH - 20;
        drawPanel(g2, 20, boxY, gp.screenWidth - 40, boxH);
        // NPC name
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(new Color(255, 220, 80));
        g2.drawString(gp.currentNPC.name, 36, boxY + 20);
        // Dialogue text  (word wrap at 60 chars)
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.setColor(Color.WHITE);
        String text = gp.currentNPC.getCurrentDialogue();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int ty = boxY + 44;
        for (String w : words) {
            if ((line + w).length() > 62) {
                g2.drawString(line.toString().trim(), 36, ty);
                line = new StringBuilder();
                ty += 18;
            }
            line.append(w).append(" ");
        }
        g2.drawString(line.toString().trim(), 36, ty);
        // Prompt
        if ((titleTimer / 20) % 2 == 0) {
            g2.setColor(new Color(180, 180, 255));
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString("▼ Press E to continue", gp.screenWidth - 200, boxY + boxH - 12);
        }
    }

    private void drawInventory(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        int pw = 420, ph = 320;
        int px = gp.screenWidth / 2 - pw / 2, py = gp.screenHeight / 2 - ph / 2;
        drawPanel(g2, px, py, pw, ph);

        g2.setFont(new Font("Georgia", Font.BOLD, 22));
        g2.setColor(new Color(255, 220, 80));
        g2.drawString("⚔  INVENTORY", px + 130, py + 30);

        Player p = gp.player;
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Gold: " + p.gold, px + 20, py + 56);
        g2.drawString("Level: " + p.level + "  XP: " + p.xp + "/" + p.xpToLevel, px + 120, py + 56);

        // Equipment
        g2.setColor(new Color(180, 180, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("Equipment:", px + 20, py + 80);
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Weapon:  " + (p.hasWeapon ? "Iron Sword (ATK +" + p.attackDamage + ")" : "Fists"), px + 20, py + 98);
        g2.drawString("Shield:  " + (p.hasShield ? "Iron Shield (DEF +" + p.defense + ")" : "None"), px + 20, py + 116);
        g2.drawString("Potions: " + p.potionCount + "  (Press ENTER in game to use)", px + 20, py + 134);

        // Inventory items grid
        g2.setColor(new Color(180, 180, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("Items:", px + 20, py + 158);

        if (p.inventory.isEmpty()) {
            g2.setColor(new Color(120, 120, 180));
            g2.setFont(hudFont);
            g2.drawString("Empty", px + 20, py + 176);
        } else {
            int ix = px + 20, iy = py + 176;
            for (int i = 0; i < p.inventory.size() && i < 18; i++) {
                g2.setColor(new Color(40, 40, 80));
                g2.fillRoundRect(ix, iy - 16, 80, 20, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(smallFont);
                g2.drawString(p.inventory.get(i), ix + 4, iy - 2);
                ix += 85;
                if (ix > px + pw - 90) { ix = px + 20; iy += 26; }
            }
        }

        g2.setFont(smallFont);
        g2.setColor(new Color(160, 160, 200));
        g2.drawString("Press I to close", px + pw / 2 - 40, py + ph - 10);
    }

    private void drawQuestLog(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        int pw = 500, ph = 360;
        int px = gp.screenWidth / 2 - pw / 2, py = gp.screenHeight / 2 - ph / 2;
        drawPanel(g2, px, py, pw, ph);

        g2.setFont(new Font("Georgia", Font.BOLD, 22));
        g2.setColor(new Color(255, 220, 80));
        g2.drawString("★  QUEST LOG", px + 160, py + 30);

        var quests = gp.questManager.getAllQuests();
        int qy = py + 56;
        for (var q : quests) {
            Color c = switch (q.status) {
                case COMPLETED -> new Color(100, 220, 100);
                case ACTIVE    -> new Color(220, 220, 255);
                case INACTIVE  -> new Color(100, 100, 140);
            };
            String prefix = switch (q.status) {
                case COMPLETED -> "✓ ";
                case ACTIVE    -> "→ ";
                case INACTIVE  -> "○ ";
            };
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(c);
            g2.drawString(prefix + q.title, px + 20, qy);
            g2.setFont(smallFont);
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
            g2.drawString(q.description, px + 36, qy + 14);
            if (q.status == quest.Quest.Status.ACTIVE) {
                g2.setColor(new Color(180, 220, 140));
                g2.drawString(q.getProgress(), px + 36, qy + 27);
                qy += 12;
            }
            g2.setColor(new Color(60, 60, 100));
            g2.drawLine(px + 10, qy + 33, px + pw - 10, qy + 33);
            qy += 46;
            if (qy > py + ph - 30) break;
        }

        g2.setFont(smallFont);
        g2.setColor(new Color(160, 160, 200));
        g2.drawString("Press Q to close | Completed: " + gp.questManager.totalCompleted() + "/" + quests.size(),
            px + 150, py + ph - 10);
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setFont(new Font("Georgia", Font.BOLD, 60));
        g2.setColor(new Color(200, 30, 30));
        FontMetrics fm = g2.getFontMetrics();
        String text = "GAME OVER";
        g2.drawString(text, gp.screenWidth / 2 - fm.stringWidth(text) / 2, gp.screenHeight / 2 - 20);
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Press ENTER to return to title", gp.screenWidth / 2 - 90, gp.screenHeight / 2 + 40);
    }

    private void drawWin(Graphics2D g2) {
        GradientPaint gp2 = new GradientPaint(0, 0, new Color(20, 10, 40), 0, gp.screenHeight, new Color(80, 40, 120));
        g2.setPaint(gp2);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setFont(new Font("Georgia", Font.BOLD, 48));
        GradientPaint gold = new GradientPaint(0, gp.screenHeight/2 - 60, new Color(255, 220, 80), 0, gp.screenHeight/2, new Color(200, 120, 30));
        g2.setPaint(gold);
        String text = "VICTORY!";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, gp.screenWidth / 2 - fm.stringWidth(text) / 2, gp.screenHeight / 2 - 20);
        g2.setFont(hudFont);
        g2.setColor(new Color(220, 220, 255));
        g2.drawString("You have conquered the Lost Kingdom!", gp.screenWidth / 2 - 130, gp.screenHeight / 2 + 30);
        g2.drawString("Press ENTER to return to title", gp.screenWidth / 2 - 100, gp.screenHeight / 2 + 60);
    }

    private void drawNotification(Graphics2D g2) {
        if (notifTimer <= 0) return;
        notifTimer--;
        float alpha = Math.min(1.0f, Math.min(notifTimer / 30f, (NOTIF_DURATION - notifTimer) / 30f + 0.1f));
        Composite orig = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int nw = fm.stringWidth(notification) + 20;
        int nx = gp.screenWidth / 2 - nw / 2;
        int ny = gp.screenHeight / 2 - 80;
        g2.setColor(new Color(20, 20, 60, 220));
        g2.fillRoundRect(nx, ny, nw, 26, 12, 12);
        g2.setColor(new Color(255, 220, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(nx, ny, nw, 26, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawString(notification, nx + 10, ny + 18);
        g2.setComposite(orig);
        g2.setStroke(new BasicStroke(1));
    }

    private void drawPanel(Graphics2D g2, int x, int y, int w, int h) {
        // Dark glass panel
        g2.setColor(new Color(10, 10, 30, 220));
        g2.fillRoundRect(x, y, w, h, 16, 16);
        g2.setColor(new Color(80, 70, 140));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, w, h, 16, 16);
        // Inner highlight
        g2.setColor(new Color(255, 255, 255, 15));
        g2.fillRoundRect(x + 2, y + 2, w - 4, h / 3, 14, 14);
        g2.setStroke(new BasicStroke(1));
    }

    // Helper: Graphics2D doesn't have fillTriangle, use polygon
    private void fillTriangle(Graphics2D g2, int x1, int y1, int x2, int y2, int x3, int y3) {
        g2.fillPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
    }

    public void update() {
        titleTimer++;
        // Check game over / win
        Player p = gp.player;
        if (p != null && !p.alive && gp.gameState == GameState.PLAY) {
            gp.gameState = GameState.GAME_OVER;
        }
    }
}
