package object;

import core.GamePanel;
import core.GameState;
import core.KeyHandler;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Rune Puzzle — an ancient rune altar.
 *
 * Interacting with it opens a sorting mini-game: swap NEIGHBOURING runes until
 * the row is in ascending order. The "Chaos" meter shows the number of
 * INVERSIONS in the current row — and the minimum number of adjacent swaps
 * needed to solve it equals that count. The count is computed with a MERGE SORT
 * in O(n log n), the classic merge-sort inversion-counting trick.
 */
public class OBJ_RunePuzzle extends SuperObject {

    private static final int N = 7;                 // number of rune stones
    public static final int GOLD_REWARD = 150;
    public static final int XP_REWARD   = 80;

    private final int[] runes = new int[N];
    private int selected = 0;
    public boolean solved = false;

    // Held-key edge detection for the overlay
    private boolean prevLeft, prevRight;

    public OBJ_RunePuzzle(GamePanel gp) {
        super(gp);
        name = "Ancient Rune Altar";
        collision = true;
        buildRunes();
        createImage();
    }

    /** Scramble a permutation of 1..N with random adjacent swaps (always solvable). */
    private void buildRunes() {
        java.util.Random rng = new java.util.Random();
        do {
            for (int i = 0; i < N; i++) runes[i] = i + 1;
            for (int s = 0; s < N * 4; s++) {
                int i = rng.nextInt(N - 1);
                int t = runes[i]; runes[i] = runes[i + 1]; runes[i + 1] = t;
            }
        } while (countInversions(runes) == 0); // never start already solved
    }

    private void createImage() {
        int ts = gp.tileSize;
        image = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color stone   = new Color(122, 124, 134);
        Color stoneDk = new Color(78, 80, 90);
        Color glow    = solved ? new Color(120, 230, 140) : new Color(150, 120, 235);

        // ground shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(6, ts - 12, ts - 12, 8);

        // stone monolith
        g.setColor(stone);
        g.fillRoundRect(9, 4, ts - 18, ts - 12, 8, 8);
        g.setColor(stoneDk);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(9, 4, ts - 18, ts - 12, 8, 8);

        // carved glow-runes
        g.setColor(glow);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(15, 16, ts - 15, 16);
        g.drawLine(ts / 2, 22, ts / 2, ts - 18);
        g.drawLine(16, ts - 18, ts - 16, ts - 18);
        g.fillOval(ts / 2 - 4, 24, 8, 8);

        g.dispose();
    }

    @Override
    public void draw(Graphics2D g2) {
        if (pickedUp) return;
        super.draw(g2);

        int ts = gp.tileSize;
        int pcol = (gp.player.worldX + gp.player.width  / 2) / ts;
        int prow = (gp.player.worldY + gp.player.height / 2) / ts;
        int col  = worldX / ts;
        int row  = worldY / ts;
        boolean near = Math.abs(pcol - col) <= 1 && Math.abs(prow - row) <= 1;
        if (!near) return;

        int screenX = worldX - gp.camera.x;
        int screenY = worldY - gp.camera.y;
        String label = solved ? "[E] Rune Altar (solved)" : "[E] Solve Rune Puzzle";
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(label) + 12;
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRoundRect(screenX + ts / 2 - w / 2, screenY - 20, w, 16, 6, 6);
        g2.setColor(Color.YELLOW);
        g2.drawString(label, screenX + ts / 2 - fm.stringWidth(label) / 2, screenY - 8);
    }

    @Override
    public void onPickup(entity.Player player) {
        gp.openRunePuzzle(this);
    }

    // ------------------------------------------------------------------
    // Puzzle overlay (drawn by GamePanel while gameState == RUNE_PUZZLE)
    // ------------------------------------------------------------------

    public void handleInput(KeyHandler key) {
        if (solved) {
            if (key.interactJustPressed || key.enterJustPressed || key.pauseJustPressed) {
                closePuzzle();
                key.clearJustPressed();
            }
            return;
        }

        // cursor movement (edge-detected so holding doesn't fly past)
        if (key.leftPressed  && !prevLeft)  selected = (selected + N - 1) % N;
        if (key.rightPressed && !prevRight) selected = (selected + 1) % N;
        prevLeft  = key.leftPressed;
        prevRight = key.rightPressed;

        if (key.interactJustPressed || key.enterJustPressed) {
            swapWithRight();
            key.clearJustPressed();
        } else if (key.pauseJustPressed || key.questJustPressed) {
            closePuzzle();
            key.clearJustPressed();
        }
    }

