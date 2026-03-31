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

        gp.npcs[0] = new NPC(gp, "Elder Rowan", "elder",
            ts * 9, ts * 9,
            "Welcome, traveler, to Verdant Village. Our land is in peril...",
            "Slimes have been multiplying near the lake to the east.",
            "Please, help us drive them back. We will reward you handsomely.",
            "Speak to Mira the merchant for supplies before you go."
        );

        gp.npcs[1] = new NPC(gp, "Mira", "merchant",
            ts * 13, ts * 9,
            "Hello there! Lost? This is Verdant Village.",
            "I trade in fine goods — potions, gear, you name it.",
            "Explore to the south for the Crystal Caves, and east for the old ruins.",
            "Be careful of the Dark Knight — he patrols the Ancient Ruins."
        );

        gp.npcs[2] = new NPC(gp, " Jeffrey Epstein", "Billonaire",
            ts * 7, ts * 12,
              "The Ancient Ruins to my Island are fascinating.",
            "Legend says a powerful artifact rests within, guarded by the Dark Knight.",
            "Defeat him, and young girls will be yours forever."
        );

        gp.npcs[3] = new NPC(gp, "Bren", "Farmer",
            ts * 11, ts * 6,
            "Good day! Lovely weather for farming, wouldn't you say?",
            "Well, it would be, if those dreadful skeletons weren't about.",
            "I found this old key near my well. Maybe it opens something in the caves?"
        );

        gp.npcs[4] = new NPC(gp, "Thork", "Guard",
            ts * 5, ts * 10,
            "Halt! Oh wait, you look like a hero. Carry on.",
            "We've been fighting slimes for weeks. Any help appreciated!",
            "The Darkwood Forest to the right is dangerous — stay alert."
        );

        gp.npcs[5] = new entity.ImageNPC(gp, "Katrina", 
            "d:\\New folder\\.gemini\\antigravity\\scratch\\ZeldaRPG\\src\\res\\npc\\dancer.png",
            ts * 7, ts * 6,
            "Hello there, traveler!",
            "I'm practicing a new dance routine. It takes lots of energy!",
            "Don't let the monsters interrupt the rhythm of your journey."
        );
    }

    public void setupEnemies() {
        int ts = gp.tileSize;
        gp.enemies = new entity.enemy.Enemy[25];

        // Slimes near lake
        gp.enemies[0]  = new Slime(gp, ts * 23, ts * 5);
        gp.enemies[1]  = new Slime(gp, ts * 25, ts * 6);
        gp.enemies[2]  = new Slime(gp, ts * 27, ts * 8);
        gp.enemies[3]  = new Slime(gp, ts * 24, ts * 10);
        gp.enemies[4]  = new Slime(gp, ts * 29, ts * 7);
        gp.enemies[5]  = new Slime(gp, ts * 26, ts * 3);

        // Dark Forest skeletons
        gp.enemies[6]  = new Skeleton(gp, ts * 35, ts * 4);
        gp.enemies[7]  = new Skeleton(gp, ts * 38, ts * 8);
        gp.enemies[8]  = new Skeleton(gp, ts * 41, ts * 12);
        gp.enemies[9]  = new Skeleton(gp, ts * 36, ts * 16);

        // Cave slimes
        gp.enemies[10] = new Slime(gp, ts * 5, ts * 33);
        gp.enemies[11] = new Slime(gp, ts * 8, ts * 35);
        gp.enemies[12] = new Slime(gp, ts * 12, ts * 37);
        gp.enemies[13] = new Skeleton(gp, ts * 7, ts * 38);
        gp.enemies[14] = new Skeleton(gp, ts * 15, ts * 34);

        // Ancient Ruins area
        gp.enemies[15] = new Skeleton(gp, ts * 32, ts * 30);
        gp.enemies[16] = new Skeleton(gp, ts * 36, ts * 34);
        gp.enemies[17] = new Skeleton(gp, ts * 40, ts * 28);
        gp.enemies[18] = new DarkKnight(gp, ts * 38, ts * 38); // Boss
        gp.enemies[19] = new Slime(gp, ts * 34, ts * 36);
        
        // Samurai guards
        gp.enemies[20] = new Samurai(gp, ts * 36, ts * 30);
        gp.enemies[21] = new Samurai(gp, ts * 33, ts * 38);
    }

    public void setupObjects() {
        int ts = gp.tileSize;
        gp.objects = new SuperObject[20];

        // Village items
        gp.objects[0] = new OBJ_Potion(gp);  gp.objects[0].worldX = ts * 4; gp.objects[0].worldY = ts * 6;
        gp.objects[1] = new OBJ_Potion(gp);  gp.objects[1].worldX = ts * 17; gp.objects[1].worldY = ts * 11;
        gp.objects[2] = new OBJ_Sword(gp);   gp.objects[2].worldX = ts * 10; gp.objects[2].worldY = ts * 14;
        gp.objects[3] = new OBJ_Shield(gp);  gp.objects[3].worldX = ts * 15; gp.objects[3].worldY = ts * 13;
        gp.objects[4] = new OBJ_Chest(gp, "Gold", 75); gp.objects[4].worldX = ts * 18; gp.objects[4].worldY = ts * 6;

        // Forest items
        gp.objects[5] = new OBJ_Potion(gp);  gp.objects[5].worldX = ts * 37; gp.objects[5].worldY = ts * 5;
        gp.objects[6] = new OBJ_Chest(gp, "Sword", 50); gp.objects[6].worldX = ts * 42; gp.objects[6].worldY = ts * 10;

        // Cave items
        gp.objects[7] = new OBJ_Potion(gp);  gp.objects[7].worldX = ts * 6; gp.objects[7].worldY = ts * 36;
        gp.objects[8] = new OBJ_Chest(gp, "Shield", 100); gp.objects[8].worldX = ts * 14; gp.objects[8].worldY = ts * 38;

        // Ruins items
        gp.objects[9]  = new OBJ_Potion(gp);  gp.objects[9].worldX = ts * 31; gp.objects[9].worldY = ts * 30;
        gp.objects[10] = new OBJ_Chest(gp, "Legendary Sword", 200); gp.objects[10].worldX = ts * 40; gp.objects[10].worldY = ts * 40;
        gp.objects[11] = new OBJ_Potion(gp);  gp.objects[11].worldX = ts * 33; gp.objects[11].worldY = ts * 38;

        // Buildings (Overlay drawn on top of collision blocks)
        gp.objects[12] = new OBJ_House(gp);
        gp.objects[12].worldX = ts * 4;
        gp.objects[12].worldY = ts * 4;

        gp.objects[13] = new OBJ_House(gp);
        gp.objects[13].worldX = ts * 15;
        gp.objects[13].worldY = ts * 4;

        gp.objects[14] = new OBJ_Church(gp);
        gp.objects[14].worldX = ts * 9;
        gp.objects[14].worldY = ts * 4;
    }
}
