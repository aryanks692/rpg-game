package ui;

import core.GamePanel;
import core.GameState;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * UIManager (keeps class name UI for compatibility).
 * Orchestrates all modular UI subsystems and delegates draw/update calls.
 */
public class UI {
    private final GamePanel gp;

    // Sub-components
    private final HUD hud;
    private final BossHUD bossHUD;
    private final MiniMap miniMap;
    private final DialogueUI dialogueUI;
    private final InventoryUI inventoryUI;
    private final QuestLogUI questLogUI;
    private final PauseMenu pauseMenu;
    private final TitleScreen titleScreen;
    private final SaveMenu saveMenu;
    private final DeathScreen deathScreen;
    private final WinScreen winScreen;
    private final NotificationManager notificationManager;

    public UI(GamePanel gp) {
        this.gp = gp;
        this.hud = new HUD(gp);
        this.bossHUD = new BossHUD(gp);
        this.miniMap = new MiniMap(gp);
        this.dialogueUI = new DialogueUI(gp);
        this.inventoryUI = new InventoryUI(gp);
        this.questLogUI = new QuestLogUI(gp);
        this.pauseMenu = new PauseMenu(gp);
        this.titleScreen = new TitleScreen(gp);
        this.saveMenu = new SaveMenu(gp);
        this.deathScreen = new DeathScreen(gp);
        this.winScreen = new WinScreen(gp);
        this.notificationManager = new NotificationManager();
    }

    public void showNotification(String msg) {
        notificationManager.show(msg);
    }

    public void update() {
        notificationManager.update();
        hud.update();
        bossHUD.update();

        switch (gp.gameState) {
            case TITLE:
                titleScreen.update();
                break;
            case PAUSE:
                pauseMenu.update();
                break;
            case DIALOGUE:
                dialogueUI.update();
                break;
            case SAVE_MENU:
            case LOAD_MENU:
                saveMenu.update();
                break;
            default:
                break;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (gp.gameState) {
            case TITLE:
                titleScreen.draw(g2);
                break;
            case PLAY:
                drawPlayBase(g2);
                break;
            case PAUSE:
                drawPlayBase(g2);
                pauseMenu.draw(g2);
                break;
            case DIALOGUE:
                drawPlayBase(g2);
                dialogueUI.draw(g2);
                break;
            case INVENTORY:
                drawPlayBase(g2);
                inventoryUI.draw(g2);
                break;
            case QUEST_LOG:
                drawPlayBase(g2);
                questLogUI.draw(g2);
                break;
            case GAME_OVER:
                drawGameOver(g2);
                break;
            case DEATH:
                deathScreen.draw(g2);
                break;
            case WIN:
                winScreen.draw(g2);
                break;
            case SAVE_MENU:
            case LOAD_MENU:
                saveMenu.draw(g2);
                break;
        }
    }

    private void drawPlayBase(Graphics2D g2) {
        hud.draw(g2);
        miniMap.draw(g2);
        bossHUD.draw(g2);
        notificationManager.draw(g2, gp.screenWidth);
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        String text = "GAME OVER";
        UIPanel.drawShadowTextCentered(g2, text, UIFonts.DEATH, new Color(200, 30, 30), gp.screenWidth / 2, gp.screenHeight / 2 - 20);
        
        String subtext = "Press ENTER to return to title";
        UIPanel.drawShadowTextCentered(g2, subtext, UIFonts.HUD, Color.WHITE, gp.screenWidth / 2, gp.screenHeight / 2 + 40);
    }
}
