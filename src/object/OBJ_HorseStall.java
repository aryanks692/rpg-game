package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Horse Stall — a stable in Dusty Gulch. Interacting with the door takes the
 * player inside, where the Stable Owner sells a Horse for 100 gold. A horse can
 * only ever be owned once.
 */
public class OBJ_HorseStall extends SuperObject {
    public static final int HORSE_PRICE = 100;

    private int pixelWidth, pixelHeight;

    public OBJ_HorseStall(GamePanel gp) {
        super(gp);
        name = "Horse Stall";
        pixelWidth = gp.tileSize * 3;
        pixelHeight = gp.tileSize * 3;
        collisionBox = new Rectangle(8, 12, pixelWidth - 16, pixelHeight - 16);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color woodBody = new Color(150, 104, 62);
        Color woodDark = new Color(88, 56, 30);
        Color roofDark = new Color(58, 38, 24);
        Color roofLight = new Color(96, 66, 42);
        Color hay      = new Color(226, 196, 96);
        Color horse    = new Color(120, 78, 44);
        Color horseDk  = new Color(74, 48, 26);
        Color gold     = new Color(245, 205, 75);

        int bX = 6, bY = 14, bW = pixelWidth - 12, bH = pixelHeight - 18;
        int midX = bX + bW / 2;

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 85));
        g.fillRoundRect(bX - 4, pixelHeight - 12, bW + 8, 10, 8, 8);

        // --- Walls (vertical planks) ---
        g.setColor(woodBody);
        g.fillRect(bX, bY, bW, bH);
        g.setColor(woodDark);
        for (int x = bX; x < bX + bW; x += 11) g.drawLine(x, bY, x, bY + bH);

        // --- Gabled roof ---
        int roofH = 24;
        g.setColor(roofDark);
        g.fillPolygon(new int[]{bX - 6, midX, bX + bW + 6}, new int[]{bY + 4, bY - roofH, bY + 4}, 3);
        g.setColor(roofLight);
        g.fillPolygon(new int[]{bX - 2, midX, bX + bW + 2}, new int[]{bY + 2, bY - roofH + 8, bY + 2}, 3);

        // --- Signboard "HORSE STALL" ---
        g.setColor(new Color(40, 25, 15));
        g.fillRoundRect(midX - 52, bY + 8, 104, 18, 5, 5);
        g.setColor(gold);
        g.drawRoundRect(midX - 52, bY + 8, 104, 18, 5, 5);
        g.setFont(new Font("Georgia", Font.BOLD, 10));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("HORSE STALL", midX - fm.stringWidth("HORSE STALL") / 2, bY + 21);

        // --- Stall openings ---
        int opW = 40, opH = 46, opY = bY + 34;
        int opX1 = bX + 12;
        int opX2 = bX + bW - opW - 12;
        drawStallOpening(g, opX1, opY, opW, opH, woodDark);
        drawStallOpening(g, opX2, opY, opW, opH, woodDark);

        // --- Horse peeking from the right opening ---
        drawHorse(g, opX2 + opW / 2, opY + opH, horse, horseDk);

        // --- Hay bale in left opening ---
        g.setColor(hay);
        g.fillRoundRect(opX1 + 6, opY + opH - 18, opW - 12, 16, 5, 5);
        g.setColor(new Color(190, 160, 70));
        for (int i = 0; i < 4; i++) g.drawLine(opX1 + 8, opY + opH - 15 + i * 4, opX1 + opW - 10, opY + opH - 15 + i * 4);

        // --- Horseshoe emblem ---
        g.setColor(gold);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(midX - 9, bY + 40, 18, 22, 20, 320);

        // --- Stable door (below the sign) ---
        int doorW = 30, doorH = 40, doorX = midX - doorW / 2, doorY = bY + bH - doorH;
        g.setColor(woodDark);
        g.fillRect(doorX, doorY, doorW, doorH);
        g.setColor(new Color(112, 74, 44));
        g.fillRect(doorX + 2, doorY + 2, doorW - 4, doorH - 4);
        g.setColor(woodDark);
        g.drawLine(doorX + 2, doorY + doorH / 2, doorX + doorW - 2, doorY + doorH / 2);
        g.setColor(gold);
        g.fillOval(doorX + doorW - 8, doorY + doorH / 2 - 4, 4, 4);

        g.dispose();
    }

    private void drawStallOpening(Graphics2D g, int x, int y, int w, int h, Color frame) {
        g.setColor(new Color(28, 18, 10));
        g.fillRect(x, y, w, h);
        g.setColor(frame);
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, y, w, h);
        g.drawLine(x + w / 2, y, x + w / 2, y + h); // half-door split
    }

    private void drawHorse(Graphics2D g, int cx, int baseY, Color body, Color dark) {
        // Neck
        g.setColor(body);
        g.fillRoundRect(cx - 7, baseY - 34, 14, 34, 6, 6);
        // Head
        g.fillRoundRect(cx - 9, baseY - 48, 18, 20, 7, 7);
        // Muzzle
        g.setColor(dark);
        g.fillRoundRect(cx - 5, baseY - 34, 10, 10, 5, 5);
        // Ears
        g.setColor(body);
        g.fillPolygon(new int[]{cx - 8, cx - 5, cx - 2}, new int[]{baseY - 46, baseY - 56, baseY - 46}, 3);
        g.fillPolygon(new int[]{cx + 2, cx + 5, cx + 8}, new int[]{baseY - 46, baseY - 56, baseY - 46}, 3);
        // Mane
        g.setColor(dark);
        g.fillRect(cx + 5, baseY - 46, 4, 30);
        // Eye
        g.setColor(Color.WHITE);
        g.fillOval(cx - 6, baseY - 44, 3, 3);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 5, baseY - 43, 2, 2);
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;

        if (screenX + pixelWidth > 0 && screenX < gp.screenWidth &&
            screenY + pixelHeight > 0 && screenY < gp.screenHeight) {

            g2.drawImage(image, screenX, screenY, null);

            int px = gp.player.worldX + gp.player.width / 2;
            int py = gp.player.worldY + gp.player.height / 2;
            // Show the prompt when the player stands on the tiles just below the door.
            int pcol = px / gp.tileSize;
            int prow = py / gp.tileSize;
            int doorCol = worldX / gp.tileSize;
            boolean atDoor = prow >= 22 && prow <= 23 && pcol >= doorCol && pcol <= doorCol + 2;

            if (atDoor) {
                String label = "[E] Enter Horse Stall";
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(screenX + pixelWidth / 2 - 60, screenY + pixelHeight - 20, 120, 18, 6, 6);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.setColor(Color.YELLOW);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, screenX + pixelWidth / 2 - fm.stringWidth(label) / 2, screenY + pixelHeight - 7);
            }
        }
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.enterHorseStall();
    }
}
