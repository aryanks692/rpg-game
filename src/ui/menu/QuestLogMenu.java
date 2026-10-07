package ui.menu;

import core.GamePanel;
import quest.Quest;
import ui.components.UIPanel;
import java.awt.*;
import java.util.List;

public class QuestLogMenu {

    private Font smallFont = new Font("Arial", Font.PLAIN, 11);

    public void draw(Graphics2D g2, GamePanel gp) {
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        int pw = 500, ph = 360;
        int px = gp.screenWidth / 2 - pw / 2, py = gp.screenHeight / 2 - ph / 2;
        UIPanel.draw(g2, px, py, pw, ph);

        g2.setFont(new Font("Georgia", Font.BOLD, 22));
        g2.setColor(new Color(255, 220, 80));
        g2.drawString("★  QUEST LOG", px + 160, py + 30);

        List<Quest> quests = gp.questManager.getAllQuests();
        int qy = py + 56;
        for (Quest q : quests) {
            Color c;
            switch (q.status) {
                case COMPLETED: c = new Color(100, 220, 100); break;
                case ACTIVE:    c = new Color(220, 220, 255); break;
                default:        c = new Color(100, 100, 140); break;
            }
            String prefix;
            switch (q.status) {
                case COMPLETED: prefix = "\u2713 "; break;
                case ACTIVE:    prefix = "\u2192 "; break;
                default:        prefix = "\u25cb "; break;
            }
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(c);
            g2.drawString(prefix + q.title, px + 20, qy);
            g2.setFont(smallFont);
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
            g2.drawString(q.description, px + 36, qy + 14);
            if (q.status == quest.Quest.Status.ACTIVE) {
                g2.setColor(new Color(180, 220, 140));
                g2.drawString(q.getProgress(), px + 36, qy + 27);
                qy += 12;
            }
            g2.setColor(new Color(60, 60, 100));
            g2.drawLine(px + 10, qy + 33, px + pw - 10, qy + 33);
            qy += 46;
            if (qy > py + ph - 30) break;
        }

        g2.setFont(smallFont);
        g2.setColor(new Color(160, 160, 200));
        g2.drawString("Press Q to close | Completed: " + gp.questManager.totalCompleted() + "/" + quests.size(),
            px + 150, py + ph - 10);
    }
}
