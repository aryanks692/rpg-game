package tile.region;

import core.GamePanel;
import tile.Tile;
import tile.TileManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SavannahRegion extends Region {

    public SavannahRegion(GamePanel gp) {
        super(gp);
    }

    @Override
    public void registerTiles(Tile[] tiles) {
        tiles[TileManager.SAVANNAH_GRASS] = new Tile(); 
        tiles[TileManager.SAVANNAH_GRASS].image = createIndieSavannahGrass();
        
        tiles[TileManager.SAVANNAH_TREE] = new Tile(); 
        tiles[TileManager.SAVANNAH_TREE].image = createIndieAcaciaTree(); 
        tiles[TileManager.SAVANNAH_TREE].collision = true;
    }

    private BufferedImage createIndieSavannahGrass() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Color sand1 = new Color(214, 188, 110);
        Color sand2 = new Color(196, 170, 92);
        Color sand3 = new Color(176, 150, 76);
        g.setColor(sand1); g.fillRect(0, 0, ts, ts);
        for (int y = 0; y < ts; y += 2) {
            for (int x = 0; x < ts; x += 2) {
                int rand = (x * 13 + y * 7 + (x * y)) % 3;
                if (rand == 0) g.setColor(sand2);
                else if (rand == 1) g.setColor(sand1);
                else g.setColor(sand3);
                g.fillRect(x, y, 2, 2);
            }
        }
        Color grassDark = new Color(120, 102, 46);
        Color grassLight = new Color(158, 138, 68);
        for (int ty = 6; ty < ts; ty += 14) {
            for (int tx = 6; tx < ts; tx += 14) {
                g.setColor(grassDark);
                g.drawLine(tx, ty + 6, tx - 2, ty);
                g.drawLine(tx + 1, ty + 6, tx, ty - 1);
                g.drawLine(tx + 2, ty + 6, tx + 2, ty);
                g.drawLine(tx + 4, ty + 6, tx + 6, ty);
                g.drawLine(tx + 5, ty + 6, tx + 7, ty + 1);
                g.drawLine(tx + 3, ty + 6, tx + 3, ty - 1);
                g.setColor(grassLight);
                g.drawLine(tx + 2, ty + 5, tx + 2, ty + 1);
                g.drawLine(tx + 3, ty + 5, tx + 4, ty + 1);
                g.setColor(new Color(90, 76, 32, 100));
                g.fillRect(tx, ty + 6, 6, 1);
            }
        }
        g.dispose(); return img;
    }

    private BufferedImage createIndieAcaciaTree() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(createIndieSavannahGrass(), 0, 0, null);
        g.setColor(new Color(0, 0, 0, 90)); g.fillOval(ts / 2 - 22, ts - 14, 44, 12);
        Color t1 = new Color(50, 35, 25); int tx = ts / 2; int ty = ts - 4;
        g.setStroke(new BasicStroke(3)); g.setColor(t1); g.drawLine(tx, ty, tx, ty - 12);
        g.drawLine(tx, ty - 12, tx - 12, ty - 22); g.drawLine(tx, ty - 12, tx + 12, ty - 22);
        int cx = 2, cy = 8, cw = ts - 4, ch = 14;
        g.setColor(new Color(45, 65, 20)); g.fillRoundRect(cx, cy + 4, cw, ch, 15, 15);
        g.setColor(new Color(110, 140, 50)); g.fillRoundRect(cx, cy, cw, ch, 15, 15);
        g.dispose(); return img;
    }
}
