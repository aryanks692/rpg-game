package util;

import core.GamePanel;
import entity.NPC;
import entity.enemy.*;
import object.*;

public class AssetSetter {
    private GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setupNPCs() {
        gp.npcs = new NPC[6];
        int ts = gp.tileSize;
        int rowOff = 30;

        // Village is now shifted down by rowOff (30)
        gp.npcs[0] = new NPC(gp, "Elder Rowan", "elder",
                ts * 16, ts * (12 + rowOff),
                "Welcome, traveler, to Verdant Village. Our land is in peril...",
                "Slimes have been multiplying near the eastern fields.",
                "Please, help us drive them back. We will reward you handsomely.",
                "Speak to Mira the merchant for supplies before you go.");

        gp.npcs[1] = new NPC(gp, "Mira", "merchant",
                ts * 21, ts * (13 + rowOff),
                "Hello there! Lost? This is Verdant Village.",
                "I trade in fine goods — potions, gear, you name it.",
                "Explore to the south for the Crystal Caves, and east for the old ruins.",
                "Be careful of the Dark Knight — he patrols the Crystal Caves.");

        gp.npcs[2] = new NPC(gp, "Old Scholar", "elder",
                ts * 14, ts * (16 + rowOff),
                "The Crystal Caves to the south are fascinating.",
                "Legend says a powerful artifact rests within, guarded by the Dark Knight.",
                "Many adventurers entered those caves, but few returned.");

        gp.npcs[3] = new NPC(gp, "Bren", "villager",
                ts * 19, ts * (11 + rowOff),
                "Good day! Lovely weather for farming, wouldn't you say?",
                "Well, it would be, if those dreadful skeletons weren't about.",
                "I found this old key near my well. Maybe it opens something in the caves?");

        gp.npcs[4] = new NPC(gp, "Thork", "villager",
                ts * 12, ts * (19 + rowOff),
                "Halt! Oh wait, you look like a hero. Carry on.",
                "We've been fighting slimes for weeks. Any help appreciated!",
                "The Darkwood Forest to the right is dangerous — stay alert.");

        // Katrina — placed inside village
        gp.npcs[5] = new entity.ImageNPC(
                gp,
                "Katrina",
                "d:\\New folder\\.gemini\\antigravity\\scratch\\ZeldaRPG\\src\\res\\npc\\dancer.png",
                ts * 25,
                ts * (15 + rowOff),
                "(She moves closer, her voice low...)",
                "\u201cDon\u2019t look away\u2026 you\u2019ve already come too far.\u201d",
                "This rhythm? It\u2019s not in the air\u2026 it\u2019s under your skin now.",
                "Every step I take\u2026 is pulling you in deeper\u2014",
                "...and you don\u2019t even want to resist.",
                "Tell me\u2026 is it the music you feel\u2026 or is it me?",
                "(She smiles, just enough to tease)",
                "Careful\u2026 not every flame is meant to keep you warm.",
                "Some are meant to make you forget everything but the fire.");
    }

    public void setupEnemies() {
        int ts = gp.tileSize;
        int rowOff = 30;
        gp.enemies = new Enemy[30]; // ── Expanded for Savannah ────────────────

        // Savannah Region (NEW: Rows 1-30)
        gp.enemies[23] = new Slime(gp, ts * 15, ts * 10);
        gp.enemies[24] = new Slime(gp, ts * 45, ts * 15);
        gp.enemies[25] = new Skeleton(gp, ts * 30, ts * 5);
        gp.enemies[26] = new Skeleton(gp, ts * 55, ts * 20);
        // Savannah boss location (Top Center)
        gp.enemies[27] = new SunGuardian(gp, ts * 35, ts * 15);

        // Slimes near lake (Shifted)
        gp.enemies[0] = new Slime(gp, ts * 23, ts * (5 + rowOff));
        gp.enemies[1] = new Slime(gp, ts * 25, ts * (6 + rowOff));
        gp.enemies[2] = new Slime(gp, ts * 27, ts * (8 + rowOff));
        gp.enemies[3] = new Slime(gp, ts * 24, ts * (7 + rowOff));
        gp.enemies[4] = new Slime(gp, ts * 29, ts * (7 + rowOff));
        gp.enemies[5] = new Slime(gp, ts * 26, ts * (3 + rowOff));

        // Dark Forest skeletons (Shifted)
        gp.enemies[6] = new Skeleton(gp, ts * 35, ts * (4 + rowOff));
        gp.enemies[7] = new Skeleton(gp, ts * 38, ts * (8 + rowOff));
        gp.enemies[8] = new Skeleton(gp, ts * 41, ts * (12 + rowOff));
        gp.enemies[9] = new Skeleton(gp, ts * 36, ts * (16 + rowOff));

        // Cave slimes (Shifted)
        gp.enemies[10] = new Slime(gp, ts * 5, ts * (33 + rowOff));
        gp.enemies[11] = new Slime(gp, ts * 8, ts * (35 + rowOff));
        gp.enemies[12] = new Slime(gp, ts * 12, ts * (37 + rowOff));
        gp.enemies[13] = new Skeleton(gp, ts * 7, ts * (38 + rowOff));
        gp.enemies[14] = new Skeleton(gp, ts * 15, ts * (34 + rowOff));

        // Ancient Ruins area (Shifted)
        gp.enemies[15] = new Skeleton(gp, ts * 32, ts * (30 + rowOff));
        gp.enemies[16] = new Skeleton(gp, ts * 36, ts * (34 + rowOff));
        gp.enemies[17] = new Skeleton(gp, ts * 40, ts * (28 + rowOff));
        gp.enemies[18] = new Ninja(gp, ts * 30, ts * (35 + rowOff));
        gp.enemies[19] = new Slime(gp, ts * 34, ts * (36 + rowOff));

        // Samurai guards (Shifted)
        gp.enemies[20] = new Samurai(gp, ts * 36, ts * (30 + rowOff));
        gp.enemies[21] = new Samurai(gp, ts * 33, ts * (38 + rowOff));

        // Crystal Cave Guardian (Shifted)
        gp.enemies[22] = new DarkKnight(gp, ts * 18, ts * (52 + rowOff));
    }

