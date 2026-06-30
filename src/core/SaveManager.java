package core;

import entity.Player;
import quest.QuestManager;

import java.io.*;
import java.util.Properties;

public class SaveManager {
    private final String saveDir = "saves";
    public static class SaveInfo {
        public int level;
        public String zone;
        public long playTimeTicks;
        public boolean exists;
    }

    private GamePanel gp;

    public SaveManager(GamePanel gp) {
        this.gp = gp;
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getSaveFile(int slot) {
        if (slot == 0) return saveDir + "/autosave.properties";
        return saveDir + "/save" + slot + ".properties";
    }

    public boolean hasSave(int slot) {
        return new File(getSaveFile(slot)).exists();
    }

    public boolean hasAnySave() {
        for (int i = 0; i <= 4; i++) {
            if (hasSave(i)) return true;
        }
        return false;
    }

    public SaveInfo getSaveInfo(int slot) {
        SaveInfo info = new SaveInfo();
        File file = new File(getSaveFile(slot));
        info.exists = file.exists();
        if (info.exists) {
            try (InputStream input = new FileInputStream(file)) {
                Properties prop = new Properties();
                prop.load(input);
                info.level = Integer.parseInt(prop.getProperty("level", "1"));
                info.zone = prop.getProperty("currentZone", "Unknown");
                info.playTimeTicks = Long.parseLong(prop.getProperty("playTimeTicks", "0"));
            } catch (Exception e) {}
        }
        return info;
    }

    public void deleteSave(int slot) {
        File file = new File(getSaveFile(slot));
        if (file.exists()) {
            file.delete();
        }
    }

    public void save(int slot) {
        String file = getSaveFile(slot);
        try (OutputStream output = new FileOutputStream(file)) {
            Properties prop = new Properties();

            Player p = gp.player;
            // Player stats
            prop.setProperty("worldX", String.valueOf(p.worldX));
            prop.setProperty("worldY", String.valueOf(p.worldY));
            prop.setProperty("life", String.valueOf(p.life));
            prop.setProperty("maxLife", String.valueOf(p.maxLife));
            prop.setProperty("level", String.valueOf(p.level));
            prop.setProperty("xp", String.valueOf(p.xp));
            prop.setProperty("xpToLevel", String.valueOf(p.xpToLevel));
            prop.setProperty("attackDamage", String.valueOf(p.attackDamage));
            prop.setProperty("defense", String.valueOf(p.defense));
            prop.setProperty("gold", String.valueOf(p.gold));
            prop.setProperty("potionCount", String.valueOf(p.potionCount));
            prop.setProperty("hasWeapon", String.valueOf(p.hasWeapon));
            prop.setProperty("hasShield", String.valueOf(p.hasShield));
            prop.setProperty("currentZone", p.currentZone);

            // Inventory
            StringBuilder inv = new StringBuilder();
            for (int i = 0; i < p.inventory.size(); i++) {
                inv.append(p.inventory.get(i));
                if (i < p.inventory.size() - 1) {
                    inv.append(",");
                }
            }
            prop.setProperty("inventory", inv.toString());

            // Quests
            prop.setProperty("quests", gp.questManager.getQuestSaveData());

            // Playtime
            prop.setProperty("playTimeTicks", String.valueOf(gp.playTimeTicks));

            prop.store(output, "ZeldaRPG Save File");
            System.out.println("Game saved to " + file);
            gp.ui.showNotification("Progress Saved \u2713");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void load(int slot) {
        String file = getSaveFile(slot);
        try (InputStream input = new FileInputStream(file)) {
            Properties prop = new Properties();
            prop.load(input);

            Player p = gp.player;
            // Player stats
            p.worldX = Integer.parseInt(prop.getProperty("worldX", String.valueOf(p.worldX)));
            p.worldY = Integer.parseInt(prop.getProperty("worldY", String.valueOf(p.worldY)));
            p.life = Integer.parseInt(prop.getProperty("life", String.valueOf(p.life)));
            p.maxLife = Integer.parseInt(prop.getProperty("maxLife", String.valueOf(p.maxLife)));
            p.level = Integer.parseInt(prop.getProperty("level", String.valueOf(p.level)));
            p.xp = Integer.parseInt(prop.getProperty("xp", String.valueOf(p.xp)));
            p.xpToLevel = Integer.parseInt(prop.getProperty("xpToLevel", String.valueOf(p.xpToLevel)));
            p.attackDamage = Integer.parseInt(prop.getProperty("attackDamage", String.valueOf(p.attackDamage)));
            p.defense = Integer.parseInt(prop.getProperty("defense", String.valueOf(p.defense)));
            p.gold = Integer.parseInt(prop.getProperty("gold", String.valueOf(p.gold)));
            p.potionCount = Integer.parseInt(prop.getProperty("potionCount", String.valueOf(p.potionCount)));
            p.hasWeapon = Boolean.parseBoolean(prop.getProperty("hasWeapon", String.valueOf(p.hasWeapon)));
            p.hasShield = Boolean.parseBoolean(prop.getProperty("hasShield", String.valueOf(p.hasShield)));
            p.currentZone = prop.getProperty("currentZone", p.currentZone);

            // Inventory
            String invStr = prop.getProperty("inventory", "");
            p.inventory.clear();
            if (!invStr.isEmpty()) {
                String[] items = invStr.split(",");
                for (String item : items) {
                    p.inventory.add(item);
                }
            }

            // Quests
            String questsData = prop.getProperty("quests", "");
            gp.questManager.loadQuestSaveData(questsData);

            // Playtime
            gp.playTimeTicks = Long.parseLong(prop.getProperty("playTimeTicks", "0"));

            System.out.println("Game loaded from " + file);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
