package ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

/** Central font registry — lazy-loaded, shared across all UI classes. */
public final class UIFonts {

    private UIFonts() {}

    // ── Base faces ────────────────────────────────────────────────────────────
    public static final Font TITLE    = new Font("Georgia",    Font.BOLD,              48);
    public static final Font SUBTITLE = new Font("Georgia",    Font.BOLD | Font.ITALIC, 20);
    public static final Font HEADING  = new Font("Georgia",    Font.BOLD,              22);
    public static final Font BODY     = new Font("Arial",      Font.PLAIN,             13);
    public static final Font BODY_B   = new Font("Arial",      Font.BOLD,              13);
    public static final Font SMALL    = new Font("Arial",      Font.PLAIN,             11);
    public static final Font SMALL_B  = new Font("Arial",      Font.BOLD,              11);
    public static final Font HUD      = new Font("Arial",      Font.BOLD,              13);
    public static final Font ZONE     = new Font("Georgia",    Font.ITALIC,            13);
    public static final Font NOTIF    = new Font("Arial",      Font.BOLD,              14);
    public static final Font BOSS     = new Font("Georgia",    Font.BOLD,              20);
    public static final Font DEATH    = new Font("Georgia",    Font.BOLD,              64);
    public static final Font WIN      = new Font("Georgia",    Font.BOLD,              52);

    // ── Derived sizes (call Font.deriveFont) ──────────────────────────────────
    public static Font title(float size)   { return TITLE.deriveFont(size); }
    public static Font body(float size)    { return BODY.deriveFont(size);  }
    public static Font bodyB(float size)   { return BODY_B.deriveFont(size); }
    public static Font small(float size)   { return SMALL.deriveFont(size); }
    public static Font hud(float size)     { return HUD.deriveFont(size);   }
}
