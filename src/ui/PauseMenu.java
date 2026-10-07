package ui;

import core.GamePanel;
import java.awt.*;

/** Pause menu with blurred overlay and navigation options. */
public class PauseMenu {

    private final GamePanel gp;
    private int timer = 0;

    public PauseMenu(GamePanel gp) {
        this.gp = gp;
    }

    public void update() { timer++; }

    public void draw(Graphics2D g2) {
        // Soft dark overlay
        UIPanel.drawOverlay(g2, gp.screenWidth, gp.screenHeight,
                             new Color(0, 0, 0, 150));

        int pw = 310, ph = 230;
        int px = gp.screenWidth / 2 - pw / 2;
        int py = gp.screenHeight / 2 - ph / 2;
        UIPanel.drawPanel(g2, px, py, pw, ph);

        // Header
        UIPanel.drawShadowTextCentered(g2, "\u23F8  PAUSED", UIFonts.HEADING,
                                        UIConstants.COL_GOLD, px + pw / 2, py + 36);
        UIPanel.drawSeparator(g2, px + 16, py + 40, pw - 32);

        // Menu items
        int ix = px + 40;
        int iy = py + 68;
        int gap = 30;
        drawMenuItem(g2, ix, iy,       "ESC",   "Resume",            UIConstants.COL_TEXT_MAIN);
        drawMenuItem(g2, ix, iy + gap, "I",     "Inventory",         UIConstants.COL_SILVER);
        drawMenuItem(g2, ix, iy + gap*2,"Q",    "Quest Log",         UIConstants.COL_SILVER);
        drawMenuItem(g2, ix, iy + gap*3,"F5",   "Save Game",         UIConstants.COL_TEXT_GOOD);
        drawMenuItem(g2, ix, iy + gap*4,"ENTER","Use Potion (" +
                gp.player.potionCount + ")", new Color(160, 220, 180));

        // Animated hint bar at bottom
        if ((timer / 28) % 2 == 0) {
            g2.setFont(UIFonts.SMALL);
            g2.setColor(UIConstants.COL_TEXT_DIM);
            String hint = "Press ESC to resume";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(hint, px + pw / 2 - fm.stringWidth(hint) / 2, py + ph - 10);
        }
    }

    private void drawMenuItem(Graphics2D g2, int x, int y,
                               String key, String label, Color labelCol) {
        // Key badge
        int kw = 32, kh = 18;
        g2.setColor(new Color(30, 28, 60, 220));
        g2.fillRoundRect(x, y - 13, kw, kh, 6, 6);
        g2.setStroke(UIConstants.STROKE_THIN);
        g2.setColor(UIConstants.COL_PANEL_BORDER_DIM);
        g2.drawRoundRect(x, y - 13, kw, kh, 6, 6);
        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(UIConstants.COL_GOLD);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(key, x + kw / 2 - fm.stringWidth(key) / 2, y - 1);

        // Label
        g2.setFont(UIFonts.BODY);
        g2.setColor(labelCol);
        g2.drawString(label, x + kw + 10, y);
    }
}
