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

    public SaveMenu(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        boolean isSaveMode = (gp.gameState == GameState.SAVE_MENU);
        int minSlot = isSaveMode ? 1 : 0;

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
            String slotName = (i == 0) ? "Auto-Save" : "File " + i;
            g2.drawString(slotName, slotX + 20, slotY + 45);

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
        g2.drawString("Press ESC to cancel", 20, gp.screenHeight - 20);
    }
}