    private void swapWithRight() {
        if (selected >= N - 1) return;         // nothing to the right
        int t = runes[selected];
        runes[selected] = runes[selected + 1];
        runes[selected + 1] = t;

        if (countInversions(runes) == 0) onSolved();
    }

    private void onSolved() {
        solved = true;
        createImage();
        gp.player.gold += GOLD_REWARD;
        gp.player.gainXP(XP_REWARD);
        gp.ui.showNotification("Rune Altar solved! " + GOLD_REWARD + " gold + " + XP_REWARD + " XP.");
    }

    private void closePuzzle() {
        gp.activePuzzle = null;
        gp.gameState = GameState.PLAY;
    }

    public void drawOverlay(Graphics2D g2) {
        int W = gp.screenWidth, H = gp.screenHeight;

        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, W, H);

        int panelW = 520, panelH = 300;
        int px = (W - panelW) / 2, py = (H - panelH) / 2;

        g2.setColor(new Color(28, 24, 40));
        g2.fillRoundRect(px, py, panelW, panelH, 18, 18);
        g2.setColor(solved ? new Color(120, 230, 140) : new Color(150, 120, 235));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(px, py, panelW, panelH, 18, 18);

        FontMetrics fm;
        g2.setColor(new Color(230, 220, 255));
        g2.setFont(new Font("Georgia", Font.BOLD, 22));
        String title = "The Rune Altar";
        fm = g2.getFontMetrics();
        g2.drawString(title, W / 2 - fm.stringWidth(title) / 2, py + 38);

        g2.setColor(new Color(190, 185, 210));
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        String sub = "Arrange the runes ascending. You may only swap neighbours.";
        fm = g2.getFontMetrics();
        g2.drawString(sub, W / 2 - fm.stringWidth(sub) / 2, py + 60);

        // rune row
        int slot = 56, gap = 10;
        int totalW = N * slot + (N - 1) * gap;
        int startX = W / 2 - totalW / 2;
        int rowY = py + 92;
        for (int i = 0; i < N; i++) {
            int x = startX + i * (slot + gap);
            boolean sel = (i == selected) && !solved;

            g2.setColor(sel ? new Color(70, 60, 110) : new Color(44, 40, 60));
            g2.fillRoundRect(x, rowY, slot, slot, 12, 12);
            g2.setColor(sel ? Color.YELLOW : new Color(120, 115, 150));
            g2.setStroke(new BasicStroke(sel ? 3 : 2));
            g2.drawRoundRect(x, rowY, slot, slot, 12, 12);

            g2.setFont(new Font("Georgia", Font.BOLD, 26));
            g2.setColor(solved ? new Color(150, 240, 160) : Color.WHITE);
            fm = g2.getFontMetrics();
            String v = String.valueOf(runes[i]);
            g2.drawString(v, x + slot / 2 - fm.stringWidth(v) / 2, rowY + slot / 2 + 9);
        }

        // chaos (inversion) meter
        int inv = countInversions(runes);
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        g2.setColor(new Color(255, 160, 120));
        String chaos = "Chaos (inversions): " + inv;
        fm = g2.getFontMetrics();
        g2.drawString(chaos, W / 2 - fm.stringWidth(chaos) / 2, rowY + slot + 30);

        // help
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.setColor(new Color(200, 195, 225));
        String help = solved
                ? "SOLVED!  Press [E] to leave."
                : "[<] [>] choose rune     [E] swap right     [ESC] leave";
        fm = g2.getFontMetrics();
        g2.drawString(help, W / 2 - fm.stringWidth(help) / 2, py + panelH - 22);
    }

    // ------------------------------------------------------------------
    // MERGE SORT inversion count  ->  minimum adjacent swaps to sort
    // ------------------------------------------------------------------

    private int countInversions(int[] src) {
        int[] a = src.clone();
        int[] buf = new int[a.length];
        return (int) mergeCount(a, buf, 0, a.length - 1);
    }

    private long mergeCount(int[] a, int[] buf, int lo, int hi) {
        if (lo >= hi) return 0;
        int mid = (lo + hi) >>> 1;
        long inv = mergeCount(a, buf, lo, mid) + mergeCount(a, buf, mid + 1, hi);

        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            if (a[i] <= a[j]) {
                buf[k++] = a[i++];
            } else {
                inv += (mid - i + 1);   // every remaining left element is inverted
                buf[k++] = a[j++];
            }
        }
        while (i <= mid) buf[k++] = a[i++];
        while (j <= hi)  buf[k++] = a[j++];
        for (int t = lo; t <= hi; t++) a[t] = buf[t];
        return inv;
    }
}
