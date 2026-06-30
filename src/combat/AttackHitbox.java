package combat;

import entity.Entity;
import java.awt.Rectangle;

public class AttackHitbox {
    public Rectangle box;
    public int damage;
    public boolean crit;
    public int active = 20; // frames this hitbox is alive
    public Entity owner;
    private boolean[] alreadyHit;

    public AttackHitbox(int x, int y, int w, int h, int damage, Entity owner, int maxTargets) {
        this.box = new Rectangle(x, y, w, h);
        this.damage = damage;
        this.owner = owner;
        this.crit = Math.random() < 0.15;
        if (this.crit) this.damage = (int)(damage * 1.75);
        this.alreadyHit = new boolean[maxTargets];
    }

    /** Returns true if this hitbox is still active */
    public boolean update() {
        active--;
        return active > 0;
    }

    public boolean hasHit(int index) {
        if (index < 0 || index >= alreadyHit.length) return false;
        return alreadyHit[index];
    }

    public void markHit(int index) {
        if (index >= 0 && index < alreadyHit.length) alreadyHit[index] = true;
    }
}
