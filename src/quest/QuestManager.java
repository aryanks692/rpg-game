package quest;

import core.GamePanel;

import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private List<Quest> quests = new ArrayList<>();

    private GamePanel gp;

    public QuestManager(GamePanel gp) {
        this.gp = gp;
        initQuests();
    }

    private void initQuests() {
        // Starter village quest
        Quest q1 = new Quest("Q_SLIMES", "Slime Infestation",
            "The village elder asks you to slay 5 slimes near the lake.", Quest.Type.KILL);
        q1.targetEnemy = "Slime";
        q1.killRequired = 5;
        q1.rewardGold = 50;
        q1.rewardItem = "Health Potion";
        quests.add(q1);

        Quest q2 = new Quest("Q_FOREST", "Into the Dark Forest",
            "A hunter went missing in the Darkwood Forest. Find out what happened.", Quest.Type.EXPLORE);
        q2.targetZone = "Darkwood Forest";
        q2.rewardGold = 100;
        quests.add(q2);

        Quest q3 = new Quest("Q_SKELETONS", "Undead Rising",
            "Skeletons have been spotted near the Ancient Ruins. Defeat 3 of them.", Quest.Type.KILL);
        q3.targetEnemy = "Skeleton";
        q3.killRequired = 3;
        q3.rewardGold = 75;
        q3.rewardItem = "Iron Shield";
        quests.add(q3);

        Quest q4 = new Quest("Q_CAVES", "Crystal Depths",
            "Miners speak of glittering caves to the south. Explore the Crystal Caves.", Quest.Type.EXPLORE);
        q4.targetZone = "Crystal Caves";
        q4.rewardGold = 120;
        quests.add(q4);

        // Savannah Quest
        Quest q5 = new Quest("Q_SAVANNAH", "Master of the Plains",
            "Conquer the vast northern plains. Discover the Great Savannah.", Quest.Type.EXPLORE);
        q5.targetZone = "Great Savannah";
        q5.rewardGold = 150;
        q5.rewardItem = "Gold Potion";
        quests.add(q5);

        // Activate starter quests
        q1.status = Quest.Status.ACTIVE;
        q2.status = Quest.Status.ACTIVE;
        q5.status = Quest.Status.ACTIVE;
    }

    public List<Quest> getActiveQuests() {
        return quests.stream().filter(q -> q.status == Quest.Status.ACTIVE).collect(Collectors.toList());
    }

    public List<Quest> getAllQuests() {
        return quests;
    }

    public void onEnemyKilled(String enemyType) {
        for (Quest q : quests) {
            if (q.status == Quest.Status.ACTIVE && q.type == Quest.Type.KILL
                && enemyType.equalsIgnoreCase(q.targetEnemy)) {
                q.killCount++;
                if (q.isComplete()) {
                    q.status = Quest.Status.COMPLETED;
                    // Activate next quest
                    activateNextQuest();
                    gp.saveManager.save(0);
                }
            }
        }
    }

    public void onZoneEntered(String zone) {
        for (Quest q : quests) {
            if (q.status == Quest.Status.ACTIVE && q.type == Quest.Type.EXPLORE
                && zone.equalsIgnoreCase(q.targetZone)) {
                q.explored = true;
                if (q.isComplete()) {
                    q.status = Quest.Status.COMPLETED;
                    activateNextQuest();
                    gp.saveManager.save(0);
                }
            }
        }
    }

    private void activateNextQuest() {
        for (Quest q : quests) {
            if (q.status == Quest.Status.INACTIVE) {
                q.status = Quest.Status.ACTIVE;
                break;
            }
        }
    }

    public int totalCompleted() {
        return (int) quests.stream().filter(q -> q.status == Quest.Status.COMPLETED).count();
    }

    public String getQuestSaveData() {
        StringBuilder sb = new StringBuilder();
        for (Quest q : quests) {
            sb.append(q.id).append(":").append(q.status.name()).append(":").append(q.killCount).append(":").append(q.explored).append(";");
        }
        return sb.toString();
    }

    public void loadQuestSaveData(String data) {
        if (data == null || data.isEmpty()) return;
        String[] qDataArray = data.split(";");
        for (String qData : qDataArray) {
            if (qData.isEmpty()) continue;
            String[] parts = qData.split(":");
            if (parts.length >= 4) {
                String id = parts[0];
                for (Quest q : quests) {
                    if (q.id.equals(id)) {
                        q.status = Quest.Status.valueOf(parts[1]);
                        q.killCount = Integer.parseInt(parts[2]);
                        q.explored = Boolean.parseBoolean(parts[3]);
                        break;
                    }
                }
            }
        }
    }
}
