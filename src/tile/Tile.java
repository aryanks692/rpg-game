package tile;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image;
    public boolean collision;
    public boolean animated;
    public BufferedImage[] frames;
    public int frameCount;
    public int animTimer;
    public int animSpeed = 30; // frames between animation steps

    public Tile() {}

    public BufferedImage getCurrentFrame() {
        if (!animated || frames == null) return image;
        animTimer++;
        if (animTimer >= animSpeed) {
            animTimer = 0;
            frameCount = (frameCount + 1) % frames.length;
        }
        return frames[frameCount];
    }
}
