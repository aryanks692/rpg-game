package ui;

import java.awt.Color;
import java.awt.BasicStroke;

/** Central registry of all UI design tokens. */
public final class UIConstants {

    private UIConstants() {}

    // ── Palette ──────────────────────────────────────────────────────────────
    public static final Color COL_PANEL_BG        = new Color(8,  10,  28,  230);
    public static final Color COL_PANEL_BORDER     = new Color(180, 150,  60, 255);
    public static final Color COL_PANEL_BORDER_DIM = new Color( 80,  70, 140, 200);
    public static final Color COL_PANEL_HIGHLIGHT  = new Color(255, 255, 255,  18);

    public static final Color COL_GOLD         = new Color(255, 215,  60);
    public static final Color COL_GOLD_DARK    = new Color(180, 110,  20);
    public static final Color COL_SILVER       = new Color(200, 210, 225);
    public static final Color COL_TEXT_MAIN    = new Color(240, 240, 255);
    public static final Color COL_TEXT_DIM     = new Color(150, 150, 190);
    public static final Color COL_TEXT_GOOD    = new Color( 90, 220, 100);
    public static final Color COL_TEXT_WARN    = new Color(255, 160,  40);
    public static final Color COL_TEXT_BAD     = new Color(230,  60,  60);

    public static final Color COL_HP_HIGH      = new Color(210,  45,  45);
    public static final Color COL_HP_LOW       = new Color(200, 100,  20);
    public static final Color COL_HP_BG        = new Color( 40,  18,  18);
    public static final Color COL_XP_FILL      = new Color( 60, 200, 120);
    public static final Color COL_XP_BG        = new Color( 20,  40,  25);

    public static final Color COL_BOSS_HP1     = new Color(200,  30,  30);
    public static final Color COL_BOSS_HP2     = new Color(120,  10,  10);
    public static final Color COL_BOSS_PHASE   = new Color(255, 180,   0, 180);

    public static final Color COL_NOTIF_BG     = new Color( 14,  12,  40, 235);
    public static final Color COL_NOTIF_BORDER = new Color(255, 215,  60, 200);

    public static final Color COL_OVERLAY      = new Color(  0,   0,   0, 170);
    public static final Color COL_OVERLAY_DARK = new Color(  0,   0,   0, 210);

    // ── Geometry ─────────────────────────────────────────────────────────────
    public static final int CORNER_RADIUS = 14;
    public static final int PANEL_PAD     = 14;

    // ── Strokes ──────────────────────────────────────────────────────────────
    public static final BasicStroke STROKE_BORDER = new BasicStroke(2f);
    public static final BasicStroke STROKE_THIN   = new BasicStroke(1f);
    public static final BasicStroke STROKE_THICK  = new BasicStroke(3f);

    // ── Timing ───────────────────────────────────────────────────────────────
    public static final int NOTIF_DURATION   = 180;   // frames
    public static final int ANIM_LERP_SPEED  = 6;     // HP bar smoothing divisor
}
