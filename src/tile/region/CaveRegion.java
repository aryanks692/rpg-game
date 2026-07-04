package tile.region;

import core.GamePanel;
import tile.Tile;
import tile.TileManager;
import java.awt.*;

public class CaveRegion extends Region {

    public CaveRegion(GamePanel gp) {
        super(gp);
    }

    @Override
    public void registerTiles(Tile[] tiles) {
        tiles[TileManager.CAVE_FLOOR] = new Tile(); 
        tiles[TileManager.CAVE_FLOOR].image = createIndieBrick(new Color(60, 55, 78), new Color(38, 34, 52), false);
        
        tiles[TileManager.CAVE_WALL] = new Tile(); 
        tiles[TileManager.CAVE_WALL].image = createIndieBrick(new Color(36, 32, 50), new Color(20, 18, 34), false);
        tiles[TileManager.CAVE_WALL].collision = true;
    }
}
