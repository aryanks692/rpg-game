package util;

import core.GamePanel;
import entity.Entity;
import tile.TileManager;
import java.awt.Rectangle;

public class CollisionChecker {
    private GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int ts = gp.tileSize;
        Rectangle cb = entity.collisionBox;

        int entityLeft   = entity.worldX + cb.x;
        int entityRight  = entity.worldX + cb.x + cb.width;
        int entityTop    = entity.worldY + cb.y;
        int entityBottom = entity.worldY + cb.y + cb.height;

        int colLeft   = entityLeft / ts;
        int colRight  = entityRight / ts;
        int rowTop    = entityTop / ts;
        int rowBottom = entityBottom / ts;

        int tileNum1, tileNum2;
        entity.collisionOn = false;

        switch (entity.direction) {
            case "up":
                rowTop = (entityTop - entity.speed) / ts;
                tileNum1 = getTile(colLeft, rowTop);
                tileNum2 = getTile(colRight, rowTop);
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "down":
                rowBottom = (entityBottom + entity.speed) / ts;
                tileNum1 = getTile(colLeft, rowBottom);
                tileNum2 = getTile(colRight, rowBottom);
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "left":
                colLeft = (entityLeft - entity.speed) / ts;
                tileNum1 = getTile(colLeft, rowTop);
                tileNum2 = getTile(colLeft, rowBottom);
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
            case "right":
                colRight = (entityRight + entity.speed) / ts;
                tileNum1 = getTile(colRight, rowTop);
                tileNum2 = getTile(colRight, rowBottom);
                if (isSolid(tileNum1) || isSolid(tileNum2)) entity.collisionOn = true;
                break;
        }
    }

    private int getTile(int col, int row) {
        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) return -1;
        return gp.tileManager.mapTileNum[col][row];
    }

    private boolean isSolid(int tileId) {
        if (tileId < 0 || tileId >= gp.tileManager.tiles.length) return true;
        if (gp.tileManager.tiles[tileId] == null) return false;
        return gp.tileManager.tiles[tileId].collision;
    }

    /** Check entity-entity collision. Returns the index of the entity hit, or -1. */
    public int checkEntity(Entity entity, Entity[] targets) {
        int index = -1;
        if (targets == null) return index;
        Rectangle aBox = entity.getWorldCollisionBox();
        for (int i = 0; i < targets.length; i++) {
            Entity t = targets[i];
            if (t == null || !t.alive) continue;
            Rectangle bBox = t.getWorldCollisionBox();
            if (aBox.intersects(bBox)) {
                entity.collisionOn = true;
                index = i;
            }
        }
        return index;
    }
        /**
     * Checks if moving into a solid building or object (e.g. Dance Club, Saloon, Houses, Barrels).
     * Returns the object index if hit, or -1 if no solid collision.
     */
    public int checkObject(Entity entity, boolean isPlayer) {
        int index = -1;
        if (gp.objects == null) return index;

        Rectangle nextBox = new Rectangle(
            entity.worldX + entity.collisionBox.x,
            entity.worldY + entity.collisionBox.y,
            entity.collisionBox.width,
            entity.collisionBox.height
        );

        // Predict next position based on direction
        switch (entity.direction) {
            case "up":    nextBox.y -= entity.speed; break;
            case "down":  nextBox.y += entity.speed; break;
            case "left":  nextBox.x -= entity.speed; break;
            case "right": nextBox.x += entity.speed; break;
        }

        for (int i = 0; i < gp.objects.length; i++) {
            object.SuperObject obj = gp.objects[i];
            if (obj == null || obj.pickedUp) continue;

            // Only block solid objects (buildings, barrels, chests, etc. — ignore collectible floor items)
            if (!(obj instanceof object.OBJ_Potion || obj instanceof object.OBJ_Sword || obj instanceof object.OBJ_Shield)) {
                if (nextBox.intersects(obj.getWorldCollisionBox())) {
                    entity.collisionOn = true;
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
}
