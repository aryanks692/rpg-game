package tile.region;

import core.GamePanel;
import tile.Tile;
import tile.TileManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TownRegion extends Region {

    public TownRegion(GamePanel gp) {
        super(gp);
    }

    @Override
    public void registerTiles(Tile[] tiles) {
        tiles[TileManager.DIRT] = new Tile(); 
        tiles[TileManager.DIRT].image = createIndieCobblestone(new Color(140, 105, 68), new Color(95, 72, 46), false);
        
        tiles[TileManager.STONE] = new Tile(); 
        tiles[TileManager.STONE].image = createIndieBrick(new Color(125, 122, 135), new Color(80, 78, 90), true);
        tiles[TileManager.STONE].collision = true;
        
        tiles[TileManager.WATER_A] = new Tile(); 
        tiles[TileManager.WATER_A].animated = true;
        tiles[TileManager.WATER_A].frames = new BufferedImage[]{createIndieWater(0.0f), createIndieWater(0.5f)};
        tiles[TileManager.WATER_A].image = tiles[TileManager.WATER_A].frames[0]; 
        tiles[TileManager.WATER_A].collision = true;
        
        tiles[TileManager.WATER_B] = tiles[TileManager.WATER_A];
        
        tiles[TileManager.WALL] = new Tile(); 
        tiles[TileManager.WALL].image = createIndieBrick(new Color(70, 68, 82), new Color(45, 43, 55), true);
        tiles[TileManager.WALL].collision = true;
        
        tiles[TileManager.SAND] = new Tile(); 
        tiles[TileManager.SAND].image = createIndieSand();
        
        tiles[TileManager.PATH] = new Tile(); 
        tiles[TileManager.PATH].image = createWoodPathTile();
    }

    private BufferedImage createIndieCobblestone(Color stone, Color mortar, boolean rounded) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(mortar); 
        g.fillRect(0, 0, ts, ts);
        if (rounded) {
            int[][] ss = {{1, 1, 23, 15}, {25, 1, 22, 15}, {1, 17, 14, 14}, {17, 17, 30, 14}};
            for (int[] s : ss) {
                g.setColor(stone); 
                g.fillRoundRect(s[0], s[1], s[2], s[3], 8, 8);
            }
        } else {
            g.setColor(stone); 
            g.fillRect(0, 0, ts, ts);
            g.setColor(mortar); 
            for (int y = 11; y < ts; y += 12) {
                g.drawLine(0, y, ts - 1, y);
            }
        }
        g.dispose(); 
        return img;
    }

    private BufferedImage createIndieWater(float phase) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        GradientPaint grad = new GradientPaint(0, 0, new Color(70, 155, 240), 0, ts, new Color(28, 88, 200));
        g.setPaint(grad); 
        g.fillRect(0, 0, ts, ts);
        int wo = (int)(phase * 12) % 16;
        g.setColor(new Color(110, 185, 255, 130)); 
        g.setStroke(new BasicStroke(2));
        for (int y = 6; y < ts; y += 12) {
            for (int x = -16 + wo; x < ts + 16; x += 16) {
                g.drawArc(x, y - 3, 12, 8, 0, 180);
            }
        }
        g.dispose(); 
        return img;
    }

    private BufferedImage createWoodPathTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Color base = new Color(155, 115, 75);
        Color dark = new Color(80, 55, 35);
        int ph = ts / 4;
        for (int y = 0; y < ts; y += ph) {
            g.setColor(base); g.fillRect(1, y, ts - 2, ph - 2);
            g.setColor(dark); g.fillRect(0, y + ph - 2, ts, 2);
            g.setColor(br(base, 20)); g.fillRect(2, y + 2, ts / 2, 1);
            g.setColor(dk(base, 15)); g.fillRect(ts / 2, y + ph - 5, ts / 3, 1);
        }
        g.dispose(); 
        return img;
    }

    private BufferedImage createIndieSand() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(218, 197, 130)); 
        g.fillRect(0, 0, ts, ts);
        g.setColor(new Color(198, 174, 105)); 
        for (int y = 6; y < ts; y += 8) {
            g.drawLine(0, y, ts - 1, y);
        }
        g.dispose(); 
        return img;
    }
}
