package ui.hud;

import core.GamePanel;
import ui.components.UIPanel;
import java.awt.*;

public class BossBarRenderer {

    public void draw(Graphics2D g2, GamePanel gp) {
        if (gp.enemies == null) return;
        
        for (entity.enemy.Enemy e : gp.enemies) {
            if (e != null && e.isBoss && e.aiState != entity.enemy.Enemy.State.PATROL && e.alive) {
                // Massive Bottom Bar
                int bw = gp.screenWidth - 100;
                int bh = 22;
                int bx = gp.screenWidth / 2 - bw / 2;
                int by = gp.screenHeight - bh - 60;

                // 1. Background Panel
                UIPanel.draw(g2, bx - 10, by - 30, bw + 20, bh + 45);
                
                // 2. Boss Name
                g2.setFont(new Font("Georgia", Font.BOLD, 18));
                g2.setColor(new Color(255, 220, 80));
                g2.drawString(e.name, bx + 5, by - 10);

                // 3. Health Bar Shadow
                g2.setColor(new Color(40, 20, 20));
                g2.fillRoundRect(bx, by, bw, bh, 6, 6);
                
                // 4. Current Health Fill
                float ratio = (float)e.life / e.maxLife;
                int fillW = (int)(bw * ratio);
                
                // Pulsing Gradient for Boss Life
                GradientPaint hpPaint = new GradientPaint(bx, by, new Color(200, 30, 30), 
                                                        bx, by + bh, new Color(120, 10, 10));
                g2.setPaint(hpPaint);
                g2.fillRoundRect(bx, by, fillW, bh, 6, 6);
                
                // Gloss highlight
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(bx, by, fillW, bh / 2, 6, 6);
                
                // 5. Border
                g2.setColor(new Color(220, 180, 60));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(bx, by, bw, bh, 6, 6);
                
                break; // Only draw one boss bar at a time
            }
        }
    }
}
