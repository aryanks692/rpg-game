package camera;

import entity.Player;

public class Camera {
    public int x, y;
    private final int screenWidth;
    private final int screenHeight;
    private final int worldWidth;
    private final int worldHeight;

    public Camera(int screenWidth, int screenHeight, int worldWidth, int worldHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(Player player) {
        // Smooth follow
        int targetX = player.worldX - screenWidth / 2 + player.width / 2;
        int targetY = player.worldY - screenHeight / 2 + player.height / 2;

        // Smooth lerp
        x += (targetX - x) / 6;
        y += (targetY - y) / 6;

        // Clamp to world bounds
        x = Math.max(0, Math.min(x, worldWidth - screenWidth));
        y = Math.max(0, Math.min(y, worldHeight - screenHeight));
    }
}
