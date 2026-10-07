package ui;

import java.awt.*;

/**
 * Reusable rendering primitives: glass panel, progress bar, shadow text,
 * decorative border, heart icon.
 * All methods are stateless static helpers.
 */
public final class UIPanel {

    private UIPanel() {}

    // ── Glass Panel ───────────────────────────────────────────────────────────
    /** Standard deep-navy glass panel with golden border. */
    public static void drawPanel(Graphics2D g2, int x, int y, int w, int h) {
        drawPanel(g2, x, y, w, h, UIConstants.COL_PANEL_BG, UIConstants.COL_PANEL_BORDER);
    }

    /** Glass panel with custom border colour. */
    public static void drawPanel(Graphics2D g2, int x, int y, int w, int h,
                                  Color bg, Color border) {
        int r = UIConstants.CORNER_RADIUS;
        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(x + 4, y + 4, w, h, r, r);
        // Fill
        g2.setColor(bg);
        g2.fillRoundRect(x, y, w, h, r, r);
        // Inner glass highlight
        g2.setColor(UIConstants.COL_PANEL_HIGHLIGHT);
        g2.fillRoundRect(x + 2, y + 2, w - 4, h / 3, r - 2, r - 2);
        // Border
        g2.setStroke(UIConstants.STROKE_BORDER);
        g2.setColor(border);
        g2.drawRoundRect(x, y, w, h, r, r);
        g2.setStroke(UIConstants.STROKE_THIN);
    }

    /** Dimmed panel variant (for overlays). */
    public static void drawOverlay(Graphics2D g2, int screenW, int screenH, Color col) {
        g2.setColor(col);
        g2.fillRect(0, 0, screenW, screenH);
    }

    // ── Progress Bar ─────────────────────────────────────────────────────────
    /**
     * Draws a rounded progress bar.
     * @param ratio 0.0–1.0 fill fraction
     */
    public static void drawBar(Graphics2D g2,
                                int x, int y, int w, int h,
                                float ratio,
                                Color bgCol, Color fillColA, Color fillColB) {
        int r = h / 2;
        // Background
        g2.setColor(bgCol);
        g2.fillRoundRect(x, y, w, h, r, r);
        // Fill gradient
        if (ratio > 0f) {
            int fw = Math.max(r, (int)(w * Math.min(ratio, 1f)));
            GradientPaint gp = new GradientPaint(x, y, fillColA, x, y + h, fillColB);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, fw, h, r, r);
            // Gloss highlight
            g2.setColor(new Color(255, 255, 255, 35));
            g2.fillRoundRect(x, y, fw, h / 2, r, r);
        }
        g2.setPaint(null);
    }

    // ── Shadow Text ───────────────────────────────────────────────────────────
    public static void drawShadowText(Graphics2D g2, String text, Font font,
                                       Color textCol, int x, int y) {
        g2.setFont(font);
        g2.setColor(new Color(0, 0, 0, 160));
        g2.drawString(text, x + 2, y + 2);
        g2.setColor(textCol);
        g2.drawString(text, x, y);
    }

    /** Centre-aligned shadow text. */
    public static void drawShadowTextCentered(Graphics2D g2, String text, Font font,
                                               Color textCol, int centreX, int y) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int x = centreX - fm.stringWidth(text) / 2;
        drawShadowText(g2, text, font, textCol, x, y);
    }

    // ── Heart Icon ────────────────────────────────────────────────────────────
    public static void drawHeart(Graphics2D g2, int x, int y, int size, boolean filled) {
        Color fill   = filled ? UIConstants.COL_HP_HIGH    : new Color(55, 25, 25, 170);
        Color border = filled ? new Color(255, 140, 140)   : new Color( 90, 50, 50, 130);
        int r = size / 3;
        g2.setColor(fill);
        g2.fillOval(x,     y, r * 2, r * 2);
        g2.fillOval(x + r, y, r * 2, r * 2);
        int[] hx = {x,      x + size,  x + size / 2};
        int[] hy = {y + r,  y + r,     y + size};
        g2.fillPolygon(hx, hy, 3);
        if (filled) {
            g2.setColor(new Color(255, 185, 185, 160));
            g2.fillOval(x + 2, y + 2, r - 2, r - 3);
        }
        g2.setStroke(UIConstants.STROKE_THIN);
        g2.setColor(border);
        g2.drawOval(x,     y, r * 2, r * 2);
        g2.drawOval(x + r, y, r * 2, r * 2);
        g2.drawPolygon(hx, hy, 3);
    }

    // ── Decorative separator ──────────────────────────────────────────────────
    public static void drawSeparator(Graphics2D g2, int x, int y, int w) {
        GradientPaint gp = new GradientPaint(x, y, new Color(180,150,60,0),
                                              x + w/2, y, new Color(180,150,60,180));
        g2.setPaint(gp);
        g2.fillRect(x, y, w / 2, 1);
        gp = new GradientPaint(x + w/2, y, new Color(180,150,60,180),
                                x + w,  y, new Color(180,150,60,0));
        g2.setPaint(gp);
        g2.fillRect(x + w/2, y, w / 2, 1);
        g2.setPaint(null);
    }

    // ── Triangle helper ───────────────────────────────────────────────────────
    public static void fillTriangle(Graphics2D g2, int x1, int y1,
                                     int x2, int y2, int x3, int y3) {
        g2.fillPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
    }
}