    public void setupObjects() {
        int ts = gp.tileSize;
        int rowOff = 30;
        gp.objects = new SuperObject[25];

        // Village items (Shifted)
        gp.objects[0] = new OBJ_Potion(gp);
        gp.objects[0].worldX = ts * 13;
        gp.objects[0].worldY = ts * (17 + rowOff);

        gp.objects[1] = new OBJ_Potion(gp);
        gp.objects[1].worldX = ts * 26;
        gp.objects[1].worldY = ts * (17 + rowOff);

        gp.objects[2] = new OBJ_Sword(gp);
        gp.objects[2].worldX = ts * 18;
        gp.objects[2].worldY = ts * (20 + rowOff);

        gp.objects[3] = new OBJ_Shield(gp);
        gp.objects[3].worldX = ts * 22;
        gp.objects[3].worldY = ts * (20 + rowOff);

        gp.objects[4] = new OBJ_Chest(gp, "Gold", 75);
        gp.objects[4].worldX = ts * 28;
        gp.objects[4].worldY = ts * (14 + rowOff);

        // Forest items (Shifted)
        gp.objects[5] = new OBJ_Potion(gp);
        gp.objects[5].worldX = ts * 37;
        gp.objects[5].worldY = ts * (10 + rowOff);

        gp.objects[6] = new OBJ_Chest(gp, "Sword", 50);
        gp.objects[6].worldX = ts * 40;
        gp.objects[6].worldY = ts * (16 + rowOff);

        // Cave items (Shifted)
        gp.objects[7] = new OBJ_Potion(gp);
        gp.objects[7].worldX = ts * 13;
        gp.objects[7].worldY = ts * (44 + rowOff);

        gp.objects[8] = new OBJ_Chest(gp, "Shield", 100);
        gp.objects[8].worldX = ts * 20;
        gp.objects[8].worldY = ts * (50 + rowOff);

        // Ruins items (Shifted)
        gp.objects[9] = new OBJ_Potion(gp);
        gp.objects[9].worldX = ts * 35;
        gp.objects[9].worldY = ts * (42 + rowOff);

        gp.objects[10] = new OBJ_Chest(gp, "Legendary Sword", 200);
        gp.objects[10].worldX = ts * 55;
        gp.objects[10].worldY = ts * (55 + rowOff);

        gp.objects[11] = new OBJ_Potion(gp);
        gp.objects[11].worldX = ts * 42;
        gp.objects[11].worldY = ts * (48 + rowOff);

        // Buildings (Shifted)
        gp.objects[12] = new OBJ_House(gp);
        gp.objects[12].worldX = ts * 12;
        gp.objects[12].worldY = ts * (11 + rowOff);

        gp.objects[13] = new OBJ_House(gp);
        gp.objects[13].worldX = ts * 24;
        gp.objects[13].worldY = ts * (11 + rowOff);

        gp.objects[14] = new OBJ_Church(gp);
        gp.objects[14].worldX = ts * 17;
        gp.objects[14].worldY = ts * (11 + rowOff);

        gp.objects[15] = new OBJ_CrystalGate(gp);
        gp.objects[15].worldX = ts * 17;
        gp.objects[15].worldY = ts * (36 + rowOff);

        // Savannah items
        gp.objects[16] = new OBJ_Potion(gp);
        gp.objects[16].worldX = ts * 10;
        gp.objects[16].worldY = ts * 5;
    }
}