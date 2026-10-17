package ui;

import core.GamePanel;
import core.GameState;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Intro story cutscene shown when a brand-new game begins.
 * Features 5 illustrated cinematic pixel art panels with typewriter subtitles.
 * ENTER / SPACE / E / Z advances the cutscene; ESC skips to the village.
 */
public class CutsceneScreen {

    private final GamePanel gp;
    private final BufferedImage[] panelImages = new BufferedImage[5];

    /** A single story panel: heading, panel index, and story narration lines. */
    private static final class Scene {
        final String heading;
        final int panelIndex;
        final String[] lines;

        Scene(String heading, int panelIndex, String... lines) {
            this.heading = heading;
            this.panelIndex = panelIndex;
            this.lines = lines;
        }
    }

    private static final Scene[] SCENES = {
        new Scene("The Ancient Legend", 0,
                "Long ago, before the kingdom fell to ruin, a sacred blade",
                "was sealed deep within the forgotten sanctuary.",
                "Legends say only a chosen adventurer can awaken its ancient light."),

        new Scene("The Awakening", 1,
                "As a golden dawn breaks over Verdant Village, you awake to",
                "warm sunlight streaming through your cottage window.",
                "A quiet whisper of destiny calls you to rise and take up your blade."),

        new Scene("The Elder's Quest", 2,
                "In the village square, Elder Rowan gestures toward the horizon.",
                "'Brave wanderer, dark forces awaken beyond our borders. The",
                "ancient sanctuary calls. Seek the truth and restore our land.'"),

        new Scene("Beyond the Village", 3,
                "Leaving the warm wooden homes of Verdant Village behind,",
                "you reach the boundary trail. Ahead lies the shadowy canopy",
                "and glowing twilight mist of the Darkwood Forest."),

        new Scene("The Crystal Gate", 4,
                "Deep within the ancient grove stands the radiant Crystal Gate,",
                "pulsating with violet arcane power as dark spirits stir in the mist.",
                "Your journey begins now. Stand brave and forge your chronicle!"),
    };

    private int sceneIndex = 0;
    private int revealed = 0;      // characters shown of current panel
    private int timer = 0;
    private float fade = 0f;       // per-panel fade-in (0..1)
    private boolean finishing = false;
    private int finishTimer = 0;

    public CutsceneScreen(GamePanel gp) {
        this.gp = gp;
        loadPanelImages();
    }

    private void loadPanelImages() {
        for (int i = 0; i < 5; i++) {
            String path = "/res/cutscene/panel" + (i + 1) + ".jpg";
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    panelImages[i] = ImageIO.read(is);
                }
            } catch (Exception ignored) {}

            // Fallback: direct filesystem loading if running unpacked
            if (panelImages[i] == null) {
                try {
                    File f = new File("src/res/cutscene/panel" + (i + 1) + ".jpg");
                    if (f.exists()) panelImages[i] = ImageIO.read(f);
                } catch (Exception ignored) {}
            }
        }
    }

    public void reset() {
        sceneIndex = 0;
        revealed = 0;
        timer = 0;
        fade = 0f;
        finishing = false;
        finishTimer = 0;
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
            if (finishTimer >= 35) finish();
            return;
        }

        if (fade < 1f) fade = Math.min(1f, fade + 1f / 16f);

        // ESC skips the entire intro.
        if (gp.keyHandler.pauseJustPressed) {
            gp.keyHandler.clearJustPressed();
            finish();
            return;
        }

        boolean advance = gp.keyHandler.enterJustPressed
                || gp.keyHandler.fireJustPressed
                || gp.keyHandler.interactJustPressed
                || gp.keyHandler.attackJustPressed;

        if (advance) {
            if (revealed < totalChars()) {
                revealed = totalChars();   // First press completes the typewriter text immediately
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
        gp.ui.showNotification("Welcome to Verdant Village! Explore and speak with the villagers.");
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    public void draw(Graphics2D g2) {
        Scene sc = scene();

        // 1. Draw the high-detail cinematic pixel art illustration
        BufferedImage img = (sc.panelIndex >= 0 && sc.panelIndex < panelImages.length)
                ? panelImages[sc.panelIndex] : null;

        if (img != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(img, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            // Elegant fallback background
            GradientPaint sky = new GradientPaint(0, 0, new Color(20, 24, 38), 0, gp.screenHeight, new Color(8, 10, 16));
            g2.setPaint(sky);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            g2.setPaint(null);
        }

        // 2. Draw cinematic subtitle dialog box
        drawText(g2, sc);

        // 3. Cinematic top and bottom letterbox bars
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, 20);
        g2.fillRect(0, gp.screenHeight - 12, gp.screenWidth, 12);

        // 4. Per-panel fade-in veil
        if (fade < 1f) {
            g2.setColor(new Color(0, 0, 0, (int) ((1f - fade) * 255)));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        // 5. Final transition fade out
        if (finishing) {
            int a = Math.min(255, finishTimer * 255 / 35);
            g2.setColor(new Color(0, 0, 0, a));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    private void drawText(Graphics2D g2, Scene sc) {
        int boxH = 132;
        int boxY = gp.screenHeight - boxH - 16;
        int boxX = 20;
        int boxW = gp.screenWidth - 40;

        // Cinematic deep-navy glass banner with warm golden border
        g2.setColor(new Color(6, 10, 22, 225));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g2.setColor(new Color(212, 175, 55, 230));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g2.setStroke(new BasicStroke(1f));

        // Heading with warm gold
        g2.setFont(UIFonts.title(22f));
        g2.setColor(UIConstants.COL_GOLD);
        g2.drawString(sc.heading, boxX + 24, boxY + 34);

        // Panel counter (e.g., "1 / 5")
        g2.setFont(UIFonts.small(12f));
        g2.setColor(new Color(255, 220, 120, 190));
        String progress = (sceneIndex + 1) + " / " + SCENES.length;
        g2.drawString(progress, boxX + boxW - 24 - g2.getFontMetrics().stringWidth(progress), boxY + 32);

        // Body narration (Typewriter effect)
        g2.setFont(UIFonts.body(15f));
        g2.setColor(Color.WHITE);
        int remaining = revealed;
        int ty = boxY + 64;
        for (String line : sc.lines) {
            int show = Math.max(0, Math.min(line.length(), remaining));
            if (show > 0) g2.drawString(line.substring(0, show), boxX + 24, ty);
            remaining -= line.length();
            ty += 22;
        }

        // Advance prompt (blinking once fully typed)
        if (revealed >= totalChars() && (timer / 25) % 2 == 0) {
            g2.setFont(UIFonts.hud(12f));
            g2.setColor(UIConstants.COL_GOLD);
            String p = (sceneIndex == SCENES.length - 1) ? "ENTER / SPACE: Begin Adventure ▶" : "ENTER / SPACE: Next ▶";
            g2.drawString(p, boxX + boxW - 24 - g2.getFontMetrics().stringWidth(p), boxY + boxH - 12);
        }

        // Skip hint in bottom-left corner of the box
        g2.setFont(UIFonts.small(11f));
        g2.setColor(new Color(200, 200, 200, 160));
        g2.drawString("ESC: Skip Intro", boxX + 24, boxY + boxH - 12);
    }
}
