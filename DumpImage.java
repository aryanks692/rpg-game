import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DumpImage {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("src/res/npc/dancer.png"));
        int w = img.getWidth();
        int h = img.getHeight();
        System.out.println("Image Size: " + w + "x" + h);
        int cols = 32;
        int step = w / cols;
        for (int i=0; i<cols; i++) {
            int cx = i * step + step/2;
            boolean hasP = false;
            for(int y=0; y<h/4; y++) {
                int r = (img.getRGB(cx, y) >> 16) & 0xFF;
                if (r < 240) hasP = true;
            }
            System.out.print(hasP ? "#" : ".");
        }
        System.out.println();
    }
}
