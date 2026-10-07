package ui;

import core.GamePanel;
import core.GameState;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Animated Title screen with stars, moon, ground, parallax silhouettes and prompts. */
public class TitleScreen {

    private final GamePanel gp;
    private int titleTimer = 0;
    private float titleAlpha = 0;

    public TitleScreen(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        titleTimer++;
        titleAlpha = Math.min(1.0f, titleTimer / 120f);
    }

    public void draw(Graphics2D g2) {
        // Sky gradient background
        GradientPaint sky = new GradientPaint(0, 0, new Color(10, 15, 40), 0, gp.screenHeight, new Color(40, 60, 120));
        g2.setPaint(sky);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setPaint(null);

        // Stars
        g2.setColor(Color.WHITE);
        long seed = 42;
        for (int i = 0; i < 60; i++) {
            seed = (seed * 1664525L + 1013904223L) & 0xFFFFFFFFL;
            int sx = (int) (seed % gp.screenWidth);
            seed = (seed * 1664525L + 1013904223L) & 0xFFFFFFFFL;
            int sy = (int) (seed % (gp.screenHeight / 2));
            float alpha = 0.4f + ((i % 5) * 0.12f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fillOval(sx, sy, 3, 3);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Moon
        g2.setColor(new Color(255, 250, 230));
        g2.fillOval(gp.screenWidth - 140, 30, 80, 80);
        g2.setColor(new Color(10, 15, 40));
        g2.fillOval(gp.screenWidth - 125, 25, 80, 80);

        // Ground silhouette
        GradientPaint ground = new GradientPaint(0, gp.screenHeight - 100, new Color(20, 50, 20),
                0, gp.screenHeight, new Color(10, 25, 10));
        g2.setPaint(ground);
        g2.fillRect(0, gp.screenHeight - 100, gp.screenWidth, 100);
        g2.setPaint(null);

        // Tree silhouettes
        g2.setColor(new Color(10, 30, 10));
        int[] treeX = {40, 100, 200, 560, 650, 720};
        for (int tx : treeX) {
            UIPanel.fillTriangle(g2, tx, gp.screenHeight - 100, tx + 30, gp.screenHeight, tx - 30, gp.screenHeight);
            UIPanel.fillTriangle(g2, tx + 5, gp.screenHeight - 130, tx + 25, gp.screenHeight - 90, tx - 15, gp.screenHeight - 90);
        }

        // Title fade in
        Composite orig = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));

        // Title shadow & text
        String title = "CHRONICLES OF THE";
        String title2 = "LOST KINGDOM";
        FontMetrics fm = g2.getFontMetrics(UIFonts.TITLE);
        int tx = gp.screenWidth / 2 - fm.stringWidth(title) / 2;
        int tx2 = gp.screenWidth / 2 - fm.stringWidth(title2) / 2;

        UIPanel.drawShadowText(g2, title, UIFonts.TITLE, UIConstants.COL_GOLD, tx, gp.screenHeight / 2 - 58);
        UIPanel.drawShadowText(g2, title2, UIFonts.TITLE, UIConstants.COL_GOLD, tx2, gp.screenHeight / 2 - 6);

        // Subtitle
        UIPanel.drawShadowTextCentered(g2, "An Open World RPG Adventure", UIFonts.SUBTITLE,
                new Color(180, 220, 255, 200), gp.screenWidth / 2, gp.screenHeight / 2 + 30);

        // Press Enter prompt (flashing)
        if ((titleTimer / 30) % 2 == 0) {
            String prompt = gp.saveManager.hasAnySave() ? "▶  Press ENTER to Continue  ◀" : "▶  Press ENTER to Begin  ◀";
            UIPanel.drawShadowTextCentered(g2, prompt, UIFonts.hud(14f), Color.WHITE, gp.screenWidth / 2, gp.screenHeight / 2 + 80);
        }

        // New Game prompt if save exists
        if (gp.saveManager.hasAnySave()) {
            String prompt2 = "Press R to Start New Game";
            UIPanel.drawShadowTextCentered(g2, prompt2, UIFonts.small(11f), new Color(255, 100, 100), gp.screenWidth / 2, gp.screenHeight / 2 + 100);
        }

        // Controls hint
        String controls = "ARROWS/WASD: Move  |  Z: Sword  |  X: Shield  |  V: Fire  |  E: Interact  |  I: Inv  |  Q: Quests";
        UIPanel.drawShadowTextCentered(g2, controls, UIFonts.SMALL, new Color(180, 180, 180, 180), gp.screenWidth / 2, gp.screenHeight - 20);

        g2.setComposite(orig);
    }
}
