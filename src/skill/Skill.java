package skill;

import game.Hero;

public abstract class Skill {
    protected String name;
    protected int energyCost;
    protected int cooldown;
    private int currentCooldown;
    protected boolean isAvailable;

    public Skill(String name, int energyCost, int cooldown) {
        this.name = name;
        this.energyCost = energyCost;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
        this.isAvailable = true;
    }

    public int getCurrentCooldown() { return currentCooldown; }

    public abstract void use(Hero user, Hero target);

    public int getCooldown() { return cooldown; }

    public void startCooldown() {
        currentCooldown = cooldown;
        isAvailable = false;
    }

    public void reduceCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
            if (currentCooldown == 0) {
                isAvailable = true;
            }
        }
    }

    public boolean isReady() { return isAvailable && currentCooldown == 0; }
    public int getEnergyCost() { return energyCost; }
    public String getName() { return name; }
}