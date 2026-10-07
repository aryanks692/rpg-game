package ui;

import core.GamePanel;
import java.awt.*;

/** Death Screen shown when player dies. Offers restore or restart. */
public class DeathScreen {

    private final GamePanel gp;

    public DeathScreen(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        FontMetrics fm = g2.getFontMetrics(UIFonts.DEATH);
        String text = "YOU DIED";
        UIPanel.drawShadowTextCentered(g2, text, UIFonts.DEATH, new Color(200, 30, 30), gp.screenWidth / 2, gp.screenHeight / 2 - 40);

        if (gp.saveManager.hasAnySave()) {
            String t1 = "Press ENTER to Restore from Save";
            UIPanel.drawShadowTextCentered(g2, t1, UIFonts.HUD, new Color(220, 220, 255), gp.screenWidth / 2, gp.screenHeight / 2 + 20);
        }

        String t2 = "Press R to Start a New Game";
        UIPanel.drawShadowTextCentered(g2, t2, UIFonts.HUD, new Color(255, 100, 100), gp.screenWidth / 2, gp.screenHeight / 2 + 50);
    }
}
