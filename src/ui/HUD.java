package ui;

import core.GamePanel;
import entity.Player;
import quest.Quest;
import java.awt.*;
import java.util.List;

/** Heads-Up Display: hearts, XP bar, stat panel, zone label, quest tracker. */
public class HUD {

    private final GamePanel gp;
    private final HealthBar hpBar;
    private float xpDisplayed = 0;

    private static final int HEART_SIZE = 22;
    private static final int HEART_GAP  = 3;

    public HUD(GamePanel gp) {
        this.gp    = gp;
        this.hpBar = new HealthBar(gp.player.maxLife);
    }

    public void update() {
        Player p = gp.player;
        hpBar.update(p.life, p.maxLife);
        // Smooth XP bar
        float xpTarget = (float) p.xp / p.xpToLevel;
        xpDisplayed += (xpTarget - xpDisplayed) / UIConstants.ANIM_LERP_SPEED;
    }

    public void draw(Graphics2D g2) {
        Player p = gp.player;

        drawHearts(g2, p);
        drawStatsPanel(g2, p);
        drawZoneLabel(g2, p);
        drawQuestTracker(g2);
    }

    // ── Hearts ─────────────────────────────────────────────────────────────────
    private void drawHearts(Graphics2D g2, Player p) {
        int maxHearts = p.maxLife / 10;
        int curHearts = (int) Math.ceil((double) p.life / 10);
        int sx = 12, sy = 12;

        for (int i = 0; i < maxHearts; i++) {
            UIPanel.drawHeart(g2, sx + i * (HEART_SIZE + HEART_GAP), sy,
                              HEART_SIZE, i < curHearts);
        }

        // Numeric HP
        int barW = maxHearts * (HEART_SIZE + HEART_GAP) - HEART_GAP;
        int barH = 8;
        int barY = sy + HEART_SIZE + 4;
        hpBar.draw(g2, sx, barY, barW, barH);
        // Label
        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(new Color(255, 190, 190));
        g2.drawString("HP  " + p.life + " / " + p.maxLife, sx, barY + barH + 12);
    }

    // ── Stats panel (top-right) ────────────────────────────────────────────────
    private void drawStatsPanel(Graphics2D g2, Player p) {
        int pw = 168, ph = 106;
        int px = gp.screenWidth - pw - 8, py = 8;
        UIPanel.drawPanel(g2, px, py, pw, ph);

        int lx = px + 10;
        // Row 1: level + XP
        g2.setFont(UIFonts.HUD);
        g2.setColor(UIConstants.COL_GOLD);
        g2.drawString("\u2694 Lv " + p.level, lx, py + 18);
        g2.setColor(UIConstants.COL_TEXT_DIM);
        g2.drawString("XP " + p.xp + "/" + p.xpToLevel, lx + 52, py + 18);

        // XP bar
        UIPanel.drawBar(g2, lx, py + 22, pw - 20, 5, xpDisplayed,
                         UIConstants.COL_XP_BG, UIConstants.COL_XP_FILL,
                         new Color(30, 160, 80));

        // Row 2: Gold / Potion
        // Gold coin icon + count
        g2.setColor(UIConstants.COL_GOLD);
        g2.fillOval(lx, py + 29, 12, 12);
        g2.setColor(new Color(180, 140, 20));
        g2.drawOval(lx, py + 29, 12, 12);
        g2.setColor(Color.WHITE);
        g2.fillOval(lx + 3, py + 32, 3, 3);

        g2.setFont(UIFonts.HUD);
        g2.setColor(UIConstants.COL_GOLD);
        g2.drawString("Gold: " + p.gold, lx + 16, py + 40);

        // Potion icon + count
        g2.setColor(new Color(220, 60, 60));
        g2.fillRoundRect(lx + 92, py + 29, 11, 12, 3, 3);
        g2.setColor(Color.WHITE);
        g2.drawString("+", lx + 94, py + 39);

        g2.setColor(new Color(160, 220, 180));
        g2.drawString("x" + p.potionCount, lx + 108, py + 40);

        // Row 3: ATK / DEF
        g2.setColor(UIConstants.COL_TEXT_BAD);
        g2.drawString("\u2694 " + p.attackDamage, lx, py + 58);
        g2.setColor(new Color(100, 180, 255));
        g2.drawString("\uD83D\uDEE1 " + p.defense, lx + 70, py + 58);

        // Row 4: equipment flags
        g2.setFont(UIFonts.SMALL);
        g2.setColor(p.hasWeapon ? UIConstants.COL_TEXT_GOOD : UIConstants.COL_TEXT_DIM);
        g2.drawString(p.hasWeapon ? "Sword \u2713" : "Sword \u2715", lx, py + 74);
        g2.setColor(p.hasShield ? UIConstants.COL_TEXT_GOOD : UIConstants.COL_TEXT_DIM);
        g2.drawString(p.hasShield ? "Shield \u2713" : "Shield \u2715", lx + 72, py + 74);

        // Row 5: Credit Score / Tier
        String tier = p.getCreditTier();
        Color tierColor = getTierColor(tier);
        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(new Color(180, 140, 220));
        g2.drawString("\u2605", lx, py + 96);
        g2.setColor(tierColor);
        g2.drawString(tier + " Credit", lx + 12, py + 96);
        g2.setColor(UIConstants.COL_TEXT_DIM);
        g2.setFont(UIFonts.SMALL);
        g2.drawString("" + p.creditScore, lx + 78, py + 96);
        int disc = p.getCreditDiscountPercent();
        if (disc > 0) {
            g2.setColor(UIConstants.COL_TEXT_GOOD);
            g2.drawString("-" + disc + "%", lx + 115, py + 96);
        }
    }

