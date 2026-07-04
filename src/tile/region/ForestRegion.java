package tile.region;

import core.GamePanel;
import tile.Tile;
import tile.TileManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ForestRegion extends Region {

    public ForestRegion(GamePanel gp) {
        super(gp);
    }

    @Override
    public void registerTiles(Tile[] tiles) {
        tiles[TileManager.GRASS] = new Tile(); 
        tiles[TileManager.GRASS].image = createPremiumFloralGrass(new Color(110, 195, 75), false);
        
        tiles[TileManager.GRASS2] = new Tile(); 
        tiles[TileManager.GRASS2].image = createPremiumFloralGrass(new Color(95, 185, 60), true);
        
        tiles[TileManager.TREE] = new Tile(); 
        tiles[TileManager.TREE].image = createIndieTree(); 
        tiles[TileManager.TREE].collision = true;
        
        tiles[TileManager.FLOWER] = new Tile(); 
        tiles[TileManager.FLOWER].image = createPremiumFloralGrass(new Color(100, 205, 80), true);
        
        tiles[TileManager.DARK_GRASS] = new Tile(); 
        tiles[TileManager.DARK_GRASS].image = createPremiumFloralGrass(new Color(40, 110, 40), true);
    }

    private BufferedImage createPremiumFloralGrass(Color base, boolean dense) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Color dark = dk(base, 18);
        for (int y = 0; y < ts; y += 8) {
            g.setColor((y / 8) % 2 == 0 ? base : dark);
            g.fillRect(0, y, ts, 8);
        }
        g.setColor(dk(base, 25));
        for (int i = 0; i < 12; i++) {
            g.fillRect((int)(Math.random() * ts), (int)(Math.random() * ts), 2, 2);
        }
        Color[] blossoms = {new Color(255, 235, 100), Color.WHITE, new Color(255, 160, 60)};
        int numFlowers = dense ? 30 : 15; 
        for (int i = 0; i < numFlowers; i++) {
            int fx = (int)(Math.random() * (ts - 6)), fy = (int)(Math.random() * (ts - 6));
            g.setColor(blossoms[i % blossoms.length]);
            g.fillRect(fx, fy, 4, 3);
            g.setColor(new Color(0, 0, 0, 30));
            g.fillRect(fx + 1, fy + 3, 3, 1);
        }
        g.dispose(); 
        return img;
    }

    private BufferedImage createIndieTree() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(76, 162, 56)); g.fillRect(0, 0, ts, ts);
        g.setColor(new Color(108, 70, 28)); g.fillRect(ts / 2 - 4, ts / 2, 8, ts / 2);
        g.setColor(new Color(30, 100, 28, 110)); g.fillOval(ts / 2 - 14, ts / 2 - 4, 28, 12);
        g.setColor(new Color(32, 118, 32)); g.fillOval(ts / 2 - 18, 1, 36, 36);
        g.setColor(new Color(52, 148, 50)); g.fillOval(ts / 2 - 14, 2, 28, 30);
        g.dispose(); 
        return img;
    }
}
