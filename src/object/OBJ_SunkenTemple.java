package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Sunken classical temple sanctuary resting in the lake waters of Ancient Ruins.
 */
public class OBJ_SunkenTemple extends SuperObject {
    private int pixelWidth, pixelHeight;

    public OBJ_SunkenTemple(GamePanel gp) {
        super(gp);
        this.name = "Sunken Sanctuary";
        this.pixelWidth = gp.tileSize * 3;
        this.pixelHeight = gp.tileSize * 3;
        this.collision = true;

        // Collision box for the temple podium
        collisionBox = new Rectangle(12, 40, pixelWidth - 24, pixelHeight - 48);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color roofRose     = new Color(175, 115, 105);
        Color roofDark     = new Color(130, 80, 70);
        Color stoneLight   = new Color(215, 210, 198);
        Color stoneMid     = new Color(175, 170, 158);
        Color stoneDark    = new Color(120, 115, 105);
        Color doorwayDark  = new Color(32, 28, 36);
        Color mossGreen    = new Color(75, 125, 65);
        Color waterCyan    = new Color(100, 190, 230, 150);

        int baseX = 12;
        int baseY = pixelHeight - 28;
        int baseW = pixelWidth - 24;

        // 1. Submerged Water Ripples at the Base
        g2.setColor(waterCyan);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(baseX - 6, baseY - 6, baseW + 12, 28, 12, 12);
        g2.setColor(new Color(255, 255, 255, 130));
        g2.drawRoundRect(baseX - 4, baseY - 4, baseW + 8, 24, 10, 10);
        g2.setStroke(new BasicStroke(1f));

        // 2. Stone Foundation Steps (Podium)
        g2.setColor(stoneDark);
        g2.fillRoundRect(baseX, baseY - 12, baseW, 20, 6, 6);
        g2.setColor(stoneMid);
        g2.fillRoundRect(baseX + 6, baseY - 18, baseW - 12, 14, 4, 4);

        // 3. Dark Temple Interior Portal
        int portalY = 56;
        int portalH = baseY - 18 - portalY;
        g2.setColor(doorwayDark);
        g2.fillRect(baseX + 16, portalY, baseW - 32, portalH);

        // 4. Classical Pillars (4 colonnade columns)
        int numPillars = 4;
        int pillarW = 12;
        int spacing = (baseW - 24 - (numPillars * pillarW)) / (numPillars - 1);
        for (int i = 0; i < numPillars; i++) {
            int px = baseX + 12 + i * (pillarW + spacing);
            // Column body
            g2.setColor(stoneLight);
            g2.fillRect(px, portalY - 2, pillarW, portalH + 2);
            // Fluting / shading
            g2.setColor(stoneDark);
            g2.drawLine(px, portalY, px, portalY + portalH);
            g2.drawLine(px + pillarW - 1, portalY, px + pillarW - 1, portalY + portalH);
            // Capital & base
            g2.setColor(stoneMid);
            g2.fillRect(px - 2, portalY - 4, pillarW + 4, 4);
            g2.fillRect(px - 2, portalY + portalH - 3, pillarW + 4, 4);
        }

        // 5. Entablature (Horizontal stone beam above pillars)
        g2.setColor(stoneDark);
        g2.fillRect(baseX + 4, portalY - 10, baseW - 8, 8);
        g2.setColor(stoneLight);
        g2.fillRect(baseX + 6, portalY - 9, baseW - 12, 4);

        // 6. Triangular Pediment Roof
        int roofPeakX = pixelWidth / 2;
        int roofPeakY = 16;
        int roofLeftX = baseX - 2;
        int roofRightX = baseX + baseW + 2;
        int roofBottomY = portalY - 10;

        Polygon roof = new Polygon();
        roof.addPoint(roofPeakX, roofPeakY);
        roof.addPoint(roofRightX, roofBottomY);
        roof.addPoint(roofLeftX, roofBottomY);

        g2.setColor(roofRose);
        g2.fillPolygon(roof);

        // Roof border & pediment trim
        g2.setColor(roofDark);
        g2.setStroke(new BasicStroke(2f));
        g2.drawPolygon(roof);

        // Tympanum inner triangle
        Polygon innerTympanum = new Polygon();
        innerTympanum.addPoint(roofPeakX, roofPeakY + 10);
        innerTympanum.addPoint(roofRightX - 12, roofBottomY - 4);
        innerTympanum.addPoint(roofLeftX + 12, roofBottomY - 4);
        g2.setColor(stoneDark);
        g2.fillPolygon(innerTympanum);

        // Relief medallion in pediment
        g2.setColor(new Color(235, 195, 80));
        g2.fillOval(roofPeakX - 5, roofPeakY + 16, 10, 10);

        // 7. Cracks & Overgrown Ivy
        g2.setColor(mossGreen);
        g2.fillOval(baseX + 14, portalY + 8, 14, 8);
        g2.fillOval(baseX + baseW - 28, portalY + 18, 12, 10);
        g2.fillOval(roofLeftX + 18, roofBottomY - 14, 16, 8);

        // Crack on roof
        g2.setColor(doorwayDark);
        g2.drawLine(roofPeakX + 8, roofPeakY + 8, roofPeakX + 16, roofPeakY + 22);
        g2.drawLine(roofPeakX + 16, roofPeakY + 22, roofPeakX + 24, roofBottomY - 6);

        g2.dispose();
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.ui.showNotification("Sunken Sanctuary — ancient guardian temple.");
    }
}
