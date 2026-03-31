package object;

import core.GamePanel;
import entity.Player;
import java.awt.*;

public class OBJ_Sword extends SuperObject {
    public int damage;

    public OBJ_Sword(GamePanel gp) {
        super(gp);
        name = "Sword";
        damage = 15;
        description = "A sturdy iron sword. Deals " + damage + " damage.";
        image = createIcon(new Color(180, 180, 200), new Color(220, 220, 255), "⚔");
    }

    @Override
    public void onPickup(Player player) {
        player.attackDamage += damage;
        player.hasWeapon = true;
        pickedUp = true;
    }
}
