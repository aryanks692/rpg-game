package ui;

import core.GamePanel;
import quest.Quest;
import java.awt.*;
import java.util.List;

/** Quest log overlay with status icons and per-quest progress bars. */
public class QuestLogUI {

    private final GamePanel gp;

    public QuestLogUI(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        UIPanel.drawOverlay(g2, gp.screenWidth, gp.screenHeight, UIConstants.COL_OVERLAY);

        int pw = 510, ph = 360;
        int px = gp.screenWidth / 2 - pw / 2;
        int py = gp.screenHeight / 2 - ph / 2;
        UIPanel.drawPanel(g2, px, py, pw, ph);

        // Title
        UIPanel.drawShadowTextCentered(g2, "\u2605  QUEST LOG", UIFonts.HEADING,
                                        UIConstants.COL_GOLD, px + pw / 2, py + 30);
        UIPanel.drawSeparator(g2, px + 16, py + 34, pw - 32);

        List<Quest> quests = gp.questManager.getAllQuests();
        int completed = gp.questManager.totalCompleted();

        // Summary chip
        String summary = "Completed: " + completed + " / " + quests.size();
        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(UIConstants.COL_TEXT_GOOD);
        g2.drawString(summary, px + pw - 16 - g2.getFontMetrics().stringWidth(summary), py + 30);

        int qy = py + 50;
        for (Quest q : quests) {
            if (qy > py + ph - 40) break;
            drawQuestRow(g2, q, px + 14, qy, pw - 28);
            qy += questRowHeight(q);
        }

        // Close hint
        g2.setFont(UIFonts.SMALL);
        g2.setColor(UIConstants.COL_TEXT_DIM);
        String hint = "Press Q to close";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hint, px + pw / 2 - fm.stringWidth(hint) / 2, py + ph - 10);
    }

    private void drawQuestRow(Graphics2D g2, Quest q, int x, int y, int w) {
        // Status dot
        Color statusCol;
        String icon;
        switch (q.status) {
            case COMPLETED: statusCol = UIConstants.COL_TEXT_GOOD; icon = "\u2713"; break;
            case ACTIVE:    statusCol = UIConstants.COL_GOLD;       icon = "\u25b6"; break;
            default:        statusCol = UIConstants.COL_TEXT_DIM;  icon = "\u25cb"; break;
        }

        // Status icon
        g2.setFont(UIFonts.BODY_B);
        g2.setColor(statusCol);
        g2.drawString(icon, x, y + 13);

        // Title
        UIPanel.drawShadowText(g2, q.title, UIFonts.BODY_B, statusCol, x + 18, y + 13);

        // Description
        g2.setFont(UIFonts.SMALL);
        g2.setColor(new Color(statusCol.getRed(), statusCol.getGreen(),
                               statusCol.getBlue(), 190));
        g2.drawString(q.description, x + 20, y + 27);

        // Progress bar + text (active only)
        if (q.status == Quest.Status.ACTIVE) {
            String prog = q.getProgress();
            g2.setFont(UIFonts.SMALL);
            g2.setColor(UIConstants.COL_TEXT_GOOD);
            g2.drawString(prog, x + 20, y + 40);

            // Small progress bar
            float ratio = killRatio(q);
            if (ratio >= 0) {
                UIPanel.drawBar(g2, x + 20, y + 44, 150, 5, ratio,
                                 UIConstants.COL_XP_BG, UIConstants.COL_XP_FILL,
                                 new Color(30, 160, 80));
            }
        }

        UIPanel.drawSeparator(g2, x, y + questRowHeight(q) - 4, w);
    }

    private int questRowHeight(Quest q) {
        return q.status == Quest.Status.ACTIVE ? 58 : 42;
    }

    private float killRatio(Quest q) {
        if (q.type != Quest.Type.KILL || q.killRequired <= 0) return -1;
        return Math.min(1f, (float) q.killCount / q.killRequired);
    }
}
