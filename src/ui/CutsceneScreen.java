package ui;

import core.GamePanel;
import core.GameState;
import java.awt.*;

/**
 * Intro story cutscene shown when a brand-new game begins.
 * Presents a sequence of illustrated, letterboxed panels with a typewriter
 * effect. ENTER / E / Z completes or advances the current panel; ESC skips
 * the whole intro. Finishing drops the player into PLAY at the village spawn.
 */
public class CutsceneScreen {

    private final GamePanel gp;

    /** A single story panel: heading, body lines, sky palette and emblem art. */
    private static final class Scene {
        final String heading;
        final String[] lines;
        final Color skyTop, skyBottom;
        final int emblem;   // 0 castle, 1 crystal, 2 slimes, 3 wanderer, 4 hero, 5 gate
        Scene(String heading, Color skyTop, Color skyBottom, int emblem, String... lines) {
            this.heading = heading;
            this.skyTop = skyTop;
            this.skyBottom = skyBottom;
            this.emblem = emblem;
            this.lines = lines;
        }
    }

    private static final Scene[] SCENES = {
        new Scene("The Kingdom of Aetheria",
                new Color(255, 196, 110), new Color(255, 238, 190), 0,
                "Long ago, the Kingdom of Aetheria prospered beneath",
                "a sky of endless dawn, watched over by the light",
                "of the Crystal Heart."),

        new Scene("The Shattering",
                new Color(60, 20, 70), new Color(20, 8, 34), 1,
                "But a creeping blight fell upon the land. The Crystal",
                "Heart shattered, and its fragments were scattered",
                "across the realm."),

        new Scene("The Long Dark",
                new Color(30, 34, 46), new Color(10, 12, 20), 2,
                "The fields grew cold. Slimes swarmed from the marshes,",
                "and in the deep caves a Dark Knight arose to guard",
                "what little light remained."),

        new Scene("The Wanderer",
                new Color(255, 170, 120), new Color(90, 60, 110), 3,
                "For a hundred years the legend slept, until a lone",
                "wanderer crossed the Golden Meadows toward the",
                "gates of Verdant Village."),

        new Scene("A Hero Rises",
                new Color(120, 170, 255), new Color(20, 30, 70), 4,
                "You are that wanderer. The elders whisper that only a",
                "hero who can reforge the Crystal Heart may wake the",
                "Lost Kingdom once more."),

        new Scene("The Journey Begins",
                new Color(150, 210, 255), new Color(230, 245, 255), 5,
                "Your chronicle begins now. Seek out Elder Rowan in",
                "Verdant Village, take up your blade, and let the",
                "chronicles of the Lost Kingdom unfold."),
    };

    private int sceneIndex = 0;
    private int revealed = 0;      // characters shown of the current panel
    private int timer = 0;
    private float fade = 0f;       // per-panel fade-in (0..1)
    private boolean finishing = false;
    private int finishTimer = 0;

    public CutsceneScreen(GamePanel gp) {
        this.gp = gp;
    }

    private Scene scene() {
        return SCENES[Math.min(sceneIndex, SCENES.length - 1)];
    }

    private int totalChars() {
        int n = 0;
        for (String s : scene().lines) n += s.length();
        return n;
    }

    public void update() {
        timer++;

        // Fade out to gameplay once the player dismissed the final panel.
        if (finishing) {
            finishTimer++;
            if (finishTimer >= 40) finish();
            return;
        }

        if (fade < 1f) fade = Math.min(1f, fade + 1f / 20f);

        // ESC skips the entire intro.
        if (gp.keyHandler.pauseJustPressed) {
            gp.keyHandler.clearJustPressed();
            finish();
            return;
        }

        boolean advance = gp.keyHandler.enterJustPressed
                || gp.keyHandler.interactJustPressed
                || gp.keyHandler.attackJustPressed;

        if (advance) {
            if (revealed < totalChars()) {
                revealed = totalChars();   // first press completes the typing
            } else {
                advanceScene();
            }
            gp.keyHandler.clearJustPressed();
            return;
        }

        // Typewriter: reveal ~2 characters per frame.
        if (revealed < totalChars()) {
            revealed = Math.min(totalChars(), revealed + 2);
        }
    }

    private void advanceScene() {
        if (sceneIndex < SCENES.length - 1) {
            sceneIndex++;
            revealed = 0;
            fade = 0f;
            timer = 0;
        } else {
            finishing = true;
            finishTimer = 0;
        }
    }

