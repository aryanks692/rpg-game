package ui;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Queue-based notification system.
 * Notifications slide in from top-centre, hold, then fade out.
 * Up to 3 can be displayed simultaneously (stacked).
 */
public class NotificationManager {

    private static final int MAX_VISIBLE  = 3;
    private static final int HOLD_FRAMES  = UIConstants.NOTIF_DURATION;
    private static final int ANIM_FRAMES  = 20;   // slide + fade frames
    private static final int SLOT_HEIGHT  = 36;
    private static final int SLOT_GAP     = 6;

    private static class Notif {
        String text;
        int    timer;  // counts down from HOLD_FRAMES + ANIM_FRAMES*2
        float  yOff;   // slide offset (pixels, starts at -SLOT_HEIGHT)
        Notif(String text) {
            this.text  = text;
            this.timer = HOLD_FRAMES + ANIM_FRAMES * 2;
            this.yOff  = -SLOT_HEIGHT;
        }
        /** 0..1 alpha for fade */
        float alpha() {
            int total = HOLD_FRAMES + ANIM_FRAMES * 2;
            // Fade-in phase
            if (timer > total - ANIM_FRAMES) return (float)(total - timer) / ANIM_FRAMES;
            // Fade-out phase
            if (timer < ANIM_FRAMES) return (float) timer / ANIM_FRAMES;
            return 1f;
        }
        boolean done() { return timer <= 0; }
    }

    private final ArrayDeque<String> pending   = new ArrayDeque<>();
    private final List<Notif>        active     = new ArrayList<>();

    /** Enqueue a notification string. */
    public void show(String msg) {
        if (msg == null || msg.isEmpty()) return;
        if (active.size() < MAX_VISIBLE) {
            active.add(new Notif(msg));
        } else if (pending.size() < 6 && !pending.contains(msg)) {
            pending.offer(msg);
        }
    }

    /** Advance state each game tick. */
    public void update() {
        Iterator<Notif> it = active.iterator();
        while (it.hasNext()) {
            Notif n = it.next();
            n.timer--;
            // Slide in
            int total = HOLD_FRAMES + ANIM_FRAMES * 2;
            if (n.timer > total - ANIM_FRAMES) {
                n.yOff = -SLOT_HEIGHT * (1f - (float)(total - n.timer) / ANIM_FRAMES);
            } else {
                n.yOff = 0;
            }
            if (n.done()) {
                it.remove();
                if (!pending.isEmpty() && active.size() < MAX_VISIBLE) {
                    active.add(new Notif(pending.poll()));
                }
            }
        }
    }

    /** Draw all active notifications at top-centre of screen. */
    public void draw(Graphics2D g2, int screenW) {
        int baseY = 16;
        for (int i = 0; i < active.size(); i++) {
            Notif n = active.get(i);
            float alpha = n.alpha();

            g2.setFont(UIFonts.NOTIF);
            FontMetrics fm = g2.getFontMetrics();
            int nw = fm.stringWidth(n.text) + 32;
            int nx = screenW / 2 - nw / 2;
            int ny = baseY + (int) n.yOff + i * (SLOT_HEIGHT + SLOT_GAP);

            Composite orig = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // Background pill
            g2.setColor(UIConstants.COL_NOTIF_BG);
            g2.fillRoundRect(nx, ny, nw, SLOT_HEIGHT, 16, 16);
            // Border
            g2.setStroke(UIConstants.STROKE_BORDER);
            g2.setColor(UIConstants.COL_NOTIF_BORDER);
            g2.drawRoundRect(nx, ny, nw, SLOT_HEIGHT, 16, 16);
            // Icon ★
            g2.setColor(UIConstants.COL_GOLD);
            g2.setFont(UIFonts.SMALL_B);
            g2.drawString("★", nx + 8, ny + SLOT_HEIGHT - 10);
            // Text
            g2.setColor(UIConstants.COL_TEXT_MAIN);
            g2.setFont(UIFonts.NOTIF);
            g2.drawString(n.text, nx + 24, ny + SLOT_HEIGHT - 10);

            g2.setComposite(orig);
            g2.setStroke(UIConstants.STROKE_THIN);
        }
    }

    public boolean hasActive() { return !active.isEmpty(); }
}
