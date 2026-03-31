package quest;

public class Quest {
    public enum Type { KILL, FETCH, EXPLORE }
    public enum Status { INACTIVE, ACTIVE, COMPLETED }

    public String id;
    public String title;
    public String description;
    public Type type;
    public Status status = Status.INACTIVE;

    // Kill quest
    public String targetEnemy;
    public int killRequired;
    public int killCount;

    // Fetch quest
    public String targetItem;
    public int itemRequired;

    // Explore quest
    public String targetZone;
    public boolean explored;

    // Reward
    public int rewardGold;
    public String rewardItem;

    public Quest(String id, String title, String description, Type type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public boolean isComplete() {
        return switch (type) {
            case KILL    -> killCount >= killRequired;
            case FETCH   -> false; // handled by inventory
            case EXPLORE -> explored;
        };
    }

    public String getProgress() {
        return switch (type) {
            case KILL    -> killCount + "/" + killRequired + " " + targetEnemy + "s slain";
            case FETCH   -> "Find: " + targetItem;
            case EXPLORE -> explored ? "Area discovered!" : "Find: " + targetZone;
        };
    }
}
