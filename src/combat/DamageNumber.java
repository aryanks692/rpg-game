package combat;

import java.awt.*;

public class DamageNumber {
    public int x, y;
    public int value;
    public boolean crit;
    private int life = 60;
    private float vy = -2.5f;
    private float alpha = 1.0f;

    public DamageNumber(int x, int y, int value, boolean crit) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.crit = crit;
    }

    public boolean update() {
        y += vy;
        vy *= 0.97f;
        life--;
        if (life < 30) alpha = life / 30f;
        return life <= 0;
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        int sx = x - camX;
        int sy = y - camY;
        Composite orig = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        Font font = new Font("Arial", Font.BOLD, crit ? 22 : 16);
        g2.setFont(font);
        String text = crit ? "★" + value + "!" : String.valueOf(value);

        // Shadow
        g2.setColor(Color.BLACK);
        g2.drawString(text, sx + 1, sy + 1);
        // Text
        g2.setColor(crit ? new Color(255, 220, 0) : new Color(255, 80, 80));
        g2.drawString(text, sx, sy);

        g2.setComposite(orig);
    }
}
