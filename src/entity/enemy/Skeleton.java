package entity.enemy;

import core.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Skeleton extends Enemy {
    private int boneRattle = 0;

    public Skeleton(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, gp.tileSize * 7);
        type = "Skeleton";
        maxLife = 55;
        life = maxLife;
        speed = 2;
        attackDamage = 12;
        attackRange = gp.tileSize * 2;
        attackCooldownMax = 70;
        xpReward = 25;
        goldReward = 12;
        buildSprites();
    }

    private void buildSprites() {
        for (int i = 0; i < 4; i++) {
            walkDown[i]  = drawSkeleton("down",  i);
            walkUp[i]    = drawSkeleton("up",    i);
            walkLeft[i]  = drawSkeleton("left",  i);
            walkRight[i] = drawSkeleton("right", i);
        }
        image = walkDown[0];
    }

  private BufferedImage drawSkeleton(String dir, int frame) {

    int ts = gp.tileSize;

    BufferedImage img = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    Color bone = new Color(232,228,214);
    Color darkBone = new Color(185,180,165);
    Color outline = new Color(70,65,60);

    int walk = (frame % 2 == 0) ? 2 : -2;

    //-------------------------
    // SHADOW
    //-------------------------
    g.setColor(new Color(0,0,0,60));
    g.fillOval(12,40,24,6);

    //-------------------------
    // LEGS
    //-------------------------
    g.setColor(darkBone);

    g.fillRoundRect(16,29,5,13+walk,3,3);
    g.fillRoundRect(27,29,5,13-walk,3,3);

    //-------------------------
    // FEET
    //-------------------------
    g.fillRoundRect(14,41+walk,8,3,2,2);
    g.fillRoundRect(26,41-walk,8,3,2,2);

    //-------------------------
    // PELVIS
    //-------------------------
    g.setColor(bone);
    g.fillRoundRect(15,24,18,7,4,4);

    //-------------------------
    // SPINE
    //-------------------------
    g.setColor(darkBone);
    g.fillRoundRect(22,15,4,10,2,2);

    //-------------------------
    // RIB CAGE
    //-------------------------
    g.setColor(bone);
    g.fillRoundRect(13,12,22,14,8,8);

    g.setColor(darkBone);

    for(int i=0;i<4;i++){
        int y=14+i*3;
        g.drawLine(15,y,33,y);
    }

    //-------------------------
    // ARMS
    //-------------------------
    g.setColor(bone);

    int swing=(frame%2==0)?3:-3;

    g.fillRoundRect(8,15+swing,5,13,3,3);
    g.fillRoundRect(35,15-swing,5,13,3,3);

    //-------------------------
    // HANDS
    //-------------------------
    g.fillOval(8,25+swing,5,5);
    g.fillOval(35,25-swing,5,5);

    //-------------------------
    // SKULL
    //-------------------------
    g.setColor(bone);
    g.fillRoundRect(12,2,24,18,8,8);

    //-------------------------
    // JAW
    //-------------------------
    g.fillRoundRect(16,16,16,7,4,4);

    //-------------------------
    // EYES
    //-------------------------
    g.setColor(new Color(25,25,30));

    g.fillOval(17,8,6,6);
    g.fillOval(25,8,6,6);

    //-------------------------
    // GLOW
    //-------------------------
    g.setColor(new Color(0,255,140,180));

    g.fillOval(19,10,2,2);
    g.fillOval(27,10,2,2);

    //-------------------------
    // NOSE
    //-------------------------
    g.setColor(outline);
    g.fillOval(23,12,2,4);

    //-------------------------
    // TEETH
    //-------------------------
    g.setColor(darkBone);

    for(int i=0;i<4;i++){
        g.drawLine(18+i*3,18,18+i*3,21);
    }

    //-------------------------
    // OUTLINE
    //-------------------------
    g.setColor(outline);
    g.drawRoundRect(12,2,24,18,8,8);
    g.drawRoundRect(13,12,22,14,8,8);
    g.drawRoundRect(15,24,18,7,4,4);

    g.dispose();

    return img;
}
}