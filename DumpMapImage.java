import core.GamePanel;
import tile.TileManager;
import tile.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class DumpMapImage {
    public static void main(String[] args) {
        try {
            // Need a dummy JFrame to initialize properly if needed, but GamePanel is a JPanel
            GamePanel gp = new GamePanel();
            
            // TileManager uses gp settings
            TileManager tm = new TileManager(gp);
            
            int worldWidth = gp.maxWorldCol * gp.tileSize;
            int worldHeight = gp.maxWorldRow * gp.tileSize;
            
            BufferedImage mapImage = new BufferedImage(worldWidth, worldHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = mapImage.createGraphics();
            
            System.out.println("Generating map image...");
            
            for (int col = 0; col < gp.maxWorldCol; col++) {
                for (int row = 0; row < gp.maxWorldRow; row++) {
                    int tileId = tm.mapTileNum[col][row];
                    if (tileId >= 0 && tileId < tm.tiles.length && tm.tiles[tileId] != null) {
                        BufferedImage tileImg = tm.tiles[tileId].image;
                        if (tileImg != null) {
                            g2.drawImage(tileImg, col * gp.tileSize, row * gp.tileSize, null);
                        }
                    }
                }
            }
            
            g2.dispose();
            
            // Save downscaled version for artifact (e.g. 1/4 size)
            int scaleDown = 4;
            BufferedImage smallMap = new BufferedImage(worldWidth/scaleDown, worldHeight/scaleDown, BufferedImage.TYPE_INT_ARGB);
            Graphics2D sg2 = smallMap.createGraphics();
            sg2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            sg2.drawImage(mapImage, 0, 0, worldWidth/scaleDown, worldHeight/scaleDown, null);
            sg2.dispose();
            
            String outPath = "C:\\Users\\aryanks\\.gemini\\antigravity\\brain\\7dd694a3-e2fd-449e-95fb-5ab5d722d2eb\\map_overview.png";
            ImageIO.write(smallMap, "PNG", new File(outPath));
            
            System.out.println("Map image saved successfully to " + outPath);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
