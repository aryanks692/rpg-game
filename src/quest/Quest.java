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
        if (type == Type.KILL)    return killCount >= killRequired;
        if (type == Type.EXPLORE) return explored;
        return false;
    }

    public String getProgress() {
        if (type == Type.KILL)    return killCount + "/" + killRequired + " " + targetEnemy + "s slain";
        if (type == Type.FETCH)   return "Find: " + targetItem;
        if (type == Type.EXPLORE) return explored ? "Area discovered!" : "Find: " + targetZone;
        return "";
    }
}
