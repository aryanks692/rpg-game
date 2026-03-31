package object;

import core.GamePanel;
import entity.Player;
import java.awt.*;

public class OBJ_Shield extends SuperObject {
    public int defense;

    public OBJ_Shield(GamePanel gp) {
        super(gp);
        name = "Shield";
        defense = 5;
        description = "An iron shield. Reduces incoming damage by " + defense + ".";
        image = createIcon(new Color(100, 160, 220), new Color(200, 230, 255), "🛡");
    }

    @Override
    public void onPickup(Player player) {
        player.defense += defense;
        player.hasShield = true;
        pickedUp = true;
    }
}
