package ui.hud;

import core.GamePanel;
import quest.Quest;
import java.awt.*;
import java.util.List;

public class QuestTrackerRenderer {

    public void draw(Graphics2D g2, GamePanel gp) {
        List<Quest> activeQuests = gp.questManager.getActiveQuests();
        if (!activeQuests.isEmpty()) {
            int qtX = gp.screenWidth / 2 - 120, qtY = 10;
            int qtW = 240, qtH = 14 + activeQuests.size() * 28;
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRoundRect(qtX, qtY, qtW, qtH, 10, 10);
            
            g2.setColor(new Color(255, 200, 60));
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString("★ ACTIVE QUESTS", qtX + 8, qtY + 12);

            int qy = qtY + 26;
            for (Quest q : activeQuests) {
                g2.setColor(new Color(220, 220, 255));
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.drawString("• " + q.title, qtX + 8, qy);
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.setColor(new Color(160, 200, 160));
                g2.drawString("  " + q.getProgress(), qtX + 8, qy + 12);
                qy += 28;
            }
        }
    }
}
