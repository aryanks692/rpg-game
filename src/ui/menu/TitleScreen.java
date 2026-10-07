package ui.menu;

import core.GamePanel;
import java.awt.*;

public class TitleScreen {

    private Font titleFont = new Font("Georgia", Font.BOLD, 48);
    private Font subtitleFont = new Font("Georgia", Font.BOLD | Font.ITALIC, 22);
    private Font hudFont = new Font("Arial", Font.BOLD, 13);
    private Font smallFont = new Font("Arial", Font.PLAIN, 11);

    public void draw(Graphics2D g2, GamePanel gp, int titleTimer) {
        // Sky gradient background
        GradientPaint sky = new GradientPaint(0, 0, new Color(10, 15, 40), 0, gp.screenHeight, new Color(40, 60, 120));
        g2.setPaint(sky);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Stars
        g2.setColor(Color.WHITE);
        long seed = 42;
        for (int i = 0; i < 60; i++) {
            seed = (seed * 1664525L + 1013904223L) & 0xFFFFFFFFL;
            int sx = (int)(seed % gp.screenWidth);
            seed = (seed * 1664525L + 1013904223L) & 0xFFFFFFFFL;
            int sy = (int)(seed % (gp.screenHeight / 2));
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
        
        // Tree silhouettes
        g2.setColor(new Color(10, 30, 10));
        int[] treeX = {40, 100, 200, 560, 650, 720};
        for (int tx : treeX) {
            g2.fillPolygon(new int[]{tx, tx + 30, tx - 30}, new int[]{gp.screenHeight - 100, gp.screenHeight, gp.screenHeight}, 3);
            g2.fillPolygon(new int[]{tx + 5, tx + 25, tx - 15}, new int[]{gp.screenHeight - 130, gp.screenHeight - 90, gp.screenHeight - 90}, 3);
        }

        // Title fade in
        float titleAlpha = Math.min(1.0f, titleTimer / 120f);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));

        // Title shadow
        g2.setFont(titleFont);
        String title = "CHRONICLES OF THE";
        String title2 = "LOST KINGDOM";
        FontMetrics fm = g2.getFontMetrics();
        int tx = gp.screenWidth / 2 - fm.stringWidth(title) / 2;
        int tx2 = gp.screenWidth / 2 - fm.stringWidth(title2) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(title, tx + 3, gp.screenHeight / 2 - 55);
        g2.drawString(title2, tx2 + 3, gp.screenHeight / 2 - 3);

        // Title gradient text
        GradientPaint titlePaint = new GradientPaint(0, gp.screenHeight/2 - 80, new Color(255, 220, 80),
            0, gp.screenHeight/2, new Color(200, 120, 30));
        g2.setPaint(titlePaint);
        g2.drawString(title, tx, gp.screenHeight / 2 - 58);
        g2.drawString(title2, tx2, gp.screenHeight / 2 - 6);

        // Subtitle
        g2.setFont(subtitleFont);
        g2.setColor(new Color(180, 220, 255, 200));
        String sub = "An Open World RPG Adventure";
        fm = g2.getFontMetrics();
        g2.drawString(sub, gp.screenWidth / 2 - fm.stringWidth(sub) / 2, gp.screenHeight / 2 + 30);

        // Press Enter prompt (flashing)
        if ((titleTimer / 30) % 2 == 0) {
            g2.setFont(hudFont);
            g2.setColor(Color.WHITE);
            String prompt = gp.saveManager.hasAnySave() ? "▶  Press ENTER to Continue  ◀" : "▶  Press ENTER to Begin  ◀";
            fm = g2.getFontMetrics();
            g2.drawString(prompt, gp.screenWidth / 2 - fm.stringWidth(prompt) / 2, gp.screenHeight / 2 + 80);
        }

        // New Game prompt if save exists
        if (gp.saveManager.hasAnySave()) {
            g2.setFont(smallFont);
            g2.setColor(new Color(255, 100, 100));
            String prompt2 = "Press R to Start New Game";
            fm = g2.getFontMetrics();
            g2.drawString(prompt2, gp.screenWidth / 2 - fm.stringWidth(prompt2) / 2, gp.screenHeight / 2 + 100);
        }

        // Controls hint
        g2.setFont(smallFont);
        g2.setColor(new Color(180, 180, 180, 180));
        String controls = "ARROWS/WASD: Move  |  Z: Sword  |  X: Shield  |  V: Fire  |  E: Interact  |  I: Inv  |  Q: Quests";
        fm = g2.getFontMetrics();
        g2.drawString(controls, gp.screenWidth / 2 - fm.stringWidth(controls) / 2, gp.screenHeight - 20);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
