package ui.menu;

import core.GamePanel;
import ui.components.UIPanel;
import java.awt.*;

public class DialogueBox {

    public void draw(Graphics2D g2, GamePanel gp, int titleTimer) {
        if (gp.currentDialogueEntity == null) return;
        int boxH = 120;
        int boxY = gp.screenHeight - boxH - 20;
        UIPanel.draw(g2, 20, boxY, gp.screenWidth - 40, boxH);
        
        // Entity name
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(new Color(255, 220, 80));
        g2.drawString(gp.currentDialogueEntity.name, 36, boxY + 20);
        
        // Dialogue text  (word wrap at 60 chars)
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.setColor(Color.WHITE);
        String text = gp.currentDialogueEntity.getCurrentDialogue();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int ty = boxY + 44;
        for (String w : words) {
            if ((line + w).length() > 62) {
                g2.drawString(line.toString().trim(), 36, ty);
                line = new StringBuilder();
                ty += 18;
            }
            line.append(w).append(" ");
        }
        g2.drawString(line.toString().trim(), 36, ty);
        
        // Prompt
        if ((titleTimer / 20) % 2 == 0) {
            g2.setColor(new Color(180, 180, 255));
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString("▼ Press E to continue", gp.screenWidth - 200, boxY + boxH - 12);
        }
    }
}
