package ui;

import core.GamePanel;
import core.GameState;
import entity.Player;
import ui.hud.HUDRenderer;
import ui.hud.NotificationRenderer;
import ui.menu.*;
import java.awt.*;

public class UI {
    private GamePanel gp;
    
    // Renderers
    private HUDRenderer hudRenderer = new HUDRenderer();
    private NotificationRenderer notificationRenderer = new NotificationRenderer();
    private TitleScreen titleScreen = new TitleScreen();
    private PauseMenu pauseMenu = new PauseMenu();
    private InventoryMenu inventoryMenu = new InventoryMenu();
    private QuestLogMenu questLogMenu = new QuestLogMenu();
    private SaveLoadMenu saveLoadMenu = new SaveLoadMenu();
    private DialogueBox dialogueBox = new DialogueBox();
    private DeathScreen deathScreen = new DeathScreen();
    private WinScreen winScreen = new WinScreen();
    private GameOverScreen gameOverScreen = new GameOverScreen();

    // Shared UI state
    private int titleTimer = 0;
    private int saveSlotCommandNum = 1;

    public UI(GamePanel gp) {
        this.gp = gp;
    }

    public void showNotification(String msg) {
        notificationRenderer.showNotification(msg);
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (gp.gameState) {
            case TITLE:     titleScreen.draw(g2, gp, titleTimer); break;
            case PLAY:      hudRenderer.draw(g2, gp); notificationRenderer.draw(g2, gp); break;
            case PAUSE:     hudRenderer.draw(g2, gp); pauseMenu.draw(g2, gp); break;
            case DIALOGUE:  hudRenderer.draw(g2, gp); dialogueBox.draw(g2, gp, titleTimer); break;
            case INVENTORY: hudRenderer.draw(g2, gp); inventoryMenu.draw(g2, gp); break;
            case QUEST_LOG: hudRenderer.draw(g2, gp); questLogMenu.draw(g2, gp); break;
            case GAME_OVER: gameOverScreen.draw(g2, gp); break;
            case DEATH:     deathScreen.draw(g2, gp); break;
            case WIN:       winScreen.draw(g2, gp); break;
            case SAVE_MENU: saveLoadMenu.draw(g2, gp, true, saveSlotCommandNum); break;
            case LOAD_MENU: saveLoadMenu.draw(g2, gp, false, saveSlotCommandNum); break;
        }
    }

    public void update() {
        notificationRenderer.update();
        titleTimer++;
        
        // Check game over / win
        Player p = gp.player;
        if (p != null && !p.alive && gp.gameState == GameState.PLAY) {
            gp.gameState = GameState.DEATH;
        }

        // Handle Save/Load Menu Navigation
        if (gp.gameState == GameState.SAVE_MENU || gp.gameState == GameState.LOAD_MENU) {
            int minSlot = (gp.gameState == GameState.SAVE_MENU) ? 1 : 0;
            if (gp.keyHandler.upPressed) {
                saveSlotCommandNum--;
                if (saveSlotCommandNum < minSlot) saveSlotCommandNum = 4;
                gp.keyHandler.upPressed = false;
            }
            if (gp.keyHandler.downPressed) {
                saveSlotCommandNum++;
                if (saveSlotCommandNum > 4) saveSlotCommandNum = minSlot;
                gp.keyHandler.downPressed = false;
            }
            if (gp.keyHandler.enterJustPressed) {
                if (gp.gameState == GameState.SAVE_MENU) {
                    gp.currentSaveSlot = saveSlotCommandNum;
                    gp.saveManager.save(saveSlotCommandNum);
                    gp.gameState = GameState.PLAY;
                } else if (gp.gameState == GameState.LOAD_MENU) {
                    if (gp.saveManager.hasSave(saveSlotCommandNum)) {
                        gp.currentSaveSlot = saveSlotCommandNum;
                        gp.saveManager.load(saveSlotCommandNum);
                        gp.gameState = GameState.PLAY;
                    } else {
                        showNotification("Slot empty!");
                    }
                }
                gp.keyHandler.clearJustPressed();
            }
            if (gp.keyHandler.pauseJustPressed) {
                gp.gameState = (gp.player.alive && gp.playTimeTicks > 0) ? GameState.PLAY : GameState.TITLE;
                gp.keyHandler.clearJustPressed();
            }
        }
    }
}
