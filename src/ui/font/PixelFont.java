package ui.font;

import java.awt.*;
import java.io.InputStream;

public class PixelFont {

    // Currently we will map this to standard fonts to avoid crashing without actual .ttf files,
    // but the architecture is ready for a real font loader.
    public static Font getFont(float size) {
        // Placeholder for loading a custom pixel font (e.g. PixelFont.ttf)
        return new Font("Arial", Font.BOLD, (int)size);
    }
}
