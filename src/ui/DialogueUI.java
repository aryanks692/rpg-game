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

        int boxH   = 124;
        int boxW   = gp.screenWidth - 40;
        int boxX   = 20;
        int boxY   = gp.screenHeight - boxH - 18;
        int portW  = 96;
        int portH  = boxH - 16;
        int frameX = boxX + 8;
        int frameY = boxY + 8;
        int textX  = boxX + portW + 22;

        // Main panel
        UIPanel.drawPanel(g2, boxX, boxY, boxW, boxH);

        // Portrait frame background
        g2.setColor(new Color(25, 20, 50));
        g2.fillRoundRect(frameX, frameY, portW, portH, 8, 8);
        
        if (entity.portraitImage != null) {
            // Draw photo portrait with high quality bicubic interpolation and aspect-ratio preservation
            Object oldInterp = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
            Object oldRender = g2.getRenderingHint(RenderingHints.KEY_RENDERING);
            Object oldAlpha  = g2.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            Shape oldClip = g2.getClip();
            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(frameX, frameY, portW, portH, 8, 8));

            int imgW = entity.portraitImage.getWidth();
            int imgH = entity.portraitImage.getHeight();
            // Scale to fill the portrait frame without distortion (cover)
            double scale = Math.max((double) portW / imgW, (double) portH / imgH);
            int drawW = (int) Math.round(imgW * scale);
            int drawH = (int) Math.round(imgH * scale);
            int drawX = frameX + (portW - drawW) / 2;
            // For tall character portraits, anchor to the top so the face/head is always fully visible
            int drawY = (drawH > portH) ? frameY : frameY + (portH - drawH) / 2;

            g2.drawImage(entity.portraitImage, drawX, drawY, drawW, drawH, null);
            g2.setClip(oldClip);

            // Restore hints
            if (oldInterp != null) g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterp);
            if (oldRender != null) g2.setRenderingHint(RenderingHints.KEY_RENDERING, oldRender);
            if (oldAlpha != null)  g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, oldAlpha);
        } else {
            // Letter placeholder fallback
            g2.setFont(UIFonts.HEADING);
            g2.setColor(UIConstants.COL_GOLD);
            String letter = entity.name.isEmpty() ? "?" : entity.name.substring(0, 1);
            FontMetrics fmP = g2.getFontMetrics();
            g2.drawString(letter,
                           frameX + portW / 2 - fmP.stringWidth(letter) / 2,
                           frameY + portH / 2 + fmP.getAscent() / 2);
        }

        g2.setStroke(UIConstants.STROKE_BORDER);
        g2.setColor(UIConstants.COL_PANEL_BORDER_DIM);
        g2.drawRoundRect(frameX, frameY, portW, portH, 8, 8);

        // Entity name
        g2.setFont(UIFonts.BODY_B);
        UIPanel.drawShadowText(g2, entity.name, UIFonts.BODY_B, UIConstants.COL_GOLD,
                                textX, boxY + 20);
        UIPanel.drawSeparator(g2, textX, boxY + 24, boxW - portW - 38);

        // Typewriter text (word-wrapped)
        g2.setFont(UIFonts.BODY);
        g2.setColor(UIConstants.COL_TEXT_MAIN);
        String visible = fullText.substring(0, Math.min(charsRevealed, fullText.length()));
        drawWrapped(g2, visible, textX, boxY + 42, boxW - portW - 42, 18);

        if (entity.awaitingChoice && entity.serviceCost > 0) {
            int baseCost = entity.serviceCost;
            int effCost = entity.getEffectiveServiceCost();
            int discount = 0;
            String tier = "New";
            int hpBonus = 25;
            if (gp.player != null) {
                discount = gp.player.getCreditDiscountPercent();
                tier = gp.player.getCreditTier();
                hpBonus = gp.player.getHpBonusPerPurchase();
            }
            int playerGold = (gp.player != null) ? gp.player.gold : 0;
            boolean canAfford = playerGold >= effCost;

            int offerBoxX = boxX + portW + 12;
            int offerBoxY = boxY + boxH - 64;
            int offerBoxW = boxW - portW - 32;
            int offerBoxH = 54;

            Color panelBg = new Color(40, 10, 25, 230);
            Color borderCol = new Color(255, 180, 80);
            if ("Gold".equals(tier)) { panelBg = new Color(50, 40, 10, 230); borderCol = new Color(255, 215, 80); }
            else if ("Platinum".equals(tier)) { panelBg = new Color(20, 25, 50, 230); borderCol = new Color(200, 220, 255); }

            g2.setColor(panelBg);
            g2.fillRoundRect(offerBoxX, offerBoxY, offerBoxW, offerBoxH, 10, 10);
            g2.setStroke(UIConstants.STROKE_BORDER);
            g2.setColor(borderCol);
            g2.drawRoundRect(offerBoxX, offerBoxY, offerBoxW, offerBoxH, 10, 10);

            g2.setFont(UIFonts.BODY_B);
            if (discount > 0 && effCost < baseCost) {
                g2.setColor(UIConstants.COL_TEXT_DIM);
                g2.drawString("\u2665 " + entity.serviceName + " — " + baseCost + "g",
                               offerBoxX + 12, offerBoxY + 18);
                FontMetrics fm = g2.getFontMetrics();
                int strW = fm.stringWidth("" + baseCost + "g");
                int priceX = offerBoxX + 12 + fm.stringWidth("\u2665 " + entity.serviceName + " — ");
                g2.setColor(new Color(255, 80, 80));
                g2.drawLine(priceX, offerBoxY + 14, priceX + strW, offerBoxY + 14);
                g2.setColor(UIConstants.COL_GOLD);
                g2.drawString("  " + effCost + " Gold (-" + discount + "%)",
                               offerBoxX + 12, offerBoxY + 18);
            } else {
                g2.setColor(UIConstants.COL_GOLD);
                g2.drawString("\u2665 " + entity.serviceName + " — " + effCost + " Gold",
                               offerBoxX + 12, offerBoxY + 18);
            }

            g2.setColor(canAfford ? new Color(180, 255, 180) : new Color(255, 120, 120));
            g2.setFont(UIFonts.SMALL);
            g2.drawString("Your Gold: " + playerGold, offerBoxX + 12, offerBoxY + 34);

            g2.setColor(new Color(160, 220, 180));
            g2.drawString("+ " + hpBonus + " HP", offerBoxX + 100, offerBoxY + 34);

            Color tierColor = UIConstants.COL_TEXT_DIM;
            if ("Platinum".equals(tier)) tierColor = new Color(200, 220, 255);
            else if ("Gold".equals(tier)) tierColor = new Color(255, 215, 80);
            else if ("Silver".equals(tier)) tierColor = new Color(200, 200, 210);
            else if ("Bronze".equals(tier)) tierColor = new Color(205, 127, 50);
            g2.setColor(tierColor);
            g2.drawString("\u2605 " + tier + " Member", offerBoxX + 12, offerBoxY + 48);

            if ((globalTimer / 22) % 2 == 0) {
                g2.setFont(UIFonts.SMALL_B);
                g2.setColor(new Color(255, 255, 150));
                g2.drawString("[Y] Accept   [N] Decline",
                               offerBoxX + offerBoxW - 165, offerBoxY + 48);
            }
        } else if (charsRevealed >= fullText.length() && (globalTimer / 22) % 2 == 0) {
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
