package ui.hud;

import core.GamePanel;
import entity.Player;
import java.awt.*;

public class HUDRenderer {

    private HeartRenderer heartRenderer = new HeartRenderer();
    private MinimapRenderer minimapRenderer = new MinimapRenderer();
    private BossBarRenderer bossBarRenderer = new BossBarRenderer();
    private QuestTrackerRenderer questTrackerRenderer = new QuestTrackerRenderer();
    
    private Font hudFont = new Font("Arial", Font.BOLD, 13);

    public void draw(Graphics2D g2, GamePanel gp) {
        Player p = gp.player;

        // === Left: Hearts / HP ===
        int heartSize = 22;
        int startX = 12, startY = 12;
        int maxHearts = p.maxLife / 10;
        int curHearts = (int)Math.ceil((double)p.life / 10);

        for (int i = 0; i < maxHearts; i++) {
            int hx = startX + i * (heartSize + 2);
            boolean filled = i < curHearts;
            heartRenderer.drawHeart(g2, hx, startY, heartSize, filled);
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
        questTrackerRenderer.draw(g2, gp);

        // === Minimap ===
        minimapRenderer.draw(g2, gp);

        // === Boss Health Bar (Bottom Center) ===
        bossBarRenderer.draw(g2, gp);

        g2.setStroke(new BasicStroke(1));
    }
}
