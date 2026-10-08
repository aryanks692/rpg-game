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
import java.util.LinkedList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    // Tile & screen settings
    public final int tileSize = 48;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768
    public final int screenHeight = tileSize * maxScreenRow; // 576

    // World size
    public final int maxWorldCol = 100;
    public final int maxWorldRow = 100;

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
    public Sound sound;
    public SaveManager saveManager;

    // Entities
    public Player player;
    public NPC[] npcs;
    public Enemy[] enemies;
    public SuperObject[] objects;
    public Entity currentDialogueEntity;

    // Combat effects and projectiles (LinkedList for O(1) removal on update)
    public List<DamageNumber> damageNumbers = new LinkedList<>();
    public List<Projectile> projectiles = new LinkedList<>();

    // State
    public GameState gameState = GameState.TITLE;

    // Background music tick (simple procedural sound)
    private long tick = 0;
    
    // Play time
    public long playTimeTicks = 0;
    public int currentSaveSlot = 0; // 0 = Auto-save, 1-4 = Manual
      // Map transition
    public int currentMap = 0; // 0 = Overworld, 1 = Dance Club Interior
    public final int maxMap = 2;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyHandler);
        setFocusable(true);
    }

    public void setupGame() {
        tileManager = new TileManager(this);
        collisionChecker = new CollisionChecker(this);
        camera = new Camera(screenWidth, screenHeight, maxWorldCol * tileSize, maxWorldRow * tileSize);
        questManager = new QuestManager(this);
        player = new Player(this, keyHandler);
        ui = new UI(this);
        saveManager = new SaveManager(this);
        assetSetter = new AssetSetter(this);
        assetSetter.setupNPCs();
        assetSetter.setupEnemies();
        assetSetter.setupObjects();
        
        sound = new Sound();
        sound.loadTrack("Title Theme", "/res/sound/title.mid");
        sound.loadTrack("Verdant Village", "/res/sound/village.mid");
        sound.loadTrack("Darkwood Forest", "/res/sound/forest.mid");
        sound.loadTrack("Great Savannah", "/res/sound/savannah.mid");
        sound.loadTrack("Dusty Gulch", "/res/sound/wildwest.mid");
        sound.loadTrack("Golden Meadows", "/res/sound/village.mid"); // Fallback
        sound.loadTrack("Crystal Caves", "/res/sound/cave.mid");
        sound.loadTrack("Ancient Ruins", "/res/sound/ancient_ruins.mid");
        
        sound.play("Title Theme");
    }
      public void enterDanceClub() {
        player.worldX = tileSize * 88; // Center of dance floor
        player.worldY = tileSize * 93; // Safe distance above exit door
        ui.showNotification("Entered the Wild West Dance Club!");
    }

    public void exitDanceClub() {
        player.worldX = tileSize * 85; // Outside club door in Dusty Gulch
        player.worldY = tileSize * 11; // Street level
        ui.showNotification("Exited to Dusty Gulch.");
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
                // System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        tick++;
        ui.update();

        switch (gameState) {
            case TITLE:
                if (keyHandler.enterJustPressed) {
                    if (saveManager.hasAnySave()) {
                        gameState = GameState.LOAD_MENU;
                    } else {
                        gameState = GameState.PLAY;
                    }
                    keyHandler.clearJustPressed();
                }
                if (keyHandler.newGameJustPressed) {
                    gameState = GameState.SAVE_MENU;
                    keyHandler.clearJustPressed();
                }
                break;
            case PLAY:
                playTimeTicks++;
                if (keyHandler.saveJustPressed) {
                    gameState = GameState.SAVE_MENU;
                    keyHandler.clearJustPressed();
                }
                player.update();
                camera.update(player);
                updateNPCs();
                updateEnemies();
                updateCombat();
                updateProjectiles();
                updateDamageNumbers();
                break;
            case PAUSE:
            case INVENTORY:
            case QUEST_LOG:
            case SAVE_MENU:
            case LOAD_MENU:
                // Player or UI handles key input for these states internally
                player.update();
                break;
            case DIALOGUE:
                if (currentDialogueEntity != null && currentDialogueEntity.awaitingChoice) {
                    if (keyHandler.yesJustPressed) {
                        int price = currentDialogueEntity.getEffectiveServiceCost();
                        int discount = player.getCreditDiscountPercent();
                        int hpBonus = player.getHpBonusPerPurchase();
                        if (currentDialogueEntity.tryPurchaseService()) {
                            player.life = Math.min(player.maxLife, player.life + hpBonus);
                            String tier = player.getCreditTier();
                            String notif = "Paid " + price + " gold for " + currentDialogueEntity.serviceName + "! +" + hpBonus + " HP";
                            if (discount > 0) notif += " (" + discount + "% " + tier + " Discount!)";
                            notif += " [Credit: " + player.creditScore + "]";
                            ui.showNotification(notif);
                            if (player.lastTierUp != null) {
                                String t = player.lastTierUp;
                                int nextTarget = 0;
                                if ("Bronze".equals(t))   nextTarget = Player.CREDIT_BRONZE;
                                if ("Silver".equals(t))   nextTarget = Player.CREDIT_SILVER;
                                if ("Gold".equals(t))     nextTarget = Player.CREDIT_GOLD;
                                if ("Platinum".equals(t)) nextTarget = Player.CREDIT_PLATINUM;
                                int d = player.getCreditDiscountPercent();
                                int h = player.getHpBonusPerPurchase();
                                ui.showNotification("\u2605\u2605\u2605 CREDIT TIER UP: " + t + "! -" + d + "% discount, +" + h + " HP per visit!");
                                if ("Platinum".equals(t)) {
                                    player.maxLife += 25;
                                    player.life = player.maxLife;
                                    player.attackDamage += 2;
                                    ui.showNotification("PLATINUM VIP PERK: +25 Max HP, +2 ATK, Full Heal!");
                                } else if ("Gold".equals(t)) {
                                    player.maxLife += 15;
                                    player.life = player.maxLife;
                                    ui.showNotification("GOLD PERK: +15 Max HP, Full Heal!");
                                } else if ("Silver".equals(t)) {
                                    player.potionCount += 2;
                                    ui.showNotification("SILVER PERK: +2 Free Potions!");
                                } else if ("Bronze".equals(t)) {
                                    player.potionCount += 1;
                                    ui.showNotification("BRONZE PERK: +1 Free Potion!");
                                }
                                player.lastTierUp = null;
                            }
                            currentDialogueEntity.dialogueIndex = currentDialogueEntity.choiceDialogueIndex + 1;
                            if (currentDialogueEntity.dialogueIndex >= currentDialogueEntity.dialogues.length) {
                                currentDialogueEntity.dialogueIndex = 0;
                                gameState = GameState.PLAY;
                            }
                        } else {
                            ui.showNotification("Not enough gold! You need " + price + " gold.");
                        }
                        keyHandler.clearJustPressed();
                    } else if (keyHandler.noJustPressed || keyHandler.interactJustPressed || keyHandler.enterJustPressed) {
                        currentDialogueEntity.declineService();
                        keyHandler.clearJustPressed();
                    }
                } else {
                    if (keyHandler.interactJustPressed || keyHandler.enterJustPressed) {
                        if (currentDialogueEntity != null) {
                            currentDialogueEntity.advanceDialogue();
                        }
                        keyHandler.clearJustPressed();
                    }
                }
                break;
            case GAME_OVER:
                if (keyHandler.enterJustPressed) {
                    resetGame();
                    keyHandler.clearJustPressed();
                }
                break;
            case DEATH:
                if (keyHandler.enterJustPressed) { // Restore
                    if (saveManager.hasAnySave()) {
                        gameState = GameState.LOAD_MENU;
                    } else {
                        resetGame();
                        gameState = GameState.PLAY;
                    }
                    keyHandler.clearJustPressed();
                }
                if (keyHandler.newGameJustPressed) { // Restart
                    resetGame();
                    gameState = GameState.TITLE;
                    keyHandler.clearJustPressed();
                }
                break;
            case WIN:
                if (keyHandler.enterJustPressed) {
                    resetGame();
                    keyHandler.clearJustPressed();
                }
                break;
        }
    }

    private void updateNPCs() {
        if (npcs == null)
            return;
        for (NPC npc : npcs) {
            if (npc != null)
                npc.update();
        }
    }

    private void updateEnemies() {
        if (enemies == null)
            return;
        for (Enemy e : enemies) {
            if (e != null && !e.readyToRemove)
                e.update();
        }
    }

    private void updateCombat() {
        if (enemies == null || player.activeHitboxes == null)
            return;

        Iterator<AttackHitbox> it = player.activeHitboxes.iterator();
        while (it.hasNext()) {
            AttackHitbox hb = it.next();
            if (!hb.update()) {
                it.remove();
                continue;
            }
            for (int i = 0; i < enemies.length; i++) {
                Enemy e = enemies[i];
                if (e == null || !e.alive || e.readyToRemove)
                    continue;
                if (hb.hasHit(i))
                    continue;
                if (hb.box.intersects(e.getWorldCollisionBox())) {
                    hb.markHit(i);
                    e.takeDamage(hb.damage);
                    damageNumbers.add(new DamageNumber(
                            e.worldX + 16, e.worldY, hb.damage, hb.crit));
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
                // Player's projectile hits enemies
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
            } else if (p.user != player && player != null && player.alive) {
                // Enemy's bullet hits player
                if (p.getWorldCollisionBox().intersects(player.getWorldCollisionBox())) {
                    p.alive = false;
                    if (player.blocking) {
                        // Shield blocks the bullet!
                        damageNumbers.add(new DamageNumber(player.worldX + 16, player.worldY, 0, false));
                    } else {
                        int dmg = Math.max(1, p.damage - player.defense);
                        player.takeDamage(dmg);
                        damageNumbers.add(new DamageNumber(player.worldX + 16, player.worldY, dmg, false));
                    }
                }
            }
        }
    }

    public void resetGame() {
        playTimeTicks = 0;
        setupGame();
        gameState = GameState.TITLE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ui == null)
            return;

        // Render everything into an offscreen buffer at the NATIVE resolution.
        // This ensures the UI always uses correct coordinates regardless of window size.
        BufferedImage offscreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = offscreen.createGraphics();

        // Render hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (gameState == GameState.TITLE) {
            ui.draw(g2);
        } else {
            // Draw world
            if (tileManager != null)
                tileManager.draw(g2);

            // Draw objects
            if (objects != null) {
                for (SuperObject obj : objects) {
                    if (obj != null)
                        obj.draw(g2);
                }
            }

            // Draw NPCs
            if (npcs != null) {
                for (NPC npc : npcs) {
                    if (npc != null)
                        npc.draw(g2);
                }
            }

            // Draw enemies
            if (enemies != null) {
                for (Enemy e : enemies) {
                    if (e != null && !e.readyToRemove)
                        e.draw(g2);
                }
            }

            // Draw player
            if (player != null)
                player.draw(g2);

            // Draw projectiles
            for (Projectile p : projectiles) {
                p.draw(g2, camera.x, camera.y);
            }

            // Draw damage numbers
            for (DamageNumber dn : damageNumbers) {
                dn.draw(g2, camera.x, camera.y);
            }

            // Draw UI on top
            if (ui != null)
                ui.draw(g2);
        }

        g2.dispose();

        // Now scale the offscreen buffer to fill the actual window
        Graphics2D wg = (Graphics2D) g;
        wg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        wg.drawImage(offscreen, 0, 0, getWidth(), getHeight(), null);
    }
}
