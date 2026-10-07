package ui;

import core.GamePanel;
import entity.enemy.Enemy;
import java.awt.*;

/**
 * Boss HP bar drawn at the bottom of the screen.
 * Features: pulsing glow, phase-warning markers at 50% and 25%.
 */
public class BossHUD {

    private final GamePanel gp;
    private HealthBar bossBar;
    private Enemy     activeBoss;
    private int       pulseTimer = 0;

    public BossHUD(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        activeBoss = findActiveBoss();
        if (activeBoss != null) {
            if (bossBar == null) bossBar = new HealthBar(activeBoss.maxLife);
            bossBar.update(activeBoss.life, activeBoss.maxLife);
            pulseTimer++;
        } else {
            bossBar = null;
        }
    }

    public void draw(Graphics2D g2) {
        if (activeBoss == null || bossBar == null) return;

        int bw = gp.screenWidth - 80;
        int bh = 20;
        int bx = gp.screenWidth / 2 - bw / 2;
        int by = gp.screenHeight - bh - 55;
        int panelH = 52;

        // Panel
        UIPanel.drawPanel(g2, bx - 12, by - 28, bw + 24, panelH,
                           new Color(10, 5, 20, 240), UIConstants.COL_PANEL_BORDER);

        // Boss name with pulsing glow
        float pulse = 0.7f + 0.3f * (float) Math.sin(pulseTimer * 0.08);
        g2.setFont(UIFonts.BOSS);
        Color nameCol = new Color(
                (int)(UIConstants.COL_GOLD.getRed()   * pulse),
                (int)(UIConstants.COL_GOLD.getGreen() * pulse),
                (int)(UIConstants.COL_GOLD.getBlue()  * 0.3f));
        UIPanel.drawShadowText(g2, activeBoss.name, UIFonts.BOSS, UIConstants.COL_GOLD,
                                bx, by - 8);

        // HP bar
        float ratio = (float) activeBoss.life / activeBoss.maxLife;
        UIPanel.drawBar(g2, bx, by, bw, bh, ratio,
                         UIConstants.COL_HP_BG,
                         UIConstants.COL_BOSS_HP1, UIConstants.COL_BOSS_HP2);

        // Phase markers
        drawPhaseMarker(g2, bx, by, bw, bh, 0.50f);
        drawPhaseMarker(g2, bx, by, bw, bh, 0.25f);

        // Border
        g2.setStroke(UIConstants.STROKE_BORDER);
        g2.setColor(UIConstants.COL_PANEL_BORDER);
        g2.drawRoundRect(bx, by, bw, bh, bh / 2, bh / 2);
        g2.setStroke(UIConstants.STROKE_THIN);

        // HP fraction text
        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(Color.WHITE);
        String frac = activeBoss.life + " / " + activeBoss.maxLife;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(frac, bx + bw - fm.stringWidth(frac) - 4, by + bh - 4);
    }

    private void drawPhaseMarker(Graphics2D g2, int bx, int by, int bw, int bh, float pct) {
        int mx = bx + (int)(bw * pct);
        g2.setColor(UIConstants.COL_BOSS_PHASE);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(mx, by, mx, by + bh);
        g2.setStroke(UIConstants.STROKE_THIN);
    }

    private Enemy findActiveBoss() {
        if (gp.enemies == null) return null;
        for (Enemy e : gp.enemies) {
            if (e != null && e.isBoss && e.alive
                    && e.aiState != Enemy.State.PATROL) return e;
        }
        return null;
    }
}
