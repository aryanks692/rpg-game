package tile.region;

import core.GamePanel;
import tile.Tile;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Region {
    protected GamePanel gp;

    public Region(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Registers the tiles specific to this region into the master tile array.
     */
    public abstract void registerTiles(Tile[] tiles);

    // Shared helper methods for generating tile graphics

    protected Color br(Color c, int n) {
        return new Color(Math.min(255, c.getRed() + n), Math.min(255, c.getGreen() + n), Math.min(255, c.getBlue() + n));
    }

    protected Color dk(Color c, int n) {
        return new Color(Math.max(0, c.getRed() - n), Math.max(0, c.getGreen() - n), Math.max(0, c.getBlue() - n));
    }

    protected BufferedImage createIndieBrick(Color face, Color mortar, boolean light) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(mortar); 
        g.fillRect(0, 0, ts, ts);
        
        int bh = 16, bw = 24;
        for (int row = 0; row < ts / bh; row++) {
            int y = row * bh; 
            int xOff = (row % 2 == 0) ? 0 : bw / 2;
            for (int x = -bw / 2 + xOff; x < ts + bw; x += bw) {
                int bx = x + 1, by = y + 1, dw = Math.min(bw - 2, ts - bx);
                if (bx < ts && dw > 0) {
                    g.setColor(face); 
                    g.fillRect(bx, by, dw, bh - 2);
                    if (light) { 
                        g.setColor(br(face, 22)); 
                        g.fillRect(bx, by, dw, 2); 
                    }
                    g.setColor(dk(face, 18)); 
                    g.fillRect(bx, by + bh - 4, dw, 2);
                }
            }
        }
        g.dispose(); 
        return img;
    }
}
