package tile.region;

import core.GamePanel;
import tile.Tile;
import tile.TileManager;
import java.awt.*;

public class RuinRegion extends Region {

    public RuinRegion(GamePanel gp) {
        super(gp);
    }

    @Override
    public void registerTiles(Tile[] tiles) {
        tiles[TileManager.RUIN_FLOOR] = new Tile(); 
        tiles[TileManager.RUIN_FLOOR].image = createIndieBrick(new Color(108, 96, 76), new Color(68, 60, 48), true);
        
        tiles[TileManager.RUIN_WALL] = new Tile(); 
        tiles[TileManager.RUIN_WALL].image = createIndieBrick(new Color(62, 54, 42), new Color(40, 35, 28), false);
        tiles[TileManager.RUIN_WALL].collision = true;
    }
}