    private Color getTierColor(String tier) {
        switch (tier) {
            case "Platinum": return new Color(200, 220, 255);
            case "Gold":     return new Color(255, 215, 80);
            case "Silver":   return new Color(200, 200, 210);
            case "Bronze":   return new Color(205, 127, 50);
            default:         return UIConstants.COL_TEXT_DIM;
        }
    }

    // ── Zone label (bottom centre) ─────────────────────────────────────────────
    private void drawZoneLabel(Graphics2D g2, Player p) {
        g2.setFont(UIFonts.ZONE);
        FontMetrics fm = g2.getFontMetrics();
        String zone = p.currentZone;
        int zw = fm.stringWidth(zone) + 16;
        int zx = gp.screenWidth / 2 - zw / 2;
        int zy = gp.screenHeight - 24;

        g2.setColor(new Color(0, 0, 0, 130));
        g2.fillRoundRect(zx, zy - 14, zw, 20, 10, 10);
        g2.setColor(new Color(210, 215, 255));
        g2.drawString(zone, zx + 8, zy);
    }

    // ── Active quest tracker (top-centre) ──────────────────────────────────────
    private void drawQuestTracker(Graphics2D g2) {
        List<Quest> active = gp.questManager.getActiveQuests();
        if (active.isEmpty()) return;

        int qtW = 230;
        int qtH = 18 + active.size() * 30;
        int qtX = gp.screenWidth / 2 - qtW / 2;
        int qtY = 10;

        UIPanel.drawPanel(g2, qtX, qtY, qtW, qtH,
                           new Color(5, 5, 20, 200), UIConstants.COL_PANEL_BORDER_DIM);

        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(UIConstants.COL_GOLD);
        g2.drawString("\u2605 ACTIVE QUESTS", qtX + 10, qtY + 13);

        int qy = qtY + 28;
        for (Quest q : active) {
            g2.setFont(UIFonts.SMALL_B);
            g2.setColor(UIConstants.COL_TEXT_MAIN);
            g2.drawString("\u2022 " + q.title, qtX + 10, qy);
            g2.setFont(UIFonts.SMALL);
            g2.setColor(UIConstants.COL_TEXT_GOOD);
            g2.drawString("  " + q.getProgress(), qtX + 10, qy + 13);
            qy += 30;
        }
    }
}
