package util;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class UtilityTool {
    public static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();
        return scaledImage;
    }

    public static Color blend(Color c1, Color c2, float ratio) {
        float ir = 1.0f - ratio;
        int r = (int)(c1.getRed() * ratio + c2.getRed() * ir);
        int g = (int)(c1.getGreen() * ratio + c2.getGreen() * ir);
        int b = (int)(c1.getBlue() * ratio + c2.getBlue() * ir);
        return new Color(
            Math.min(255, Math.max(0, r)),
            Math.min(255, Math.max(0, g)),
            Math.min(255, Math.max(0, b))
        );
    }

    /** Draw a rounded-rectangle "panel" with a border and fill */
    public static void drawPanel(Graphics2D g2, int x, int y, int w, int h, Color fill, Color border) {
        g2.setColor(fill);
        g2.fillRoundRect(x, y, w, h, 20, 20);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x, y, w, h, 20, 20);
        g2.setStroke(new BasicStroke(1));
    }
}
