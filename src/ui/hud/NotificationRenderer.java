package ui.hud;

import core.GamePanel;
import java.awt.*;

public class NotificationRenderer {

    private String notification = "";
    private int notifTimer = 0;
    private static final int NOTIF_DURATION = 180;

    public void showNotification(String msg) {
        notification = msg;
        notifTimer = NOTIF_DURATION;
    }
    
    public void update() {
        if (notifTimer > 0) notifTimer--;
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        if (notifTimer <= 0) return;
        
        float alpha = Math.min(1.0f, Math.min(notifTimer / 30f, (NOTIF_DURATION - notifTimer) / 30f + 0.1f));
        Composite orig = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int nw = fm.stringWidth(notification) + 20;
        int nx = gp.screenWidth / 2 - nw / 2;
        int ny = gp.screenHeight / 2 - 80;
        
        g2.setColor(new Color(20, 20, 60, 220));
        g2.fillRoundRect(nx, ny, nw, 26, 12, 12);
        
        g2.setColor(new Color(255, 220, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(nx, ny, nw, 26, 12, 12);
        
        g2.setColor(Color.WHITE);
        g2.drawString(notification, nx + 10, ny + 18);
        
        g2.setComposite(orig);
        g2.setStroke(new BasicStroke(1));
    }
}