    private void finish() {
        gp.keyHandler.clearJustPressed();
        gp.gameState = GameState.PLAY;
        gp.ui.showNotification("Speak with Elder Rowan in Verdant Village.");
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    public void draw(Graphics2D g2) {
        Scene sc = scene();

        // Sky gradient
        GradientPaint sky = new GradientPaint(0, 0, sc.skyTop, 0, gp.screenHeight, sc.skyBottom);
        g2.setPaint(sky);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setPaint(null);

        drawEmblem(g2, sc.emblem);
        drawText(g2, sc);

        // Cinematic letterbox bars
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, 44);
        g2.fillRect(0, gp.screenHeight - 22, gp.screenWidth, 22);

        // Per-panel fade-in veil
        if (fade < 1f) {
            g2.setColor(new Color(0, 0, 0, (int) ((1f - fade) * 255)));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        // Fade out when the intro is dismissed
        if (finishing) {
            int a = Math.min(255, finishTimer * 255 / 40);
            g2.setColor(new Color(0, 0, 0, a));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    private void drawText(Graphics2D g2, Scene sc) {
        int boxH = 150;
        int boxY = gp.screenHeight - boxH;

        // Subtitle band
        g2.setColor(new Color(4, 6, 20, 215));
        g2.fillRect(0, boxY, gp.screenWidth, boxH);
        g2.setColor(new Color(180, 150, 60, 210));
        g2.fillRect(0, boxY, gp.screenWidth, 2);

        // Heading
        g2.setFont(UIFonts.title(24f));
        g2.setColor(UIConstants.COL_GOLD);
        g2.drawString(sc.heading, 40, boxY + 44);

        // Body (typewriter)
        g2.setFont(UIFonts.body(16f));
        g2.setColor(UIConstants.COL_TEXT_MAIN);
        int remaining = revealed;
        int ty = boxY + 76;
        for (String line : sc.lines) {
            int show = Math.max(0, Math.min(line.length(), remaining));
            if (show > 0) g2.drawString(line.substring(0, show), 40, ty);
            remaining -= line.length();
            ty += 24;
        }

        // Advance prompt (blinks once the panel is fully typed)
        if (revealed >= totalChars() && (timer / 25) % 2 == 0) {
            g2.setFont(UIFonts.hud(14f));
            g2.setColor(Color.WHITE);
            String p = (sceneIndex == SCENES.length - 1) ? "ENTER: Begin" : "ENTER: Next";
            g2.drawString(p, gp.screenWidth - 40 - g2.getFontMetrics().stringWidth(p), boxY + 44);
        }

        // Skip hint
        g2.setFont(UIFonts.small(11f));
        g2.setColor(new Color(255, 255, 255, 160));
        String hint = "ESC: Skip Intro";
        g2.drawString(hint, gp.screenWidth - 20 - g2.getFontMetrics().stringWidth(hint), gp.screenHeight - 7);
    }

    // ── Emblem art ────────────────────────────────────────────────────────────

    private void drawEmblem(Graphics2D g2, int emblem) {
        int cx = gp.screenWidth / 2;
        int gy = gp.screenHeight - 170;   // ground line, just above the text band
        switch (emblem) {
            case 0: drawCastle(g2, cx, gy); break;
            case 1: drawCrystal(g2, cx, gy); break;
            case 2: drawSlimes(g2, cx, gy); break;
            case 3: drawWanderer(g2, cx, gy); break;
            case 4: drawHero(g2, cx, gy); break;
            case 5: drawGate(g2, cx, gy); break;
        }
    }

    private void drawCastle(Graphics2D g2, int cx, int gy) {
        g2.setColor(new Color(60, 100, 55));
        g2.fillOval(cx - 260, gy - 40, 520, 150);

        g2.setColor(new Color(96, 84, 120));
        g2.fillRect(cx - 130, gy - 110, 44, 110);   // left tower
        g2.fillRect(cx + 86, gy - 110, 44, 110);    // right tower
        g2.fillRect(cx - 60, gy - 155, 120, 155);   // keep

        g2.setColor(new Color(160, 45, 65));
        UIPanel.fillTriangle(g2, cx - 138, gy - 110, cx - 108, gy - 156, cx - 78, gy - 110);
        UIPanel.fillTriangle(g2, cx + 78, gy - 110, cx + 108, gy - 156, cx + 138, gy - 110);
        UIPanel.fillTriangle(g2, cx - 68, gy - 155, cx, gy - 208, cx + 68, gy - 155);

        g2.setColor(UIConstants.COL_GOLD);
        g2.fillRect(cx - 2, gy - 240, 4, 34);
        UIPanel.fillTriangle(g2, cx + 2, gy - 240, cx + 34, gy - 230, cx + 2, gy - 218);
    }

    private void drawCrystal(Graphics2D g2, int cx, int gy) {
        g2.setColor(new Color(30, 36, 56));
        g2.fillOval(cx - 260, gy - 30, 520, 130);

        g2.setColor(new Color(120, 200, 255, 60));
        g2.fillOval(cx - 95, gy - 205, 190, 225);

        Polygon p = new Polygon();
        p.addPoint(cx, gy - 195);
        p.addPoint(cx + 58, gy - 110);
        p.addPoint(cx, gy);
        p.addPoint(cx - 58, gy - 110);
        GradientPaint gp = new GradientPaint(cx, gy - 195, new Color(205, 240, 255), cx, gy, new Color(70, 120, 200));
        g2.setPaint(gp);
        g2.fillPolygon(p);
        g2.setPaint(null);
        g2.setColor(new Color(255, 255, 255, 130));
        g2.setStroke(UIConstants.STROKE_BORDER);
        g2.drawPolygon(p);

        g2.setColor(new Color(24, 24, 44));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(cx, gy - 180, cx - 13, gy - 110);
        g2.drawLine(cx - 13, gy - 110, cx + 9, gy - 62);
        g2.drawLine(cx + 9, gy - 62, cx - 5, gy - 12);
        g2.setStroke(UIConstants.STROKE_THIN);
    }

    private void drawSlimes(Graphics2D g2, int cx, int gy) {
        g2.setColor(new Color(22, 32, 24));
        g2.fillOval(cx - 300, gy - 12, 600, 110);

        int[][] pos = { {cx - 115, gy, 64}, {cx, gy, 92}, {cx + 122, gy, 58} };
        for (int[] s : pos) {
            int x = s[0], y = s[1], sz = s[2];
            g2.setColor(new Color(72, 165, 92));
            g2.fillArc(x - sz / 2, y - sz, sz, sz * 2, 0, 180);
            g2.setColor(Color.WHITE);
            g2.fillOval(x - sz / 5 - 4, y - sz / 2, 9, 9);
            g2.fillOval(x + sz / 5 - 5, y - sz / 2, 9, 9);
            g2.setColor(Color.BLACK);
            g2.fillOval(x - sz / 5 - 2, y - sz / 2 + 3, 4, 4);
            g2.fillOval(x + sz / 5 - 3, y - sz / 2 + 3, 4, 4);
        }
    }

    private void drawWanderer(Graphics2D g2, int cx, int gy) {
        g2.setColor(new Color(255, 232, 165, 220));
        g2.fillOval(cx - 75, gy - 235, 150, 150);

        g2.setColor(new Color(120, 86, 66));
        g2.fillRect(0, gy - 26, gp.screenWidth, 36);
        g2.setColor(new Color(88, 66, 54));
        g2.fillOval(-120, gy - 40, 420, 120);
        g2.fillOval(gp.screenWidth - 300, gy - 40, 420, 120);

        g2.setColor(new Color(32, 22, 38));
        int fx = cx;
        g2.fillOval(fx - 8, gy - 100, 16, 16);
        g2.fillRect(fx - 9, gy - 84, 18, 50);
        g2.fillRect(fx - 16, gy - 80, 8, 32);
        g2.fillRect(fx + 8, gy - 80, 8, 32);
        g2.fillRect(fx - 11, gy - 34, 8, 34);
        g2.fillRect(fx + 3, gy - 34, 8, 34);
    }

    private void drawHero(Graphics2D g2, int cx, int gy) {
        g2.setColor(new Color(255, 255, 210, 55));
        g2.fillOval(cx - 170, gy - 310, 340, 340);
        g2.setColor(new Color(255, 255, 210, 45));
        g2.fillOval(cx - 115, gy - 255, 230, 230);

        g2.setColor(new Color(20, 30, 60));
        g2.fillOval(cx - 250, gy - 12, 500, 100);

        g2.setColor(new Color(14, 18, 44));
        g2.fillOval(cx - 10, gy - 124, 20, 20);
        g2.fillRect(cx - 13, gy - 104, 26, 66);
        g2.fillRect(cx - 21, gy - 100, 10, 42);
        g2.fillRect(cx - 19, gy - 38, 10, 40);
        g2.fillRect(cx + 9, gy - 38, 10, 40);

        g2.setStroke(new BasicStroke(6f));
        g2.setColor(new Color(222, 232, 255));
        g2.drawLine(cx + 20, gy - 96, cx + 56, gy - 174);
        g2.setStroke(UIConstants.STROKE_THIN);
        g2.setColor(UIConstants.COL_GOLD);
        g2.fillRect(cx + 12, gy - 106, 18, 8);
    }

    private void drawGate(Graphics2D g2, int cx, int gy) {
        g2.setColor(new Color(96, 165, 84));
        g2.fillOval(cx - 320, gy - 34, 640, 140);

        g2.setColor(new Color(152, 122, 92));
        g2.fillRect(cx - 210, gy - 122, 92, 122);
        g2.fillRect(cx + 152, gy - 102, 82, 102);
        g2.setColor(new Color(158, 52, 52));
        UIPanel.fillTriangle(g2, cx - 220, gy - 122, cx - 164, gy - 174, cx - 108, gy - 122);
        UIPanel.fillTriangle(g2, cx + 142, gy - 102, cx + 193, gy - 150, cx + 244, gy - 102);

        g2.setColor(new Color(255, 222, 140));
        g2.fillRect(cx - 188, gy - 92, 16, 16);
        g2.fillRect(cx + 172, gy - 82, 16, 16);

        g2.setColor(new Color(124, 98, 72));
        g2.fillRect(cx - 72, gy - 132, 16, 132);
        g2.fillRect(cx + 56, gy - 132, 16, 132);
        g2.fillRect(cx - 72, gy - 152, 144, 22);

        g2.setColor(new Color(204, 184, 132));
        Polygon path = new Polygon();
        path.addPoint(cx - 56, gy);
        path.addPoint(cx + 56, gy);
        path.addPoint(cx + 104, gy + 40);
        path.addPoint(cx - 104, gy + 40);
        g2.fillPolygon(path);
    }
}
