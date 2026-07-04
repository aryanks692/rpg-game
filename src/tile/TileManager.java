package tile;

import core.GamePanel;
import tile.region.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class TileManager {
    private GamePanel gp;
    public Tile[] tiles;
    public int[][] mapTileNum;

    // Tile IDs
    public static final int GRASS      = 0;
    public static final int GRASS2     = 1;
    public static final int DIRT       = 2;
    public static final int STONE      = 3;
    public static final int WATER_A    = 4;
    public static final int WATER_B    = 5;
    public static final int TREE       = 6;
    public static final int WALL       = 7;
    public static final int SAND       = 8;
    public static final int PATH       = 9;
    public static final int FLOWER     = 10;
    public static final int DARK_GRASS = 11;
    public static final int CAVE_FLOOR = 12;
    public static final int CAVE_WALL  = 13;
    public static final int RUIN_FLOOR = 14;
    public static final int RUIN_WALL  = 15;
    public static final int SAVANNAH_GRASS = 16;
    public static final int SAVANNAH_TREE  = 17;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tiles = new Tile[32];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        createTiles();
        loadMap("/res/maps/overworld.csv");
    }

    private void createTiles() {
        // Delegate tile creation to regional handlers
        new ForestRegion(gp).registerTiles(tiles);
        new TownRegion(gp).registerTiles(tiles);
        new CaveRegion(gp).registerTiles(tiles);
        new RuinRegion(gp).registerTiles(tiles);
        new SavannahRegion(gp).registerTiles(tiles);
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) { generateDefaultMap(); return; }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int row = 0; row < gp.maxWorldRow; row++) {
                String line = br.readLine(); if (line == null) break;
                String[] nums = line.split(",");
                for (int col = 0; col < gp.maxWorldCol && col < nums.length; col++) {
                    try { mapTileNum[col][row] = Integer.parseInt(nums[col].trim()); } catch (Exception ignored) {}
                }
            }
            br.close();
        } catch (Exception e) { generateDefaultMap(); }
    }

    private void generateDefaultMap() {
        int W = gp.maxWorldCol; int H = gp.maxWorldRow; int rowOff = 30;
        for (int c = 0; c < W; c++) for (int r = 0; r < H; r++) mapTileNum[c][r] = GRASS;
        for (int c = 0; c < W; c++) { mapTileNum[c][0] = WALL; mapTileNum[c][H-1] = WALL; }
        for (int r = 0; r < H; r++) { mapTileNum[0][r] = WALL; mapTileNum[W-1][r] = WALL; }
        for (int c = 1; c < W-1; c++) for (int r = 1; r < rowOff; r++) mapTileNum[c][r] = (c % 7 == 0 && r % 4 == 0) ? SAND : SAVANNAH_GRASS;
        for (int c = 5; c < W-5; c += 8) for (int r = 3; r < rowOff-3; r += 7) if ((c * r) % 5 < 2) mapTileNum[c][r] = SAVANNAH_TREE;
        for (int c = 1; c <= 5; c++) for (int r = rowOff + 1; r < H-1; r++) mapTileNum[c][r] = WATER_A;
        for (int r = rowOff + 1; r < H-1; r++) { mapTileNum[6][r] = (r % 3 == 0) ? SAND : WATER_A; mapTileNum[7][r] = SAND; mapTileNum[8][r] = (r % 5 == 0) ? GRASS2 : SAND; }
        for (int c = 5; c <= 9; c++) mapTileNum[c][20+rowOff] = PATH;
        for (int r = 18+rowOff; r <= 22+rowOff; r++) mapTileNum[9][r] = PATH;
        for (int c = 9; c < W-1; c++) for (int r = 1+rowOff; r <= 8+rowOff; r++) mapTileNum[c][r] = (c % 5 == 0 && r % 3 == 0) ? FLOWER : GRASS2;
        placeTrees(10, 1+rowOff, W-2, 7+rowOff);
        for (int c = 19; c <= 29; c++) mapTileNum[c][8+rowOff] = PATH;
        for (int c = 45; c < W-1; c++) for (int r = 1+rowOff; r < H-1; r++) mapTileNum[c][r] = (r < 30+rowOff) ? GRASS2 : GRASS;
        placeTrees(46, 1+rowOff, W-2, 28+rowOff);
        paintZone(10, 9+rowOff, 32, 22+rowOff, PATH, GRASS, FLOWER);
        placeTrees(13, 9+rowOff, 27, 21+rowOff);
        for (int c = 12; c < 15; c++) for (int r = 11+rowOff; r < 14+rowOff; r++) mapTileNum[c][r] = STONE;
        for (int c = 17; c < 21; c++) for (int r = 11+rowOff; r < 16+rowOff; r++) mapTileNum[c][r] = STONE;
        for (int c = 24; c < 27; c++) for (int r = 11+rowOff; r < 14+rowOff; r++) mapTileNum[c][r] = STONE;
        for (int c = 11; c < 30; c++) mapTileNum[c][18+rowOff] = PATH;
        for (int r = 14+rowOff; r < 18+rowOff; r++) mapTileNum[13][r] = PATH;
        for (int r = 14+rowOff; r < 18+rowOff; r++) mapTileNum[25][r] = PATH;
        for (int r = 16+rowOff; r < 18+rowOff; r++) { mapTileNum[18][r] = PATH; mapTileNum[19][r] = PATH; }
        for (int r = 18+rowOff; r < 26+rowOff; r++) mapTileNum[18][r] = PATH;
        for (int c = 33; c < 42; c++) for (int r = 1+rowOff; r < 28+rowOff; r++) mapTileNum[c][r] = DARK_GRASS;
        placeTrees(34, 2+rowOff, 41, 27+rowOff);
        for (int c = 9; c < 28; c++) for (int r = 38+rowOff; r < H-1; r++) mapTileNum[c][r] = CAVE_FLOOR;
        for (int r = 38+rowOff; r < H-1; r++) { mapTileNum[9][r] = CAVE_WALL; mapTileNum[27][r] = CAVE_WALL; }
        for (int c = 9; c < 28; c++) { mapTileNum[c][38+rowOff] = CAVE_WALL; mapTileNum[c][H-2] = CAVE_WALL; }
        for (int c = 17; c <= 19; c++) mapTileNum[c][38+rowOff] = CAVE_FLOOR;
        for (int c = 30; c < W-1; c++) for (int r = 38+rowOff; r < H-1; r++) mapTileNum[c][r] = RUIN_FLOOR;
        for (int c = 32; c < W-2; c += 7) for (int r = 40+rowOff; r < H-3; r += 7) mapTileNum[c][r] = RUIN_WALL;
        for (int r = 38+rowOff; r < H-1; r++) mapTileNum[W-2][r] = RUIN_WALL;
        for (int c = 28; c < 45; c++) for (int r = 29+rowOff; r < 38+rowOff; r++) mapTileNum[c][r] = SAND;
        for (int c = 9; c < 45; c++) for (int r = 28+rowOff; r <= 28+rowOff; r++) mapTileNum[c][r] = GRASS;
        for (int r = 26+rowOff; r < 38+rowOff; r++) mapTileNum[28][r] = PATH;
        for (int c = 9;  c < 45; c++) mapTileNum[c][37+rowOff]  = PATH;
    }

    private void paintZone(int c1, int r1, int c2, int r2, int mainTile, int bg1, int bg2) {
        for (int c = c1; c <= c2 && c < gp.maxWorldCol; c++) for (int r = r1; r <= r2 && r < gp.maxWorldRow; r++) mapTileNum[c][r] = (c + r) % 3 == 0 ? bg2 : bg1;
    }

    private void placeTrees(int c1, int r1, int c2, int r2) {
        for (int c = c1; c <= c2 && c < gp.maxWorldCol; c += 3) for (int r = r1; r <= r2 && r < gp.maxWorldRow; r += 3) if ((c + r) % 5 != 0) mapTileNum[c][r] = TREE;
    }

    public void draw(Graphics2D g2) {
        int camX = gp.camera.x; int camY = gp.camera.y;
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                int worldX = col * gp.tileSize; int worldY = row * gp.tileSize;
                int screenX = worldX - camX; int screenY = worldY - camY;
                if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth || screenY + gp.tileSize < 0 || screenY > gp.screenHeight) continue;
                int tileId = mapTileNum[col][row];
                if (tileId < 0 || tileId >= tiles.length || tiles[tileId] == null) continue;
                BufferedImage img = tiles[tileId].getCurrentFrame();
                if (img != null) g2.drawImage(img, screenX, screenY, null);
            }
        }
    }
}