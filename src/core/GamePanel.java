package core;

import camera.Camera;
import combat.*;
import entity.*;
import entity.enemy.Enemy;
import object.SuperObject;
import quest.QuestManager;
import tile.TileManager;
import ui.UI;
import util.AssetSetter;
import util.CollisionChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    // Tile & screen settings
    public final int tileSize = 48;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth  = tileSize * maxScreenCol; // 768
    public final int screenHeight = tileSize * maxScreenRow; // 576

    // World size
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // Target FPS
    private final int FPS = 60;
    private Thread gameThread;

    // Core systems
    public KeyHandler keyHandler = new KeyHandler();
    public TileManager tileManager;
    public CollisionChecker collisionChecker;
    public Camera camera;
    public AssetSetter assetSetter;
    public QuestManager questManager;
    public UI ui;

    // Entities
    public Player player;
    public NPC[] npcs;
    public Enemy[] enemies;
    public SuperObject[] objects;
    public NPC currentNPC;

    // Combat effects and projectiles
    public List<DamageNumber> damageNumbers = new ArrayList<>();
    public List<Projectile> projectiles = new ArrayList<>();

    // State
    public GameState gameState = GameState.TITLE;

    // Background music tick (simple procedural sound)
    private long tick = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyHandler);
        setFocusable(true);
    }

    public void setupGame() {
        tileManager     = new TileManager(this);
        collisionChecker = new CollisionChecker(this);
        camera           = new Camera(screenWidth, screenHeight, maxWorldCol * tileSize, maxWorldRow * tileSize);
        questManager     = new QuestManager();
        player           = new Player(this, keyHandler);
        ui               = new UI(this);
        assetSetter      = new AssetSetter(this);
        assetSetter.setupNPCs();
        assetSetter.setupEnemies();
        assetSetter.setupObjects();
    }

    public void startGameThread() {
        setupGame();
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }
            if (timer >= 1_000_000_000) {
                //System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        tick++;
        ui.update();

        switch (gameState) {
            case TITLE -> {
                if (keyHandler.enterJustPressed) {
                    gameState = GameState.PLAY;
                    keyHandler.clearJustPressed();
                }
            }
            case PLAY -> {
                player.update();
                camera.update(player);
                updateNPCs();
                updateEnemies();
                updateCombat();
                updateProjectiles();
                updateDamageNumbers();
            }
            case PAUSE, INVENTORY, QUEST_LOG -> {
                // Player handles key input for these states internally
                player.update();
            }
            case DIALOGUE -> {
                if (keyHandler.interactJustPressed || keyHandler.enterJustPressed) {
                    if (currentNPC != null) {
                        currentNPC.advanceDialogue();
                    }
                    keyHandler.clearJustPressed();
                }
            }
            case GAME_OVER -> {
                if (keyHandler.enterJustPressed) {
                    resetGame();
                    keyHandler.clearJustPressed();
                }
            }
            case WIN -> {
                if (keyHandler.enterJustPressed) {
                    resetGame();
                    keyHandler.clearJustPressed();
                }
            }
        }
    }

    private void updateNPCs() {
        if (npcs == null) return;
        for (NPC npc : npcs) {
            if (npc != null) npc.update();
        }
    }

    private void updateEnemies() {
        if (enemies == null) return;
        for (Enemy e : enemies) {
            if (e != null && !e.readyToRemove) e.update();
        }
    }

    private void updateCombat() {
        if (enemies == null || player.activeHitboxes == null) return;

        Iterator<AttackHitbox> it = player.activeHitboxes.iterator();
        while (it.hasNext()) {
            AttackHitbox hb = it.next();
            if (!hb.update()) {
                it.remove();
                continue;
            }
            for (int i = 0; i < enemies.length; i++) {
                Enemy e = enemies[i];
                if (e == null || !e.alive || e.readyToRemove) continue;
                if (hb.hasHit(i)) continue;
                if (hb.box.intersects(e.getWorldCollisionBox())) {
                    hb.markHit(i);
                    e.takeDamage(hb.damage);
                    damageNumbers.add(new DamageNumber(
                        e.worldX + 16, e.worldY, hb.damage, hb.crit
                    ));
                    ui.showNotification(hb.crit ? "Critical Hit! -" + hb.damage : "-" + hb.damage);
                }
            }
        }
    }

    private void updateDamageNumbers() {
        damageNumbers.removeIf(DamageNumber::update);
    }

    private void updateProjectiles() {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update(this);
            if (!p.alive) {
                it.remove();
                continue;
            }
            if (p.user == player && enemies != null) {
                for (int i = 0; i < enemies.length; i++) {
                    Enemy e = enemies[i];
                    if (e != null && e.alive && !e.readyToRemove) {
                        if (p.getWorldCollisionBox().intersects(e.getWorldCollisionBox())) {
                            p.alive = false;
                            e.takeDamage(p.damage);
                            damageNumbers.add(new DamageNumber(e.worldX + 16, e.worldY, p.damage, false));
                            break;
                        }
                    }
                }
            }
        }
    }

    private void resetGame() {
        setupGame();
        gameState = GameState.TITLE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ui == null) return;
        Graphics2D g2 = (Graphics2D) g;

        // Render hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (gameState == GameState.TITLE) {
            ui.draw(g2);
            g2.dispose();
            return;
        }

        // Draw world
        if (tileManager != null) tileManager.draw(g2);

        // Draw objects
        if (objects != null) {
            for (SuperObject obj : objects) {
                if (obj != null) obj.draw(g2);
            }
        }

        // Draw NPCs
        if (npcs != null) {
            for (NPC npc : npcs) {
                if (npc != null) npc.draw(g2);
            }
        }

        // Draw enemies
        if (enemies != null) {
            for (Enemy e : enemies) {
                if (e != null && !e.readyToRemove) e.draw(g2);
            }
        }

        // Draw player
        if (player != null) player.draw(g2);

        // Draw projectiles
        for (Projectile p : projectiles) {
            p.draw(g2, camera.x, camera.y);
        }

        // Draw damage numbers
        for (DamageNumber dn : damageNumbers) {
            dn.draw(g2, camera.x, camera.y);
        }

        // Draw UI on top
        if (ui != null) ui.draw(g2);

        g2.dispose();
    }
}
