package tile;

import core.GamePanel;
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

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tiles = new Tile[32];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        createTiles();
        loadMap("/res/maps/overworld.csv");
    }

    private BufferedImage createTileImage(Color base, Color detail, int pattern) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Base fill
        g.setColor(base);
        g.fillRect(0, 0, ts, ts);

        // Pattern details
        g.setColor(detail);
        switch (pattern) {
            case 0 -> { // Dots
                for (int i = 0; i < 4; i++) {
                    int px = (int)(Math.random() * (ts - 4));
                    int py = (int)(Math.random() * (ts - 4));
                    g.fillOval(px, py, 4, 4);
                }
            }
            case 1 -> { // Horizontal lines
                for (int y = 8; y < ts; y += 8) {
                    g.drawLine(0, y, ts, y);
                }
            }
            case 2 -> { // Grid
                for (int x = 0; x < ts; x += 12) g.drawLine(x, 0, x, ts);
                for (int y = 0; y < ts; y += 12) g.drawLine(0, y, ts, y);
            }
            case 3 -> { // Diagonal
                for (int i = -ts; i < ts * 2; i += 10) {
                    g.drawLine(i, 0, i + ts, ts);
                }
            }
            case 4 -> { // Noise dots
                for (int r = 0; r < 6; r++) {
                    int px = (int)(Math.random() * ts);
                    int py = (int)(Math.random() * ts);
                    g.fillRect(px, py, 3, 3);
                }
            }
            case 5 -> { // Pokemon Grass Speckles
                for (int i = 0; i < 8; i++) {
                    int px = (int)(Math.random() * (ts - 4));
                    int py = (int)(Math.random() * (ts - 4));
                    g.fillRect(px, py, 2, 2);
                    g.fillRect(px + 4, py + 2, 2, 2);
                }
            }
        }
        g.dispose();
        return img;
    }

    private BufferedImage createWaterFrame(Color c1, Color c2, float wave) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        GradientPaint gp2 = new GradientPaint(0, 0, c1, ts, ts, c2);
        g.setPaint(gp2);
        g.fillRect(0, 0, ts, ts);
        // Wave lines
        g.setColor(new Color(255, 255, 255, 60));
        g.setStroke(new BasicStroke(2));
        for (int y = 8; y < ts; y += 10) {
            int offset = (int)(Math.sin(y * wave) * 5);
            g.drawLine(offset, y, ts + offset, y);
        }
        g.dispose();
        return img;
    }

    private BufferedImage createHDGrass(Color base, boolean isDarkVariant) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        g.setColor(base);
        g.fillRect(0, 0, ts, ts);
        
        // Add messy dirt/moss shading patches (Stardew-style)
        for (int i = 0; i < 40; i++) {
            int cx = (int)(Math.random() * ts);
            int cy = (int)(Math.random() * ts);
            int rMod = (int)((Math.random() - 0.5) * 20);
            int r = Math.max(0, Math.min(255, base.getRed() + rMod));
            int gr = Math.max(0, Math.min(255, base.getGreen() + rMod));
            int b = Math.max(0, Math.min(255, base.getBlue() + rMod));
            g.setColor(new Color(r, gr, b));
            g.fillRect(cx, cy, 4, 4);
        }
        
        // Add tall grass blades mapping
        int numBlades = isDarkVariant ? 60 : 40;
        for (int i = 0; i < numBlades; i++) {
            int x = (int)(Math.random() * ts);
            int y = (int)(Math.random() * (ts - 6));
            int len = 4 + (int)(Math.random() * 6);
            
            if (Math.random() < 0.5) {
                g.setColor(new Color(Math.max(0, base.getRed()-25), Math.max(0, base.getGreen()-35), Math.max(0, base.getBlue()-25)));
            } else {
                g.setColor(new Color(Math.min(255, base.getRed()+25), Math.min(255, base.getGreen()+35), Math.min(255, base.getBlue()+25)));
            }
            g.fillRect(x, y, 2, len);
        }
        g.dispose();
        return img;
    }

    private BufferedImage createCobblestone(Color baseBrick, Color grout) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        g.setColor(grout);
        g.fillRect(0, 0, ts, ts); // Dark grout background
        
        // Algorithm for interlocking brick floor
        int rows = 6;
        int cols = 3;
        int bh = ts / rows;
        int bw = ts / cols;
        
        for (int r = 0; r <= rows; r++) {
            int y = r * bh;
            boolean offset = (r % 2 != 0);
            
            for (int c = -1; c <= cols; c++) {
                int x = c * bw;
                if (offset) x -= bw / 2;
                
                int padding = 2; // Grout width
                int rw = bw - padding;
                int rh = bh - padding;
                
                // Color variation per brick
                int var = (int)((Math.random() - 0.5) * 30);
                int rCol = Math.max(0, Math.min(255, baseBrick.getRed() + var));
                int gCol = Math.max(0, Math.min(255, baseBrick.getGreen() + var));
                int bCol = Math.max(0, Math.min(255, baseBrick.getBlue() + var));
                
                g.setColor(new Color(rCol, gCol, bCol));
                g.fillRoundRect(x + 1, y + 1, rw, rh, 4, 4);
                
                // Top highlight for 3D depth
                g.setColor(new Color(Math.min(255, rCol + 15), Math.min(255, gCol + 15), Math.min(255, bCol + 15)));
                g.fillRect(x + 2, y + 2, rw - 2, 2);
            }
        }
        g.dispose();
        return img;
    }

    private void createTiles() {
        // HD GRASS
        tiles[GRASS] = new Tile();
        tiles[GRASS].image = createHDGrass(new Color(65, 125, 45), false); 

        // HD GRASS2
        tiles[GRASS2] = new Tile();
        tiles[GRASS2].image = createHDGrass(new Color(55, 115, 35), true);

        // HD DIRT PATH (Cobblestone)
        tiles[DIRT] = new Tile();
        tiles[DIRT].image = createCobblestone(new Color(130, 120, 110), new Color(80, 70, 60));

        // STONE
        tiles[STONE] = new Tile();
        tiles[STONE].image = createSolidTile(new Color(120, 120, 130), new Color(90, 90, 100), 2);
        tiles[STONE].collision = true;

        // WATER animated
        tiles[WATER_A] = new Tile();
        tiles[WATER_A].animated = true;
        tiles[WATER_A].frames = new BufferedImage[]{
            createWaterFrame(new Color(30, 100, 200), new Color(20, 80, 180), 0.3f),
            createWaterFrame(new Color(35, 110, 210), new Color(25, 90, 190), 0.5f),
            createWaterFrame(new Color(40, 120, 220), new Color(30, 100, 200), 0.7f),
            createWaterFrame(new Color(35, 110, 210), new Color(25, 90, 190), 0.5f)
        };
        tiles[WATER_A].image = tiles[WATER_A].frames[0];
        tiles[WATER_A].collision = true;
        tiles[WATER_A].animSpeed = 20;

        tiles[WATER_B] = tiles[WATER_A];

        // TREE (collision)
        tiles[TREE] = new Tile();
        tiles[TREE].image = createTreeTile();
        tiles[TREE].collision = true;

        // WALL
        tiles[WALL] = new Tile();
        tiles[WALL].image = createSolidTile(new Color(80, 80, 90), new Color(60, 60, 70), 2);
        tiles[WALL].collision = true;

        // SAND
        tiles[SAND] = new Tile();
        tiles[SAND].image = createSolidTile(new Color(210, 185, 120), new Color(225, 200, 140), 4);

        // PATH (HD Cobblestone)
        tiles[PATH] = new Tile();
        tiles[PATH].image = createCobblestone(new Color(175, 175, 170), new Color(110, 110, 100));

        // FLOWER
        tiles[FLOWER] = new Tile();
        tiles[FLOWER].image = createFlowerTile();

        // DARK_GRASS (HD)
        tiles[DARK_GRASS] = new Tile();
        tiles[DARK_GRASS].image = createHDGrass(new Color(40, 90, 30), true);

        // CAVE_FLOOR
        tiles[CAVE_FLOOR] = new Tile();
        tiles[CAVE_FLOOR].image = createSolidTile(new Color(70, 65, 80), new Color(60, 55, 70), 2);

        // CAVE_WALL
        tiles[CAVE_WALL] = new Tile();
        tiles[CAVE_WALL].image = createSolidTile(new Color(40, 35, 50), new Color(30, 25, 40), 2);
        tiles[CAVE_WALL].collision = true;

        // RUIN_FLOOR
        tiles[RUIN_FLOOR] = new Tile();
        tiles[RUIN_FLOOR].image = createSolidTile(new Color(100, 90, 80), new Color(90, 80, 70), 3);

        // RUIN_WALL
        tiles[RUIN_WALL] = new Tile();
        tiles[RUIN_WALL].image = createSolidTile(new Color(60, 55, 50), new Color(50, 45, 40), 2);
        tiles[RUIN_WALL].collision = true;
    }

    private BufferedImage createSolidTile(Color base, Color detail, int pattern) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(base);
        g.fillRect(0, 0, ts, ts);
        g.setColor(detail);
        // Border highlight
        g.drawRect(0, 0, ts - 1, ts - 1);
        // Pattern
        switch (pattern) {
            case 0 -> { // Grass tufts
                for (int i = 6; i < ts - 4; i += 12) {
                    for (int j = 6; j < ts - 4; j += 12) {
                        if ((i + j) % 24 == 0) {
                            g.fillOval(i, j, 5, 5);
                        }
                    }
                }
            }
            case 1 -> { // Horizontal lines
                for (int y = 10; y < ts - 2; y += 12) {
                    g.drawLine(2, y, ts - 2, y);
                }
            }
            case 2 -> { // Grid / stone
                for (int x = 12; x < ts; x += 12) g.drawLine(x, 0, x, ts);
                for (int y = 12; y < ts; y += 12) g.drawLine(0, y, ts, y);
            }
            case 3 -> { // Diagonal lines
                g.setStroke(new BasicStroke(1));
                for (int i = -ts; i < ts * 2; i += 14) {
                    g.drawLine(i, 0, i + ts, ts);
                }
            }
            case 4 -> { // Sand noise
                for (int r = 0; r < 8; r++) {
                    int px = 4 + (int)((r * 7 + 3) % (ts - 8));
                    int py = 4 + (int)((r * 11 + 5) % (ts - 8));
                    g.fillOval(px, py, 3, 2);
                }
            }
        }
        g.setStroke(new BasicStroke(1));
        g.dispose();
        return img;
    }

    private BufferedImage createTreeTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Ground
        g.setColor(new Color(76, 140, 52));
        g.fillRect(0, 0, ts, ts);
        // Trunk
        g.setColor(new Color(100, 65, 30));
        g.fillRect(ts/2 - 5, ts/2 + 2, 10, ts/2 - 2);
        // Canopy layers
        g.setColor(new Color(30, 110, 30));
        g.fillOval(ts/2 - ts/3, ts/6, (int)(ts * 0.66), (int)(ts * 0.6));
        g.setColor(new Color(50, 140, 50));
        g.fillOval(ts/2 - ts/4, ts/8, ts/2, (int)(ts * 0.5));
        g.setColor(new Color(70, 160, 70));
        g.fillOval(ts/2 - ts/5, ts/5, (int)(ts * 0.4), (int)(ts * 0.35));
        g.dispose();
        return img;
    }

    private BufferedImage createFlowerTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Grass base
        g.setColor(new Color(86, 165, 62));
        g.fillRect(0, 0, ts, ts);
        // Flowers
        Color[] petals = {Color.YELLOW, Color.WHITE, Color.PINK, Color.CYAN};
        int[][] pos = {{8,8},{28,18},{16,30},{36,8},{40,30}};
        for (int i = 0; i < pos.length; i++) {
            Color c = petals[i % petals.length];
            g.setColor(c);
            g.fillOval(pos[i][0], pos[i][1], 6, 6);
            g.setColor(Color.YELLOW);
            g.fillOval(pos[i][0] + 2, pos[i][1] + 2, 3, 3);
        }
        g.dispose();
        return img;
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                generateDefaultMap();
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int row = 0; row < gp.maxWorldRow; row++) {
                String line = br.readLine();
                if (line == null) break;
                String[] nums = line.split(",");
                for (int col = 0; col < gp.maxWorldCol && col < nums.length; col++) {
                    try {
                        mapTileNum[col][row] = Integer.parseInt(nums[col].trim());
                    } catch (NumberFormatException ignored) {}
                }
            }
            br.close();
        } catch (IOException e) {
            generateDefaultMap();
        }
    }

    private void generateDefaultMap() {
        // Fill with grass
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                mapTileNum[col][row] = GRASS;
            }
        }
        // Border walls
        for (int col = 0; col < gp.maxWorldCol; col++) {
            mapTileNum[col][0] = WALL;
            mapTileNum[col][gp.maxWorldRow - 1] = WALL;
        }
        for (int row = 0; row < gp.maxWorldRow; row++) {
            mapTileNum[0][row] = WALL;
            mapTileNum[gp.maxWorldCol - 1][row] = WALL;
        }

        // Village area (top-left quadrant)
        paintZone(2, 2, 20, 15, PATH, GRASS, FLOWER);
        // Some trees
        placeTrees(5, 2, 15, 14);
        // Village buildings (stone blocks for collision)
        // House 1 (3x3)
        for (int c = 4; c < 7; c++) for (int r = 4; r < 7; r++) mapTileNum[c][r] = STONE;
        // Church (4x5)
        for (int c = 9; c < 13; c++) for (int r = 4; r < 9; r++) mapTileNum[c][r] = STONE;
        // House 2 (3x3)
        for (int c = 15; c < 18; c++) for (int r = 4; r < 7; r++) mapTileNum[c][r] = STONE;
        
        // Path through village
        for (int c = 3; c < 19; c++) mapTileNum[c][11] = PATH; // Horizontal main street
        for (int r = 7; r < 11; r++) mapTileNum[5][r] = PATH;  // Path to House 1
        for (int r = 7; r < 11; r++) mapTileNum[16][r] = PATH; // Path to House 2
        for (int r = 9; r < 11; r++) {                         // Path to Church doors
            mapTileNum[10][r] = PATH; 
            mapTileNum[11][r] = PATH; 
        }
        for (int r = 11; r < 18; r++) mapTileNum[10][r] = PATH; // Vertical town exit path

        // Water lake removed completely per request so it doesn't block the map.
        // Instead, leaving it as default GRASS.

        // Dark Forest (right side)
        for (int c = 33; c < gp.maxWorldCol - 1; c++) {
            for (int r = 1; r < 25; r++) {
                mapTileNum[c][r] = DARK_GRASS;
            }
        }
        placeTrees(34, 2, gp.maxWorldCol - 2, 24);

        // Crystal Caves (bottom left)
        for (int c = 2; c < 20; c++) for (int r = 30; r < gp.maxWorldRow - 1; r++) mapTileNum[c][r] = CAVE_FLOOR;
        for (int c = 4; c < 18; c++) for (int r = 32; r < gp.maxWorldRow - 3; r++) mapTileNum[c][r] = CAVE_FLOOR;
        // Cave walls around
        for (int r = 30; r < gp.maxWorldRow - 1; r++) { mapTileNum[2][r] = CAVE_WALL; mapTileNum[19][r] = CAVE_WALL; }
        for (int c = 2; c < 20; c++) { mapTileNum[c][30] = CAVE_WALL; mapTileNum[c][gp.maxWorldRow - 2] = CAVE_WALL; }

        // Ancient Ruins (bottom right)
        for (int c = 30; c < gp.maxWorldCol - 1; c++) for (int r = 28; r < gp.maxWorldRow - 1; r++) mapTileNum[c][r] = RUIN_FLOOR;
        for (int c = 32; c < gp.maxWorldCol - 3; c += 6) {
            for (int r = 30; r < gp.maxWorldRow - 3; r += 6) {
                mapTileNum[c][r] = RUIN_WALL;
            }
        }

        // Sand area (middle bottom)
        for (int c = 20; c < 30; c++) for (int r = 28; r < gp.maxWorldRow - 1; r++) mapTileNum[c][r] = SAND;

        // Connection paths
        for (int r = 15; r < 35; r++) mapTileNum[20][r] = PATH; // vertical
        for (int c = 20; c < 35; c++) mapTileNum[c][26] = PATH; // horizontal
    }

    private void paintZone(int c1, int r1, int c2, int r2, int mainTile, int bg1, int bg2) {
        for (int c = c1; c <= c2 && c < gp.maxWorldCol; c++) {
            for (int r = r1; r <= r2 && r < gp.maxWorldRow; r++) {
                mapTileNum[c][r] = (c + r) % 3 == 0 ? bg2 : bg1;
            }
        }
    }

    private void placeTrees(int c1, int r1, int c2, int r2) {
        for (int c = c1; c <= c2 && c < gp.maxWorldCol; c += 3) {
            for (int r = r1; r <= r2 && r < gp.maxWorldRow; r += 3) {
                if ((c + r) % 5 != 0) {
                    mapTileNum[c][r] = TREE;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        int camX = gp.camera.x;
        int camY = gp.camera.y;

        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                int worldX = col * gp.tileSize;
                int worldY = row * gp.tileSize;
                int screenX = worldX - camX;
                int screenY = worldY - camY;

                // Frustum cull
                if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth) continue;
                if (screenY + gp.tileSize < 0 || screenY > gp.screenHeight) continue;

                int tileId = mapTileNum[col][row];
                if (tileId < 0 || tileId >= tiles.length || tiles[tileId] == null) continue;
                BufferedImage img = tiles[tileId].getCurrentFrame();
                if (img != null) {
                    g2.drawImage(img, screenX, screenY, null);
                }
            }
        }
    }
}
