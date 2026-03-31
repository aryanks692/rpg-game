package object;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OBJ_Church extends SuperObject {
    private int pixelWidth, pixelHeight;
    public boolean doorLocked = true;

    public OBJ_Church(GamePanel gp) {
        super(gp);
        name = "Church";
        pixelWidth = gp.tileSize * 4;
        pixelHeight = gp.tileSize * 5;
        
        collisionBox = new Rectangle((pixelWidth / 2) - gp.tileSize/2, pixelHeight - gp.tileSize, gp.tileSize, gp.tileSize + 8);
        createImage();
    }

    private void createImage() {
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color stone = new Color(150, 150, 160);
        Color stoneDark = new Color(100, 100, 110);
        Color roofGrey = new Color(80, 80, 90);
        Color roofDark = new Color(50, 50, 60);
        
        // Main Hall (bottom 2.5 tiles)
        int hallY = pixelHeight - (int)(gp.tileSize * 2.5);
        int hallH = (int)(gp.tileSize * 2.5);
        int hallW = pixelWidth - 40;
        int hallX = 20;
        
        g2.setColor(stone);
        g2.fillRect(hallX, hallY, hallW, hallH);
        
        // Stone brick pattern
        g2.setColor(stoneDark);
        g2.setStroke(new BasicStroke(1));
        for(int y = hallY; y < pixelHeight; y += 12) {
            g2.drawLine(hallX, y, hallX + hallW, y);
            for(int x = hallX; x < hallX + hallW; x += 20) {
                int offset = ((y - hallY) / 12) % 2 == 0 ? 0 : 10;
                g2.drawLine(x + offset, y, x + offset, y + 12);
            }
        }

        // Tall Steeple Tower (center)
        int towerW = 60;
        int towerX = (pixelWidth / 2) - (towerW / 2);
        int towerY = 60; // roof top is above
        g2.setColor(stone);
        g2.fillRect(towerX, towerY, towerW, hallY - towerY);
        // Tower bricks
        g2.setColor(stoneDark);
        for(int y = towerY; y < hallY; y += 12) {
            g2.drawLine(towerX, y, towerX + towerW, y);
            for(int x = towerX; x < towerX + towerW; x += 15) {
                int offset = ((y - towerY) / 12) % 2 == 0 ? 0 : 7;
                g2.drawLine(x + offset, y, x + offset, y + 12);
            }
        }
        
        // Steeple Roof (triangle to top)
        int[] tx = {towerX - 10, pixelWidth / 2, towerX + towerW + 10};
        int[] ty = {towerY, 10, towerY};
        g2.setColor(roofGrey);
        g2.fillPolygon(tx, ty, 3);
        
        // Main Hall Roofs (triangles)
        // Huge triangle behind tower
        int[] rx = {hallX - 15, pixelWidth / 2, hallX + hallW + 15};
        int[] ry = {hallY + 20, hallY - 60, hallY + 20};
        g2.setColor(roofGrey);
        g2.fillPolygon(rx, ry, 3);
        g2.setColor(roofDark);
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolygon(rx, ry, 3);
        
        // Redraw tower body so it sits in front of the main roof!
        g2.setColor(stone);
        g2.fillRect(towerX, towerY, towerW, hallY - towerY);
        g2.setColor(stoneDark);
        g2.setStroke(new BasicStroke(1));
        for(int y = towerY; y < hallY; y += 12) {
            g2.drawLine(towerX, y, towerX + towerW, y);
            for(int x = towerX; x < towerX + towerW; x += 15) {
                int offset = ((y - towerY) / 12) % 2 == 0 ? 0 : 7;
                g2.drawLine(x + offset, y, x + offset, y + 12);
            }
        }
        // Spire cross
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(pixelWidth / 2, 0, pixelWidth / 2, 20);
        g2.drawLine(pixelWidth / 2 - 8, 10, pixelWidth / 2 + 8, 10);

        // Double Doors
        int doorW = 48;
        int doorH = 64;
        int doorX = (pixelWidth / 2) - (doorW / 2);
        int doorY = pixelHeight - doorH;
        g2.setColor(new Color(60, 30, 10));
        g2.fillArc(doorX, doorY - 10, doorW, 30, 0, 180); // Rounded top
        g2.fillRect(doorX, doorY + 5, doorW, doorH - 5);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(pixelWidth / 2, doorY - 10, pixelWidth / 2, pixelHeight); // divide
        g2.setColor(Color.YELLOW);
        g2.fillOval(pixelWidth / 2 - 10, doorY + 30, 6, 6);
        g2.fillOval(pixelWidth / 2 + 4, doorY + 30, 6, 6);

        // Awesome Stained Glass window
        int winW = 30;
        int winH = 40;
        int winX = pixelWidth / 2 - winW / 2;
        int winY = towerY + 30;
        Polygon glass = new Polygon();
        glass.addPoint(winX, winY + 20);
        glass.addPoint(winX + winW/2, winY);
        glass.addPoint(winX + winW, winY + 20);
        glass.addPoint(winX + winW, winY + winH);
        glass.addPoint(winX, winY + winH);
        g2.setColor(Color.CYAN);
        g2.fillPolygon(glass);
        g2.setColor(Color.MAGENTA);
        g2.fillPolygon(new int[]{winX+5, winX+winW/2, winX+winW-5}, new int[]{winY+25, winY+15, winY+25}, 3);
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(glass);

        // Side windows
        for(int side : new int[]{hallX + 15, hallX + hallW - 35}) {
            g2.setColor(new Color(150, 200, 255));
            g2.fillArc(side, hallY + 30, 20, 20, 0, 180);
            g2.fillRect(side, hallY + 40, 20, 30);
            g2.setColor(stoneDark);
            g2.drawArc(side, hallY + 30, 20, 20, 0, 180);
            g2.drawRect(side, hallY + 40, 20, 30);
            g2.drawLine(side + 10, hallY + 30, side + 10, hallY + 70);
            g2.drawLine(side, hallY + 55, side + 20, hallY + 55);
        }

        g2.dispose();
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;
        if (screenX + pixelWidth < 0 || screenX > gp.screenWidth) return;
        if (screenY + pixelHeight < 0 || screenY > gp.screenHeight) return;

        g2.drawImage(image, screenX, screenY, null);
    }

    @Override
    public void onPickup(entity.Player player) {
        if (doorLocked) {
           gp.ui.showNotification("The thick wooden doors are sealed shut.");
        }
    }
}
