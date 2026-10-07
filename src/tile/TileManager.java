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
    public static final int SAVANNAH_GRASS = 16;
    public static final int SAVANNAH_TREE  = 17;
    public static final int CACTUS         = 18;
    public static final int WOOD_PLANK     = 19;
    public static final int RED_SAND       = 20;
    public static final int WOOD_FENCE     = 21;
    public static final int HITCHING_POST  = 22;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tiles = new Tile[32];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        createTiles();
        loadMap("/res/maps/overworld.csv");
    }

    private Color br(Color c, int n) {
        return new Color(Math.min(255,c.getRed()+n), Math.min(255,c.getGreen()+n), Math.min(255,c.getBlue()+n));
    }
    private Color dk(Color c, int n) {
        return new Color(Math.max(0,c.getRed()-n), Math.max(0,c.getGreen()-n), Math.max(0,c.getBlue()-n));
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
            g.fillRect((int)(Math.random()*ts), (int)(Math.random()*ts), 2, 2);
        }
        Color[] blossoms = {new Color(255, 235, 100), Color.WHITE, new Color(255, 160, 60)};
        int numFlowers = dense ? 30 : 15; 
        for (int i = 0; i < numFlowers; i++) {
            int fx = (int)(Math.random() * (ts-6)), fy = (int)(Math.random() * (ts-6));
            g.setColor(blossoms[i % blossoms.length]);
            g.fillRect(fx, fy, 4, 3);
            g.setColor(new Color(0,0,0,30));
            g.fillRect(fx+1, fy+3, 3, 1);
        }
        g.dispose(); return img;
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

    private BufferedImage createWoodPathTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Color base = new Color(155, 115, 75);
        Color dark = new Color(80, 55, 35);
        int ph = ts / 4;
        for (int y = 0; y < ts; y += ph) {
            g.setColor(base); g.fillRect(1, y, ts-2, ph-2);
            g.setColor(dark); g.fillRect(0, y+ph-2, ts, 2);
            g.setColor(br(base, 20)); g.fillRect(2, y+2, ts/2, 1);
            g.setColor(dk(base, 15)); g.fillRect(ts/2, y+ph-5, ts/3, 1);
        }
        g.dispose(); return img;
    }

    private BufferedImage createIndieCobblestone(Color stone, Color mortar, boolean rounded) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(mortar); g.fillRect(0, 0, ts, ts);
        if (rounded) {
            int[][] ss = {{1,1,23,15},{25,1,22,15},{1,17,14,14},{17,17,30,14}};
            for (int[] s : ss) {
                g.setColor(stone); g.fillRoundRect(s[0],s[1],s[2],s[3],8,8);
            }
        } else {
            g.setColor(stone); g.fillRect(0,0,ts,ts);
            g.setColor(mortar); for (int y = 11; y < ts; y += 12) g.drawLine(0,y,ts-1,y);
        }
        g.dispose(); return img;
    }

    private BufferedImage createIndieBrick(Color face, Color mortar, boolean light) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(mortar); g.fillRect(0, 0, ts, ts);
        int bh=16, bw=24;
        for (int row=0; row<ts/bh; row++) {
            int y = row*bh; int xOff = (row%2==0) ? 0 : bw/2;
            for (int x = -bw/2+xOff; x < ts+bw; x += bw) {
                int bx=x+1, by=y+1, dw=Math.min(bw-2, ts-bx);
                if (bx<ts && dw>0) {
                    g.setColor(face); g.fillRect(bx, by, dw, bh-2);
                    if (light) { g.setColor(br(face,22)); g.fillRect(bx, by, dw, 2); }
                    g.setColor(dk(face,18)); g.fillRect(bx, by+bh-4, dw, 2);
                }
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
        g.setColor(new Color(0, 0, 0, 90)); g.fillOval(ts/2 - 22, ts - 14, 44, 12);
        Color t1 = new Color(50, 35, 25); int tx = ts/2; int ty = ts - 4;
        g.setStroke(new BasicStroke(3)); g.setColor(t1); g.drawLine(tx, ty, tx, ty - 12);
        g.drawLine(tx, ty - 12, tx - 12, ty - 22); g.drawLine(tx, ty - 12, tx + 12, ty - 22);
        int cx = 2, cy = 8, cw = ts - 4, ch = 14;
        g.setColor(new Color(45, 65, 20)); g.fillRoundRect(cx, cy + 4, cw, ch, 15, 15);
        g.setColor(new Color(110, 140, 50)); g.fillRoundRect(cx, cy, cw, ch, 15, 15);
        g.dispose(); return img;
    }

    private BufferedImage createIndieWater(float phase) {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        GradientPaint grad = new GradientPaint(0,0,new Color(70,155,240),0,ts,new Color(28,88,200));
        g.setPaint(grad); g.fillRect(0,0,ts,ts);
        int wo = (int)(phase*12)%16;
        g.setColor(new Color(110,185,255,130)); g.setStroke(new BasicStroke(2));
        for (int y=6;y<ts;y+=12) for (int x=-16+wo;x<ts+16;x+=16) g.drawArc(x, y-3, 12, 8, 0, 180);
        g.dispose(); return img;
    }

    private BufferedImage createIndieTree() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(76,162,56)); g.fillRect(0,0,ts,ts);
        g.setColor(new Color(108,70,28)); g.fillRect(ts/2-4,ts/2,8,ts/2);
        g.setColor(new Color(30,100,28,110)); g.fillOval(ts/2-14,ts/2-4,28,12);
        g.setColor(new Color(32,118,32)); g.fillOval(ts/2-18,1,36,36);
        g.setColor(new Color(52,148,50)); g.fillOval(ts/2-14,2,28,30);
        g.dispose(); return img;
    }

    private BufferedImage createIndieSand() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(218,197,130)); g.fillRect(0,0,ts,ts);
        g.setColor(new Color(198,174,105)); for (int y=6;y<ts;y+=8) g.drawLine(0,y,ts-1,y);
        g.dispose(); return img;
    }

    private BufferedImage createCactusTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Red sand ground base
        g.drawImage(createRedSandTile(), 0, 0, null);
        // Ground shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(ts / 2 - 14, ts - 10, 28, 8);
        // Saguaro Trunk
        Color cactusGreen = new Color(55, 125, 45);
        Color cactusDark = new Color(35, 85, 30);
        Color cactusLight = new Color(85, 160, 65);
        g.setColor(cactusDark);
        g.fillRoundRect(ts / 2 - 5, 8, 10, ts - 16, 6, 6);
        g.setColor(cactusGreen);
        g.fillRoundRect(ts / 2 - 4, 9, 8, ts - 18, 5, 5);
        // Left arm
        g.setColor(cactusDark);
        g.fillRect(ts / 2 - 14, 18, 10, 5);
        g.fillRoundRect(ts / 2 - 15, 12, 6, 12, 4, 4);
        g.setColor(cactusGreen);
        g.fillRect(ts / 2 - 13, 19, 8, 3);
        g.fillRoundRect(ts / 2 - 14, 13, 4, 10, 3, 3);
        // Right arm
        g.setColor(cactusDark);
        g.fillRect(ts / 2 + 4, 24, 10, 5);
        g.fillRoundRect(ts / 2 + 9, 16, 6, 14, 4, 4);
        g.setColor(cactusGreen);
        g.fillRect(ts / 2 + 5, 25, 8, 3);
        g.fillRoundRect(ts / 2 + 10, 17, 4, 12, 3, 3);
        // Vertical ribs/spines
        g.setColor(cactusLight);
        g.drawLine(ts / 2, 10, ts / 2, ts - 10);
        g.dispose();
        return img;
    }

    private BufferedImage createWoodPlankTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Color base = new Color(175, 130, 80);
        Color dark = new Color(105, 75, 45);
        Color light = new Color(205, 160, 110);
        int ph = ts / 4;
        for (int y = 0; y < ts; y += ph) {
            g.setColor(base);
            g.fillRect(0, y, ts, ph - 1);
            g.setColor(dark);
            g.fillRect(0, y + ph - 1, ts, 1);
            g.setColor(light);
            g.fillRect(0, y, ts, 1);
            // Nail dots
            g.setColor(new Color(60, 40, 25));
            g.fillRect(4, y + ph / 2, 2, 2);
            g.fillRect(ts - 6, y + ph / 2, 2, 2);
        }
        g.dispose();
        return img;
    }

    private BufferedImage createRedSandTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Color s1 = new Color(215, 145, 85);
        Color s2 = new Color(195, 125, 70);
        Color s3 = new Color(175, 105, 55);
        g.setColor(s1);
        g.fillRect(0, 0, ts, ts);
        for (int y = 0; y < ts; y += 2) {
            for (int x = 0; x < ts; x += 2) {
                int rand = (x * 17 + y * 11 + (x * y)) % 4;
                if (rand == 0) g.setColor(s2);
                else if (rand == 1) g.setColor(s3);
                else g.setColor(s1);
                g.fillRect(x, y, 2, 2);
            }
        }
        // Occasional desert pebbles
        g.setColor(new Color(130, 80, 45));
        g.fillRect(8, 12, 3, 2);
        g.fillRect(32, 28, 2, 2);
        g.fillRect(20, 40, 3, 1);
        g.dispose();
        return img;
    }

    private BufferedImage createWoodFenceTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(createRedSandTile(), 0, 0, null);
        // Shadow
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRect(0, ts - 12, ts, 6);
        // Fence Post
        Color wood = new Color(130, 85, 45);
        Color woodDark = new Color(85, 50, 25);
        Color woodLight = new Color(170, 115, 65);
        g.setColor(woodDark);
        g.fillRoundRect(ts / 2 - 5, 8, 10, ts - 14, 4, 4);
        g.setColor(wood);
        g.fillRoundRect(ts / 2 - 4, 9, 8, ts - 16, 3, 3);
        // Rails
        g.setColor(woodDark);
        g.fillRect(0, 14, ts, 7);
        g.fillRect(0, 26, ts, 7);
        g.setColor(wood);
        g.fillRect(0, 15, ts, 5);
        g.fillRect(0, 27, ts, 5);
        g.setColor(woodLight);
        g.fillRect(0, 15, ts, 1);
        g.fillRect(0, 27, ts, 1);
        g.dispose();
        return img;
    }

    private BufferedImage createHitchingPostTile() {
        int ts = gp.tileSize;
        BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(createWoodPlankTile(), 0, 0, null);
        Color wood = new Color(140, 95, 55);
        Color woodDark = new Color(90, 55, 30);
        // Left & Right upright posts
        g.setColor(woodDark);
        g.fillRect(6, 12, 6, ts - 16);
        g.fillRect(ts - 12, 12, 6, ts - 16);
        g.setColor(wood);
        g.fillRect(7, 13, 4, ts - 18);
        g.fillRect(ts - 11, 13, 4, ts - 18);
        // Horizontal hitch rail
        g.setColor(woodDark);
        g.fillRect(4, 18, ts - 8, 6);
        g.setColor(wood);
        g.fillRect(4, 19, ts - 8, 4);
        // Iron rings
        g.setColor(Color.LIGHT_GRAY);
        g.drawOval(14, 25, 6, 6);
        g.drawOval(ts - 20, 25, 6, 6);
        g.dispose();
        return img;
    }

    private void createTiles() {
        tiles[GRASS] = new Tile(); tiles[GRASS].image = createPremiumFloralGrass(new Color(110, 195, 75), false);
        tiles[GRASS2] = new Tile(); tiles[GRASS2].image = createPremiumFloralGrass(new Color(95, 185, 60), true);
        tiles[DIRT] = new Tile(); tiles[DIRT].image = createIndieCobblestone(new Color(140, 105, 68), new Color(95, 72, 46), false);
        tiles[STONE] = new Tile(); tiles[STONE].image = createIndieBrick(new Color(125, 122, 135), new Color(80, 78, 90), true);
        tiles[STONE].collision = true;
        tiles[WATER_A] = new Tile(); tiles[WATER_A].animated = true;
        tiles[WATER_A].frames = new BufferedImage[]{createIndieWater(0.0f), createIndieWater(0.5f)};
        tiles[WATER_A].image = tiles[WATER_A].frames[0]; tiles[WATER_A].collision = true;
        tiles[WATER_B] = tiles[WATER_A];
        tiles[TREE] = new Tile(); tiles[TREE].image = createIndieTree(); tiles[TREE].collision = true;
        tiles[WALL] = new Tile(); tiles[WALL].image = createIndieBrick(new Color(70, 68, 82), new Color(45, 43, 55), true);
        tiles[WALL].collision = true;
        tiles[SAND] = new Tile(); tiles[SAND].image = createIndieSand();
        tiles[PATH] = new Tile(); tiles[PATH].image = createWoodPathTile();
        tiles[FLOWER] = new Tile(); tiles[FLOWER].image = createPremiumFloralGrass(new Color(100, 205, 80), true);
        tiles[DARK_GRASS] = new Tile(); tiles[DARK_GRASS].image = createPremiumFloralGrass(new Color(40, 110, 40), true);
        tiles[CAVE_FLOOR] = new Tile(); tiles[CAVE_FLOOR].image = createIndieBrick(new Color(60, 55, 78), new Color(38, 34, 52), false);
        tiles[CAVE_WALL] = new Tile(); tiles[CAVE_WALL].image = createIndieBrick(new Color(36, 32, 50), new Color(20, 18, 34), false);
        tiles[CAVE_WALL].collision = true;
        tiles[RUIN_FLOOR] = new Tile(); tiles[RUIN_FLOOR].image = createIndieBrick(new Color(108, 96, 76), new Color(68, 60, 48), true);
        tiles[RUIN_WALL] = new Tile(); tiles[RUIN_WALL].image = createIndieBrick(new Color(62, 54, 42), new Color(40, 35, 28), false);
        tiles[RUIN_WALL].collision = true;
        tiles[SAVANNAH_GRASS] = new Tile(); tiles[SAVANNAH_GRASS].image = createIndieSavannahGrass();
        tiles[SAVANNAH_TREE] = new Tile(); tiles[SAVANNAH_TREE].image = createIndieAcaciaTree(); tiles[SAVANNAH_TREE].collision = true;
        tiles[CACTUS] = new Tile(); tiles[CACTUS].image = createCactusTile(); tiles[CACTUS].collision = true;
        tiles[WOOD_PLANK] = new Tile(); tiles[WOOD_PLANK].image = createWoodPlankTile();
        tiles[RED_SAND] = new Tile(); tiles[RED_SAND].image = createRedSandTile();
        tiles[WOOD_FENCE] = new Tile(); tiles[WOOD_FENCE].image = createWoodFenceTile(); tiles[WOOD_FENCE].collision = true;
        tiles[HITCHING_POST] = new Tile(); tiles[HITCHING_POST].image = createHitchingPostTile(); tiles[HITCHING_POST].collision = true;
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
        // World outer boundaries
        for (int c = 0; c < W; c++) { mapTileNum[c][0] = WALL; mapTileNum[c][H-1] = WALL; }
        for (int r = 0; r < H; r++) { mapTileNum[0][r] = WALL; mapTileNum[W-1][r] = WALL; }

        // --- 1. GREAT SAVANNAH (Rows 1-29, Cols 1-56) ---
        for (int c = 1; c <= 56; c++) for (int r = 1; r < rowOff; r++) mapTileNum[c][r] = (c % 7 == 0 && r % 4 == 0) ? SAND : SAVANNAH_GRASS;
        for (int c = 5; c <= 50; c += 8) for (int r = 3; r < rowOff-3; r += 7) if ((c * r) % 5 < 2) mapTileNum[c][r] = SAVANNAH_TREE;
        for (int c = 8; c <= 54; c += 11) for (int r = 5; r < rowOff-4; r += 8) mapTileNum[c][r] = CACTUS;

        // --- 2. BADLANDS & DUSTY GULCH WILD WEST TOWN (Rows 1-29, Cols 57-98) ---
        for (int c = 57; c < W - 1; c++) for (int r = 1; r < rowOff; r++) mapTileNum[c][r] = RED_SAND;

        // Connecting Frontier Trail from Savannah into Dusty Gulch
        for (int c = 48; c <= 62; c++) {
            mapTileNum[c][15] = RED_SAND;
            mapTileNum[c][16] = RED_SAND;
        }

        // Town Main Street (East-West Boulevard)
        for (int c = 61; c <= 96; c++) {
            mapTileNum[c][14] = WOOD_PLANK; // North Boardwalk
            mapTileNum[c][15] = RED_SAND;   // Main dirt road
            mapTileNum[c][16] = RED_SAND;   // Main dirt road
            mapTileNum[c][17] = WOOD_PLANK; // South Boardwalk
        }

        // Cross avenues & building plazas
        // Avenue 1: Police Station (Cols 66-68, Rows 8-14)
        for (int c = 66; c <= 68; c++) for (int r = 8; r <= 14; r++) mapTileNum[c][r] = WOOD_PLANK;
        // Avenue 2: Saloon Plaza (Cols 75-79, Rows 7-14)
        for (int c = 75; c <= 79; c++) for (int r = 7; r <= 14; r++) mapTileNum[c][r] = WOOD_PLANK;
        // Avenue 3: Dance Club Promenade (Cols 84-88, Rows 7-14)
        for (int c = 84; c <= 88; c++) for (int r = 7; r <= 14; r++) mapTileNum[c][r] = WOOD_PLANK;
        // Avenue 4: General Store & Water Tower (Cols 91-95, Rows 8-22)
        for (int c = 91; c <= 95; c++) for (int r = 8; r <= 22; r++) mapTileNum[c][r] = WOOD_PLANK;

        // Hitching Posts along boardwalks
        for (int c = 65; c <= 95; c += 6) {
            mapTileNum[c][13] = HITCHING_POST;
            mapTileNum[c][18] = HITCHING_POST;
        }

        // Western Perimeter Fences & Corral
        for (int c = 60; c <= 97; c++) { mapTileNum[c][2] = WOOD_FENCE; mapTileNum[c][28] = WOOD_FENCE; }
        for (int r = 2; r <= 28; r++) { if (r != 15 && r != 16) mapTileNum[60][r] = WOOD_FENCE; }
        // Animal Corral / Stables near water tower (Cols 82-89, Rows 20-26)
        for (int c = 82; c <= 89; c++) { mapTileNum[c][20] = WOOD_FENCE; mapTileNum[c][26] = WOOD_FENCE; }
        for (int r = 20; r <= 26; r++) { if (r != 23) mapTileNum[82][r] = WOOD_FENCE; mapTileNum[89][r] = WOOD_FENCE; }

        // Desert Cacti around badlands perimeter
        for (int c = 58; c < W - 2; c += 5) for (int r = 3; r < rowOff - 2; r += 6) if ((c + r) % 7 == 0 && mapTileNum[c][r] == RED_SAND) mapTileNum[c][r] = CACTUS;

        // --- 3. GOLDEN MEADOWS (Rows 30-38) ---
        for (int c = 1; c <= 5; c++) for (int r = rowOff + 1; r < H-1; r++) mapTileNum[c][r] = WATER_A;
        for (int r = rowOff + 1; r < H-1; r++) { mapTileNum[6][r] = (r % 3 == 0) ? SAND : WATER_A; mapTileNum[7][r] = SAND; mapTileNum[8][r] = (r % 5 == 0) ? GRASS2 : SAND; }
        for (int c = 5; c <= 9; c++) mapTileNum[c][20+rowOff] = PATH;
        for (int r = 18+rowOff; r <= 22+rowOff; r++) mapTileNum[9][r] = PATH;
        for (int c = 9; c < W-1; c++) for (int r = 1+rowOff; r <= 8+rowOff; r++) mapTileNum[c][r] = (c % 5 == 0 && r % 3 == 0) ? FLOWER : GRASS2;
        placeTrees(10, 1+rowOff, W-2, 7+rowOff);

        // --- 4. VERDANT VILLAGE & DARKWOOD FOREST (Rows 39-61) ---
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
        for (int c = 33; c < W-2; c++) for (int r = 1+rowOff; r < 28+rowOff; r++) if (mapTileNum[c][r] == GRASS) mapTileNum[c][r] = DARK_GRASS;
        placeTrees(34, 2+rowOff, W-3, 27+rowOff);

        // --- 5. CRYSTAL CAVES & ANCIENT RUINS (Rows 62-99) ---
        for (int c = 9; c < 28; c++) for (int r = 38+rowOff; r < H-1; r++) mapTileNum[c][r] = CAVE_FLOOR;
        for (int r = 38+rowOff; r < H-1; r++) { mapTileNum[9][r] = CAVE_WALL; mapTileNum[27][r] = CAVE_WALL; }
        for (int c = 9; c < 28; c++) { mapTileNum[c][38+rowOff] = CAVE_WALL; mapTileNum[c][H-2] = CAVE_WALL; }
        for (int c = 17; c <= 19; c++) mapTileNum[c][38+rowOff] = CAVE_FLOOR;
        for (int c = 30; c < W-1; c++) for (int r = 38+rowOff; r < H-1; r++) mapTileNum[c][r] = RUIN_FLOOR;
        for (int c = 32; c < W-2; c += 7) for (int r = 40+rowOff; r < H-3; r += 7) mapTileNum[c][r] = RUIN_WALL;
        for (int r = 38+rowOff; r < H-1; r++) mapTileNum[W-2][r] = RUIN_WALL;
        for (int c = 28; c < 45; c++) for (int r = 29+rowOff; r < 38+rowOff; r++) mapTileNum[c][r] = SAND;
        for (int c = 9; c < W-2; c++) for (int r = 28+rowOff; r <= 28+rowOff; r++) mapTileNum[c][r] = GRASS;
        for (int r = 26+rowOff; r < 38+rowOff; r++) mapTileNum[28][r] = PATH;
        for (int c = 9;  c < 45; c++) mapTileNum[c][37+rowOff]  = PATH;

        // --- 6. GTA-STYLE CABARET & DANCE CLUB INTERIOR (Cols 80-96, Rows 88-98) ---
        for (int c = 80; c <= 96; c++) {
            for (int r = 88; r <= 98; r++) {
                if (c == 80 || c == 96 || r == 88 || r == 98) {
                    mapTileNum[c][r] = WALL; // Perimeter walls
                } else {
                    mapTileNum[c][r] = RED_SAND; // Red velvet carpet
                }
            }
        }

        // Elevated Main Dance Stage in Center (Cols 86-90, Rows 90-93)
        for (int c = 86; c <= 90; c++) {
            for (int r = 90; r <= 93; r++) {
                mapTileNum[c][r] = WOOD_PLANK; // Wooden stage
            }
        }

        // Dance Stage Poles
        mapTileNum[87][91] = HITCHING_POST;
        mapTileNum[89][91] = HITCHING_POST;

        // Long Bar Counter on Left (Cols 82-83, Rows 90-94)
        for (int r = 90; r <= 94; r++) {
            mapTileNum[82][r] = STONE;
        }

        // VIP Lounge Boardwalks on Right (Cols 93-95, Rows 90-94)
        for (int c = 93; c <= 95; c++) {
            for (int r = 90; r <= 94; r++) {
                mapTileNum[c][r] = WOOD_PLANK;
            }
        }

        // Entrance / Exit Carpet Walkway & Door
        for (int r = 94; r <= 97; r++) {
            mapTileNum[88][r] = PATH;
        }
        mapTileNum[87][97] = PATH;
        mapTileNum[89][97] = PATH;
        mapTileNum[88][98] = PATH; // Exit doorway mat
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