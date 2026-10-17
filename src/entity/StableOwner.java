package entity;

import core.GamePanel;

/**
 * Stable Owner — the shopkeeper standing inside the Horse Stall interior.
 * Talks the player into buying a Horse for a flat 100 gold. A horse can only
 * ever be owned once; afterwards he just reminds the player to ride it with [C].
 */
public class StableOwner extends NPC {

    public static final int HORSE_PRICE = 100;

    private final String[] forSaleDialogue;
    private final String[] ownedDialogue = {
        "You've already got a fine steed, partner!",
        "Just press [C] out on the trail to mount or dismount."
    };

    public StableOwner(GamePanel gp, int worldX, int worldY) {
        super(gp, "Barnaby the Stablemaster", "cowboy", worldX, worldY,
            "Howdy! Welcome to my stables, stranger.",
            "Looking for a good horse? I raise the finest in all of Dusty Gulch.",
            "A sturdy mount will carry you far faster than your own two boots.",
            "I can sell you a Horse for " + HORSE_PRICE + " gold. Deal?",
            "Enjoy the ride, partner! Press [C] to mount or dismount anytime.");
        this.forSaleDialogue = dialogues;
        setService(HORSE_PRICE, "Horse", 3);
    }

    @Override
    public void startDialogue() {
        if (gp.player.hasHorse) {
            // Already owns one — no offer, no service choice.
            dialogues = ownedDialogue;
            serviceCost = 0;
            choiceDialogueIndex = -1;
        } else {
            dialogues = forSaleDialogue;
            serviceCost = HORSE_PRICE;
            choiceDialogueIndex = 3;
        }
        super.startDialogue();
    }

    /** A horse has a fixed price — no credit-score discount applies. */
    @Override
    public int getEffectiveServiceCost() {
        return serviceCost;
    }

    @Override
    protected void onServicePurchased() {
        gp.player.buyHorse();
        gp.ui.showNotification("You bought a Horse! Press [C] to ride it.");
    }

    @Override
    public void update() {
        // The stablemaster stays put at his counter.
    }
}
