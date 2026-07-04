package ui.menu;

import core.GamePanel;
import ui.components.UIPanel;
import java.awt.*;

public class SaveLoadMenu {

    private Font titleFont = new Font("Georgia", Font.BOLD, 48);
    private Font hudFont = new Font("Arial", Font.BOLD, 13);
    private Font smallFont = new Font("Arial", Font.PLAIN, 11);

    public void draw(Graphics2D g2, GamePanel gp, boolean isSaveMode, int saveSlotCommandNum) {
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        g2.setFont(titleFont.deriveFont(32f));
        g2.setColor(Color.WHITE);
        String title = isSaveMode ? "Save to which file?" : "Load from which file?";
        g2.drawString(title, gp.screenWidth/2 - g2.getFontMetrics().stringWidth(title)/2, 60);
        
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
            UIPanel.draw(g2, slotX, slotY, slotW, slotH);
            
            g2.setFont(hudFont.deriveFont(18f));
            g2.setColor(Color.WHITE);
            String slotName = (i == 0) ? "Auto-Save" : "File " + i;
            g2.drawString(slotName, slotX + 20, slotY + 45);
            
            core.SaveManager.SaveInfo info = gp.saveManager.getSaveInfo(i);
            if (info.exists) {
                g2.setFont(smallFont.deriveFont(16f));
                g2.setColor(new Color(220, 220, 220));
                g2.drawString("Lv: " + info.level, slotX + 130, slotY + 30);
                g2.drawString(info.zone, slotX + 130, slotY + 55);
                
                long secs = info.playTimeTicks / 60;
                String timeStr = String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
                g2.setFont(hudFont.deriveFont(16f));
                g2.drawString(timeStr, slotX + 400, slotY + 45);
            } else {
                g2.setColor(Color.GRAY);
                g2.setFont(smallFont.deriveFont(16f));
                g2.drawString("[ Empty ]", slotX + 130, slotY + 45);
            }
            
            slotY += slotH + 15;
        }
        
        g2.setFont(smallFont.deriveFont(12f));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Press ESC to cancel", 20, gp.screenHeight - 20);
    }
}
