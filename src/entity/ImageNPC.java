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
        this(gp, name, imagePath, null, worldX, worldY, dialogues);
    }

    public ImageNPC(GamePanel gp, String name, String imagePath, String portraitPath, int worldX, int worldY, String... dialogues) {
        super(gp, name, "image_based", worldX, worldY, dialogues);
        this.imagePath = imagePath;
        if (portraitPath != null) {
            try {
                this.portraitImage = ImageIO.read(new File(portraitPath));
            } catch (Exception e) {
                System.err.println("Failed to load portrait: " + portraitPath);
            }
        }
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
            
            // Remove green chromakey and white background
            removeBackgrounds(sheet);
            
            // Auto crop / detect the bounding box of the single dancer character
            int minX = sheet.getWidth(), minY = sheet.getHeight(), maxX = 0, maxY = 0;
            for (int y = 0; y < sheet.getHeight(); y++) {
                for (int x = 0; x < sheet.getWidth(); x++) {
                    int alpha = (sheet.getRGB(x, y) >> 24) & 0xFF;
                    if (alpha > 0) {
                        if (x < minX) minX = x;
                        if (x > maxX) maxX = x;
                        if (y < minY) minY = y;
                        if (y > maxY) maxY = y;
                    }
                }
            }

            BufferedImage singleFrame;
            if (maxX > minX && maxY > minY) {
                singleFrame = sheet.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
            } else {
                singleFrame = sheet;
            }

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

    private void removeBackgrounds(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = img.getRGB(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                
                // Pure Green / Chroma key removal
                if (g > 180 && r < 120 && b < 120) {
                    img.setRGB(x, y, 0x00000000);
                }
                // Checkerboard / Pure White background removal
                else if (r > 230 && g > 230 && b > 230) {
                    img.setRGB(x, y, 0x00000000);
                }
                // Grey checkerboard squares removal
                else if (r > 195 && g > 195 && b > 195 && Math.abs(r - g) < 5 && Math.abs(g - b) < 5) {
                    img.setRGB(x, y, 0x00000000);
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
