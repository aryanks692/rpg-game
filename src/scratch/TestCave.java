package scratch;

import core.GamePanel;

public class TestCave {
    public static void main(String[] args) {
        try {
            System.out.println("Initializing GamePanel...");
            GamePanel gp = new GamePanel();
            gp.setupGame();
            
            // Move player to Crystal Caves: row = 70, col = 5
            gp.player.worldX = 5 * gp.tileSize;
            gp.player.worldY = 70 * gp.tileSize;
            
            System.out.println("Updating game to trigger zone transition...");
            // Let's run a few updates
            for (int i = 0; i < 5; i++) {
                gp.update();
            }
            System.out.println("Simulation finished with NO errors!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
