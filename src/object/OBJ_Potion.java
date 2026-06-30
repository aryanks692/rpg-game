package object;

import core.GamePanel;
import entity.Player;
import java.awt.*;

public class OBJ_Potion extends SuperObject {
    public int healAmount;

    public OBJ_Potion(GamePanel gp) {
        super(gp);
        name = "Potion";
        healAmount = 30;
        description = "A health potion. Restores " + healAmount + " HP.";
        image = createIcon(new Color(220, 60, 60), new Color(255, 150, 150), "♥");
    }

    @Override
    public void onPickup(Player player) {
        player.life = Math.min(player.maxLife, player.life + healAmount);
        player.potionCount++;
        pickedUp = true;
    }
}
