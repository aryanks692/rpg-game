package entity;

import core.GamePanel;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageNPC extends NPC {
    private String imagePath;
    private int danceTimer = 0;
    private int danceYOffset = 0;

    public ImageNPC(GamePanel gp, String name, String imagePath, int worldX, int worldY, String... dialogues) {
        super(gp, name, "image_based", worldX, worldY, dialogues);
        this.imagePath = imagePath;
        // The super constructor calls buildSprites() which draws the vector shapes.
        // We immediately overwrite those arrays by loading the actual image tile sheet.
        loadSpriteSheet();
    }

    private void loadSpriteSheet() {
        try {
            // Load the image from the hard drive
            BufferedImage originalSheet = ImageIO.read(new File(imagePath));
            
            // Convert to ARGB so we can manipulate transparency
            BufferedImage sheet = new BufferedImage(originalSheet.getWidth(), originalSheet.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = sheet.createGraphics();
            g2d.drawImage(originalSheet, 0, 0, null);
            g2d.dispose();
            
            // Remove the white background algorithmically
            removeWhiteBackground(sheet);
            
            // To prevent "double-layer" sprites from uneven AI-generated grids, 
            // we dynamically isolate the FIRST character on the top row.
            int height = sheet.getHeight();
            int width = sheet.getWidth();
            int rowHeight = height / 4;
            
            int startX = -1;
            int endX = -1;
            
            for (int x = 0; x < width; x++) {
                boolean hasPixel = false;
                for (int y = 0; y < rowHeight; y++) {
                    int alpha = (sheet.getRGB(x, y) >> 24) & 0xFF;
                    if (alpha > 0) {
                        hasPixel = true;
                        break;
                    }
                }
                
                if (hasPixel && startX == -1) {
                    startX = x;
                } else if (!hasPixel && startX != -1 && endX == -1) {
                    // Check ahead 10 pixels to confirm it's a real gap between different character draws
                    boolean isRealGap = true;
                    for (int ahead = 1; ahead < 10; ahead++) {
                        if (x + ahead >= width) break;
                        for (int y = 0; y < rowHeight; y++) {
                            int alpha = (sheet.getRGB(x + ahead, y) >> 24) & 0xFF;
                            if (alpha > 0) {
                                isRealGap = false;
                                break;
                            }
                        }
                        if (!isRealGap) break;
                    }
                    if (isRealGap) {
                        endX = x;
                    }
                }
            }
            
            if (startX != -1 && endX == -1) endX = width;
            if (startX == -1) { startX = 0; endX = width / 4; } // Fallback
            
            BufferedImage singleFrame = sheet.getSubimage(startX, 0, endX - startX, rowHeight);

            for (int i = 0; i < 4; i++) {
                walkDown[i]  = singleFrame;
                walkUp[i]    = singleFrame;
                walkLeft[i]  = singleFrame;
                walkRight[i] = singleFrame;
            }
            image = walkDown[0];
            
        } catch (IOException e) {
            System.err.println("Failed to load ImageNPC sprite sheet: " + imagePath);
            e.printStackTrace();
        }
    }

    private void removeWhiteBackground(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = img.getRGB(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                
                // If the pixel is very close to pure white, make it transparent
                if (r > 235 && g > 235 && b > 235) {
                    img.setRGB(x, y, 0x00000000); // Fully transparent
                }
            }
        }
    }

    @Override
    public void update() {
        // Do not call super.update() so she never wanders around the map or moves!
        // Instead, we will increment a dance timer to bob her up and down
        danceTimer++;
        if (danceTimer % 40 < 20) {
            danceYOffset = -3; // Bob up slightly
        } else {
            danceYOffset = 0;  // Bob down
        }
    }

    @Override
    public void draw(java.awt.Graphics2D g2) {
        // Temporarily adjust her world position before letting the super class draw the frame
        this.worldY += danceYOffset;
        super.draw(g2);
        this.worldY -= danceYOffset; // Restore it instantly so hitboxes aren't broken
    }
}
