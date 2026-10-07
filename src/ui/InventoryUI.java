package ui;

import core.GamePanel;
import entity.Player;
import java.awt.*;
import java.util.List;

/** Inventory overlay with equipment slots, item grid, and stat summary. */
public class InventoryUI {

    private final GamePanel gp;
    private static final int SLOT_SIZE = 44;
    private static final int SLOT_GAP  = 6;

    public InventoryUI(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        // Dim world
        UIPanel.drawOverlay(g2, gp.screenWidth, gp.screenHeight, UIConstants.COL_OVERLAY);

        int pw = 460, ph = 340;
        int px = gp.screenWidth / 2 - pw / 2;
        int py = gp.screenHeight / 2 - ph / 2;
        UIPanel.drawPanel(g2, px, py, pw, ph);

        // Title
        UIPanel.drawShadowTextCentered(g2, "\u2694  INVENTORY", UIFonts.HEADING,
                                        UIConstants.COL_GOLD, px + pw / 2, py + 30);
        UIPanel.drawSeparator(g2, px + 16, py + 36, pw - 32);

        Player p = gp.player;

        // ── Stat summary row ──────────────────────────────────────────────────
        int sy = py + 52;
        drawStatChip(g2, px + 16,  sy, "Lv",    String.valueOf(p.level),    UIConstants.COL_GOLD);
        drawStatChip(g2, px + 80,  sy, "XP",    p.xp + "/" + p.xpToLevel,  UIConstants.COL_XP_FILL);
        drawStatChip(g2, px + 195, sy, "Gold",  String.valueOf(p.gold),     UIConstants.COL_GOLD);
        drawStatChip(g2, px + 285, sy, "ATK",   String.valueOf(p.attackDamage), UIConstants.COL_TEXT_BAD);
        drawStatChip(g2, px + 355, sy, "DEF",   String.valueOf(p.defense),  new Color(100,180,255));

        // ── Equipment slots ────────────────────────────────────────────────────
        int eqY = py + 82;
        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(UIConstants.COL_SILVER);
        g2.drawString("EQUIPMENT", px + 16, eqY);
        UIPanel.drawSeparator(g2, px + 16, eqY + 4, 100);

        drawEquipSlot(g2, px + 16,  eqY + 10, "\u2694", "Weapon",
                       p.hasWeapon ? "Iron Sword" : null, p.hasWeapon);
        drawEquipSlot(g2, px + 76,  eqY + 10, "\uD83D\uDEE1", "Shield",
                       p.hasShield ? "Iron Shield" : null, p.hasShield);
        drawEquipSlot(g2, px + 136, eqY + 10, "\uD83E\uDDEA", "Potions",
                       p.potionCount > 0 ? "x" + p.potionCount : null, p.potionCount > 0);

        // ── Item grid ─────────────────────────────────────────────────────────
        int gridY = eqY + 82;
        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(UIConstants.COL_SILVER);
        g2.drawString("ITEMS", px + 16, gridY);
        UIPanel.drawSeparator(g2, px + 16, gridY + 4, 60);

        List<String> inv = p.inventory;
        if (inv.isEmpty()) {
            g2.setFont(UIFonts.BODY);
            g2.setColor(UIConstants.COL_TEXT_DIM);
            g2.drawString("No items collected yet.", px + 16, gridY + 22);
        } else {
            int ix = px + 16;
            int iy = gridY + 12;
            int cols = (pw - 32) / (SLOT_SIZE + SLOT_GAP);
            for (int i = 0; i < inv.size() && i < 20; i++) {
                drawItemSlot(g2, ix, iy, inv.get(i));
                ix += SLOT_SIZE + SLOT_GAP;
                if ((i + 1) % cols == 0) { ix = px + 16; iy += SLOT_SIZE + SLOT_GAP; }
            }
        }

        // Close hint
        g2.setFont(UIFonts.SMALL);
        g2.setColor(UIConstants.COL_TEXT_DIM);
        g2.drawString("Press I to close", px + pw / 2 - 38, py + ph - 10);
    }

    private void drawStatChip(Graphics2D g2, int x, int y,
                               String label, String value, Color valCol) {
        int cw = 60, ch = 22;
        g2.setColor(new Color(20, 20, 50, 200));
        g2.fillRoundRect(x, y, cw, ch, 8, 8);
        g2.setStroke(UIConstants.STROKE_THIN);
        g2.setColor(UIConstants.COL_PANEL_BORDER_DIM);
        g2.drawRoundRect(x, y, cw, ch, 8, 8);
        g2.setFont(UIFonts.SMALL);
        g2.setColor(UIConstants.COL_TEXT_DIM);
        g2.drawString(label, x + 4, y + 12);
        g2.setFont(UIFonts.SMALL_B);
        g2.setColor(valCol);
        g2.drawString(value, x + 4, y + ch - 4);
    }

    private void drawEquipSlot(Graphics2D g2, int x, int y,
                                String icon, String slotLabel,
                                String itemName, boolean equipped) {
        int sz = 58;
        Color bg  = equipped ? new Color(20, 40, 20, 200) : new Color(20, 20, 35, 200);
        Color brd = equipped ? UIConstants.COL_TEXT_GOOD  : UIConstants.COL_TEXT_DIM;
        g2.setColor(bg);
        g2.fillRoundRect(x, y, sz, sz, 10, 10);
        g2.setStroke(UIConstants.STROKE_THIN);
        g2.setColor(brd);
        g2.drawRoundRect(x, y, sz, sz, 10, 10);

        // Icon
        g2.setFont(UIFonts.HEADING);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(equipped ? UIConstants.COL_GOLD : UIConstants.COL_TEXT_DIM);
        g2.drawString(icon, x + sz/2 - fm.stringWidth(icon)/2, y + 26);

        // Slot label
        g2.setFont(UIFonts.small(9));
        g2.setColor(UIConstants.COL_TEXT_DIM);
        fm = g2.getFontMetrics();
        g2.drawString(slotLabel, x + sz/2 - fm.stringWidth(slotLabel)/2, y + 38);

        if (itemName != null) {
            g2.setFont(UIFonts.small(9));
            g2.setColor(UIConstants.COL_TEXT_GOOD);
            fm = g2.getFontMetrics();
            g2.drawString(itemName, x + sz/2 - fm.stringWidth(itemName)/2, y + 52);
        }
    }

    private void drawItemSlot(Graphics2D g2, int x, int y, String name) {
        g2.setColor(new Color(18, 18, 45, 210));
        g2.fillRoundRect(x, y, SLOT_SIZE, SLOT_SIZE, 8, 8);
        g2.setStroke(UIConstants.STROKE_THIN);
        g2.setColor(UIConstants.COL_PANEL_BORDER_DIM);
        g2.drawRoundRect(x, y, SLOT_SIZE, SLOT_SIZE, 8, 8);

        // Item name (truncated)
        String display = name.length() > 7 ? name.substring(0, 6) + "." : name;
        g2.setFont(UIFonts.small(9));
        g2.setColor(UIConstants.COL_TEXT_MAIN);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(display, x + SLOT_SIZE/2 - fm.stringWidth(display)/2,
                       y + SLOT_SIZE/2 + fm.getAscent()/2);
    }
}
