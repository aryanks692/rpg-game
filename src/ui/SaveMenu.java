package ui;

import core.GamePanel;
import core.GameState;
import java.awt.*;

/**
 * 5-slot save/load menu with level, zone, and playtime previews.
 * Handles menu navigation and execution of save/load actions internally in update().
 */
public class SaveMenu {

    private final GamePanel gp;
    private int saveSlotCommandNum = 1;
    private boolean confirmingDelete = false;
    private int deleteSlot = -1;

    public SaveMenu(GamePanel gp) {
        this.gp = gp;
    }

    private String slotName(int slot) {
        return (slot == 0) ? "Auto-Save" : ("File " + slot);
    }

    public void update() {
        boolean isSaveMode = (gp.gameState == GameState.SAVE_MENU);
        int minSlot = isSaveMode ? 1 : 0;

        // --- Delete confirmation dialog (Yes / No) ---
        if (confirmingDelete) {
            if (gp.keyHandler.yesJustPressed) {
                gp.saveManager.deleteSave(deleteSlot);
                gp.ui.showNotification("Deleted " + slotName(deleteSlot) + ".");
                confirmingDelete = false;
                deleteSlot = -1;
                gp.keyHandler.clearJustPressed();
            } else if (gp.keyHandler.noJustPressed || gp.keyHandler.pauseJustPressed) {
                confirmingDelete = false;
                deleteSlot = -1;
                gp.keyHandler.clearJustPressed();
            }
            return; // freeze the menu while the dialog is open
        }

        if (gp.keyHandler.upPressed) {
            saveSlotCommandNum--;
            if (saveSlotCommandNum < minSlot) saveSlotCommandNum = 4;
            gp.keyHandler.upPressed = false;
        }
        if (gp.keyHandler.downPressed) {
            saveSlotCommandNum++;
            if (saveSlotCommandNum > 4) saveSlotCommandNum = minSlot;
            gp.keyHandler.downPressed = false;
        }

        // Delete the highlighted save file (asks for confirmation first)
        if (gp.keyHandler.deleteJustPressed) {
            if (gp.saveManager.hasSave(saveSlotCommandNum)) {
                confirmingDelete = true;
                deleteSlot = saveSlotCommandNum;
            } else {
                gp.ui.showNotification("Slot empty!");
            }
            gp.keyHandler.clearJustPressed();
            return;
        }

        if (gp.keyHandler.enterJustPressed) {
            if (isSaveMode) {
                gp.currentSaveSlot = saveSlotCommandNum;
                gp.saveManager.save(saveSlotCommandNum);
                gp.gameState = GameState.PLAY;
            } else {
                if (gp.saveManager.hasSave(saveSlotCommandNum)) {
                    gp.currentSaveSlot = saveSlotCommandNum;
                    gp.saveManager.load(saveSlotCommandNum);
                    gp.gameState = GameState.PLAY;
                } else {
                    gp.ui.showNotification("Slot empty!");
                }
            }
            gp.keyHandler.clearJustPressed();
        }
        if (gp.keyHandler.pauseJustPressed) {
            gp.gameState = (gp.player.alive && gp.playTimeTicks > 0) ? GameState.PLAY : GameState.TITLE;
            gp.keyHandler.clearJustPressed();
        }
    }

    public void draw(Graphics2D g2) {
        boolean isSaveMode = (gp.gameState == GameState.SAVE_MENU);

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(UIFonts.title(32f));
        g2.setColor(Color.WHITE);
        String title = isSaveMode ? "Save to which file?" : "Load from which file?";
        g2.drawString(title, gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2, 60);

        int slotX = gp.screenWidth / 2 - 250;
        int slotY = 90;
        int slotW = 500;
        int slotH = 75;

        int startSlot = isSaveMode ? 1 : 0;
        if (isSaveMode && saveSlotCommandNum == 0) saveSlotCommandNum = 1;

        for (int i = startSlot; i <= 4; i++) {
            if (i == saveSlotCommandNum) {
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRoundRect(slotX - 5, slotY - 5, slotW + 10, slotH + 10, 10, 10);
            }
            UIPanel.drawPanel(g2, slotX, slotY, slotW, slotH);

            g2.setFont(UIFonts.hud(18f));
            g2.setColor(Color.WHITE);
            g2.drawString(slotName(i), slotX + 20, slotY + 45);

            core.SaveManager.SaveInfo info = gp.saveManager.getSaveInfo(i);
            if (info.exists) {
                g2.setFont(UIFonts.small(16f));
                g2.setColor(new Color(220, 220, 220));
                g2.drawString("Lv: " + info.level, slotX + 130, slotY + 30);
                g2.drawString(info.zone, slotX + 130, slotY + 55);

                long secs = info.playTimeTicks / 60;
                String timeStr = String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
                g2.setFont(UIFonts.hud(16f));
                g2.drawString(timeStr, slotX + 400, slotY + 45);
            } else {
                g2.setColor(Color.GRAY);
                g2.setFont(UIFonts.small(16f));
                g2.drawString("[ Empty ]", slotX + 130, slotY + 45);
            }

            slotY += slotH + 15;
        }

        g2.setFont(UIFonts.small(12f));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("DEL: Delete selected file      ESC: Cancel", 20, gp.screenHeight - 20);

        if (confirmingDelete) {
            drawDeleteConfirm(g2);
        }
    }

    private void drawDeleteConfirm(Graphics2D g2) {
        UIPanel.drawOverlay(g2, gp.screenWidth, gp.screenHeight, new Color(0, 0, 0, 190));

        int pw = 490, ph = 190;
        int px = gp.screenWidth / 2 - pw / 2;
        int py = gp.screenHeight / 2 - ph / 2;
        UIPanel.drawPanel(g2, px, py, pw, ph);

        FontMetrics fm = g2.getFontMetrics();

        g2.setFont(UIFonts.title(24f));
        g2.setColor(new Color(255, 120, 120));
        String t1 = "Delete " + slotName(deleteSlot) + "?";
        g2.drawString(t1, gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(t1) / 2, py + 58);

        g2.setFont(UIFonts.small(15f));
        g2.setColor(new Color(225, 225, 225));
        String t2 = "Do you confirm deleting this save file?";
        g2.drawString(t2, gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(t2) / 2, py + 94);

        g2.setFont(UIFonts.hud(17f));
        fm = g2.getFontMetrics();
        int cy = py + 145;
        int spread = 70;
        String yes = "[Y] Yes";
        String no = "[N] No";
        g2.setColor(new Color(150, 240, 160));
        g2.drawString(yes, gp.screenWidth / 2 - spread - fm.stringWidth(yes) / 2, cy);
        g2.setColor(new Color(255, 160, 160));
        g2.drawString(no, gp.screenWidth / 2 + spread - fm.stringWidth(no) / 2, cy);
    }
}
