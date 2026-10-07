package ui;

import core.GamePanel;
import java.awt.*;

/** WinScreen shown when the player wins. */
public class WinScreen {

    private final GamePanel gp;

    public WinScreen(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        GradientPaint gp2 = new GradientPaint(0, 0, new Color(20, 10, 40), 0, gp.screenHeight, new Color(80, 40, 120));
        g2.setPaint(gp2);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setPaint(null);

        String text = "VICTORY!";
        UIPanel.drawShadowTextCentered(g2, text, UIFonts.WIN, UIConstants.COL_GOLD, gp.screenWidth / 2, gp.screenHeight / 2 - 20);

        UIPanel.drawShadowTextCentered(g2, "You have conquered the Lost Kingdom!", UIFonts.HUD, new Color(220, 220, 255), gp.screenWidth / 2, gp.screenHeight / 2 + 30);
        UIPanel.drawShadowTextCentered(g2, "Press ENTER to return to title", UIFonts.HUD, new Color(220, 220, 255), gp.screenWidth / 2, gp.screenHeight / 2 + 60);
    }
}
