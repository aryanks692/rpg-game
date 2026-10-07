package ui;

import core.GamePanel;
import entity.Entity;
import java.awt.*;

/**
 * Dialogue box with typewriter text reveal and portrait placeholder.
 */
public class DialogueUI {

    private final GamePanel gp;

    // Typewriter state
    private String   fullText        = "";
    private int      charsRevealed   = 0;
    private int      charTimer       = 0;
    private static final int CHARS_PER_TICK = 1;
    private static final int CHAR_DELAY     = 2; // ticks between chars

    private int globalTimer = 0;

    public DialogueUI(GamePanel gp) {
        this.gp = gp;
    }

    /** Call when a new dialogue line starts. */
    public void startDialogue(String text) {
        if (!text.equals(fullText)) {
            fullText      = text;
            charsRevealed = 0;
            charTimer     = 0;
        }
    }

    public void update() {
        globalTimer++;
        if (charsRevealed < fullText.length()) {
            charTimer++;
            if (charTimer >= CHAR_DELAY) {
                charTimer = 0;
                charsRevealed = Math.min(charsRevealed + CHARS_PER_TICK, fullText.length());
            }
        }
    }

    public void draw(Graphics2D g2) {
        Entity entity = gp.currentDialogueEntity;
        if (entity == null) return;

        // Sync typewriter with current dialogue
        String cur = entity.getCurrentDialogue();
        startDialogue(cur);

        int boxH   = 120;
        int boxW   = gp.screenWidth - 40;
        int boxX   = 20;
        int boxY   = gp.screenHeight - boxH - 18;
        int portW  = 68;
        int textX  = boxX + portW + 20;

        // Main panel
        UIPanel.drawPanel(g2, boxX, boxY, boxW, boxH);

        // Portrait frame
        g2.setColor(new Color(30, 25, 60));
        g2.fillRoundRect(boxX + 8, boxY + 8, portW, boxH - 16, 8, 8);
        
        if (entity.portraitImage != null) {
            // Draw real photo portrait with clip to fit rounded box
            Shape oldClip = g2.getClip();
            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(boxX + 8, boxY + 8, portW, boxH - 16, 8, 8));
            g2.drawImage(entity.portraitImage, boxX + 8, boxY + 8, portW, boxH - 16, null);
            g2.setClip(oldClip);
        } else {
            // Letter placeholder fallback
            g2.setFont(UIFonts.HEADING);
            g2.setColor(UIConstants.COL_GOLD);
            String letter = entity.name.isEmpty() ? "?" : entity.name.substring(0, 1);
            FontMetrics fmP = g2.getFontMetrics();
            g2.drawString(letter,
                           boxX + 8 + portW / 2 - fmP.stringWidth(letter) / 2,
                           boxY + 8 + (boxH - 16) / 2 + fmP.getAscent() / 2);
        }

        g2.setStroke(UIConstants.STROKE_BORDER);
        g2.setColor(UIConstants.COL_PANEL_BORDER_DIM);
        g2.drawRoundRect(boxX + 8, boxY + 8, portW, boxH - 16, 8, 8);

        // Entity name
        g2.setFont(UIFonts.BODY_B);
        UIPanel.drawShadowText(g2, entity.name, UIFonts.BODY_B, UIConstants.COL_GOLD,
                                textX, boxY + 20);
        UIPanel.drawSeparator(g2, textX, boxY + 24, boxW - portW - 30);

        // Typewriter text (word-wrapped)
        g2.setFont(UIFonts.BODY);
        g2.setColor(UIConstants.COL_TEXT_MAIN);
        String visible = fullText.substring(0, Math.min(charsRevealed, fullText.length()));
        drawWrapped(g2, visible, textX, boxY + 42, boxW - portW - 34, 18);

        // "Continue" prompt — flash when text is fully shown
        if (charsRevealed >= fullText.length() && (globalTimer / 22) % 2 == 0) {
            g2.setFont(UIFonts.SMALL_B);
            g2.setColor(new Color(170, 170, 255));
            g2.drawString("\u25bc Press E to continue",
                           boxX + boxW - 160, boxY + boxH - 10);
        }

        g2.setStroke(UIConstants.STROKE_THIN);
    }

    private void drawWrapped(Graphics2D g2, String text, int x, int y, int maxW, int lineH) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int cy = y;
        for (String w : words) {
            String test = line + w + " ";
            if (fm.stringWidth(test) > maxW && line.length() > 0) {
                g2.drawString(line.toString().trim(), x, cy);
                line = new StringBuilder();
                cy += lineH;
            }
            line.append(w).append(" ");
        }
        if (line.length() > 0) g2.drawString(line.toString().trim(), x, cy);
    }

    /** Skip typewriter — reveal all text immediately. */
    public void skipTypewriter() {
        charsRevealed = fullText.length();
    }
}
