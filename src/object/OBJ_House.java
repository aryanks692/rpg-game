package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_House extends SuperObject {
    private int pixelWidth, pixelHeight;
    public boolean doorLocked = true;

    public OBJ_House(GamePanel gp) {
        super(gp);
        name = "House";
        // 3 tiles wide, 3 tiles high
        pixelWidth = gp.tileSize * 3;
        pixelHeight = gp.tileSize * 3;
        
        // Custom collision box for the interactable door area (front center)
        collisionBox = new Rectangle((pixelWidth / 2) - gp.tileSize/2, pixelHeight - gp.tileSize, gp.tileSize, gp.tileSize + 8);

        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Anti-aliasing creates a softer, blended 32-bit aesthetic
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // HD RPG Color Palette
        Color plasterWall = new Color(245, 235, 220); // Warm white
        Color plasterShadow = new Color(215, 205, 190);
        Color woodBeam = new Color(130, 80, 50); // Rich brown
        Color woodShadow = new Color(90, 50, 30);
        Color roofShingle = new Color(180, 60, 50); // Deep Stardew red
        Color roofDark = new Color(140, 40, 30);
        Color doorTeal = new Color(70, 140, 140); 
        Color doorDark = new Color(40, 100, 100);

        int houseW = pixelWidth - 20;
        int houseH = pixelHeight - 10;
        int startX = 10;
        int startY = 10;
        
        // --- WALLS (Plaster) ---
        int wallY = houseH / 2 - 10;
        int wallH = houseH - wallY + 10;
        
        // Base plaster
        g2.setColor(plasterWall);
        g2.fillRect(startX, wallY, houseW, wallH);
        
        // Plaster shadowing (blended shading at the top under roof)
        GradientPaint wallShade = new GradientPaint(0, wallY, plasterShadow, 0, wallY + 30, plasterWall);
        g2.setPaint(wallShade);
        g2.fillRect(startX, wallY, houseW, wallH);

        // --- WOODEN FRAMING (HD Timber) ---
        g2.setColor(woodBeam);
        // Left pillar
        g2.fillRect(startX, wallY, 14, wallH);
        // Right pillar
        g2.fillRect(startX + houseW - 14, wallY, 14, wallH);
        // Middle pillar
        g2.fillRect(startX + houseW / 2 - 7, wallY, 14, wallH);

        // Horizontal bottom beam
        g2.fillRect(startX, wallY + wallH - 14, houseW, 14);
        
        // Add wood grain texture lines
        g2.setColor(woodShadow);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(startX + 4, wallY, startX + 4, wallY + wallH);
        g2.drawLine(startX + 10, wallY, startX + 10, wallY + wallH);
        g2.drawLine(startX + houseW - 10, wallY, startX + houseW - 10, wallY + wallH);

        // --- DOOR (Antique Teal) ---
        int doorW = 34;
        int doorH = 46;
        int doorX = startX + houseW / 2 - doorW / 2; // Centered between beams
        int doorY = wallY + wallH - doorH - 4; // Above bottom beam
        
        g2.setColor(doorTeal);
        g2.fillRect(doorX, doorY, doorW, doorH);
        
        // Vertical teal door planks
        g2.setColor(doorDark);
        for (int i = 0; i < doorW; i += 8) {
            g2.drawLine(doorX + i, doorY, doorX + i, doorY + doorH);
        }
        
        // Door Window
        g2.setColor(new Color(40, 40, 50)); // Dark glass
        g2.fillRect(doorX + 6, doorY + 6, doorW - 12, 14);
        // Door Handle
        g2.setColor(new Color(180, 180, 190)); // Iron handle
        g2.fillRect(doorX + doorW - 10, doorY + doorH / 2, 4, 8);

        // --- WINDOWS ---
        int winW = 28;
        int winH = 34;
        
        // Left window
        drawHDWindow(g2, startX + 26, wallY + 20, winW, winH);
        // Right window
        drawHDWindow(g2, startX + houseW - 26 - winW, wallY + 20, winW, winH);

        // --- ROOF (HD Shingles) ---
        int roofY = 5;
        int roofH = wallY - roofY + 20; 
        
        // Main roof block overlapping walls
        g2.setColor(roofShingle);
        g2.fillRect(startX - 10, roofY, houseW + 20, roofH);

        // Draw overlapping shingles
        g2.setColor(roofDark);
        for (int y = roofY; y < roofY + roofH; y += 12) {
            g2.drawLine(startX - 10, y, startX + houseW + 10, y); // Horizontal lap
            
            // Vertical staggered slits
            int xOffset = ((y - roofY) / 12) % 2 == 0 ? 0 : 8;
            for (int x = startX - 10 + xOffset; x < startX + houseW + 10; x += 16) {
                g2.fillRect(x, y, 2, 12);
            }
        }
        
        // Roof overhang shading (ambient occlusion)
        GradientPaint roofDrop = new GradientPaint(0, roofY + roofH, new Color(0,0,0,100), 0, roofY + roofH + 10, new Color(0,0,0,0));
        g2.setPaint(roofDrop);
        g2.fillRect(startX, roofY + roofH, houseW, 10);

        g2.dispose();
    }

    private void drawHDWindow(Graphics2D g2, int x, int y, int w, int h) {
        // Wooden frame
        g2.setColor(new Color(110, 60, 40));
        g2.fillRect(x - 4, y - 4, w + 8, h + 8);
        
        // Window glass
        g2.setColor(new Color(120, 180, 200));
        g2.fillRect(x, y, w, h);
        
        // Glass gradient (ambient reflection)
        GradientPaint glassRef = new GradientPaint(x, y, new Color(255,255,255,80), x + w/2, y + h, new Color(255,255,255,0));
        g2.setPaint(glassRef);
        g2.fillRect(x, y, w, h);
        
        // Wooden crossbars
        g2.setColor(new Color(110, 60, 40));
        g2.fillRect(x + w / 2 - 2, y, 4, h); // Vertical
        g2.fillRect(x, y + h / 2 - 2, w, 4); // Horizontal
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;
        
        // Cull based on pixel size
        if (screenX + pixelWidth < 0 || screenX > gp.screenWidth) return;
        if (screenY + pixelHeight < 0 || screenY > gp.screenHeight) return;

        g2.drawImage(image, screenX, screenY, null);
    }

    @Override
    public void onPickup(entity.Player player) {
        if (doorLocked) {
           gp.ui.showNotification("It is locked.");
        }
    }
}
