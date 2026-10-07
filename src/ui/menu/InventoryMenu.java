package ui.menu;

import core.GamePanel;
import entity.Player;
import ui.components.UIPanel;
import java.awt.*;

public class InventoryMenu {

    private Font hudFont = new Font("Arial", Font.BOLD, 13);
    private Font smallFont = new Font("Arial", Font.PLAIN, 11);

    public void draw(Graphics2D g2, GamePanel gp) {
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        int pw = 420, ph = 320;
        int px = gp.screenWidth / 2 - pw / 2, py = gp.screenHeight / 2 - ph / 2;
        UIPanel.draw(g2, px, py, pw, ph);

        g2.setFont(new Font("Georgia", Font.BOLD, 22));
        g2.setColor(new Color(255, 220, 80));
        g2.drawString("⚔  INVENTORY", px + 130, py + 30);

        Player p = gp.player;
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Gold: " + p.gold, px + 20, py + 56);
        g2.drawString("Level: " + p.level + "  XP: " + p.xp + "/" + p.xpToLevel, px + 120, py + 56);

        // Equipment
        g2.setColor(new Color(180, 180, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("Equipment:", px + 20, py + 80);
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Weapon:  " + (p.hasWeapon ? "Iron Sword (ATK +" + p.attackDamage + ")" : "Fists"), px + 20, py + 98);
        g2.drawString("Shield:  " + (p.hasShield ? "Iron Shield (DEF +" + p.defense + ")" : "None"), px + 20, py + 116);
        g2.drawString("Potions: " + p.potionCount + "  (Press ENTER in game to use)", px + 20, py + 134);

        // Inventory items grid
        g2.setColor(new Color(180, 180, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("Items:", px + 20, py + 158);

        if (p.inventory.isEmpty()) {
            g2.setColor(new Color(120, 120, 180));
            g2.setFont(hudFont);
            g2.drawString("Empty", px + 20, py + 176);
        } else {
            int ix = px + 20, iy = py + 176;
            for (int i = 0; i < p.inventory.size() && i < 18; i++) {
                g2.setColor(new Color(40, 40, 80));
                g2.fillRoundRect(ix, iy - 16, 80, 20, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(smallFont);
                g2.drawString(p.inventory.get(i), ix + 4, iy - 2);
                ix += 85;
                if (ix > px + pw - 90) { ix = px + 20; iy += 26; }
            }
        }

        g2.setFont(smallFont);
        g2.setColor(new Color(160, 160, 200));
        g2.drawString("Press I to close", px + pw / 2 - 40, py + ph - 10);
    }
}
