package ui;

import java.awt.*;

/**
 * Animated health bar widget.
 * The displayed value smoothly lerps toward the real value each frame.
 */
public class HealthBar {

    private float displayed;   // smoothed display value
    private int   max;

    public HealthBar(int maxLife) {
        this.max       = maxLife;
        this.displayed = maxLife;
    }

    /** Call every frame with the current real life value. */
    public void update(int currentLife, int maxLife) {
        this.max = maxLife;
        float target = currentLife;
        // Lerp: chase target value
        float delta = target - displayed;
        displayed += delta / UIConstants.ANIM_LERP_SPEED;
        if (Math.abs(displayed - target) < 0.5f) displayed = target;
    }

    /**
     * Draw the HP bar at the given position.
     * @param w bar width in pixels
     * @param h bar height in pixels
     */
    public void draw(Graphics2D g2, int x, int y, int w, int h) {
        float ratio = Math.max(0, displayed / max);
        // Colour transitions: high=red, low=amber
        Color fillA = ratio > 0.35f ? UIConstants.COL_HP_HIGH : UIConstants.COL_HP_LOW;
        Color fillB = ratio > 0.35f ? new Color(130, 20, 20)  : new Color(140, 65, 10);
        UIPanel.drawBar(g2, x, y, w, h, ratio, UIConstants.COL_HP_BG, fillA, fillB);
    }

    /** Draw with a numeric "current / max" label. */
    public void drawWithLabel(Graphics2D g2, int x, int y, int w, int h,
                               int currentLife) {
        draw(g2, x, y, w, h);
        String label = currentLife + " / " + max;
        g2.setFont(UIFonts.SMALL_B);
        FontMetrics fm = g2.getFontMetrics();
        int lx = x + w / 2 - fm.stringWidth(label) / 2;
        int ly = y + h / 2 + fm.getAscent() / 2 - 1;
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawString(label, lx + 1, ly + 1);
        g2.setColor(Color.WHITE);
        g2.drawString(label, lx, ly);
    }

    public float getDisplayed() { return displayed; }
}
