package game;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import skill.Skill;

public class Hero {
    protected String name;
    protected int maxHP;
    protected int currentHP;
    private int attackPower;
    private int defense;
    protected int maxEnergy;
    protected int currentEnergy;
    private int ultimateBar;
    protected List<Skill> skills;
    protected List<StatusEffect> activeEffects;
    protected int attackBuff = 0;
    protected int defenseBuff = 0;
    private int critChance = 0;
    private int critDamage = 0;
    private int dodgeChance = 0;
    public boolean hasShield = false;
    public int shieldAmount = 0;
    private static final Random random = new Random();

    public Hero(String name, int maxHP, int attackPower, int defense, int maxEnergy) {
        this.name = name;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.attackPower = attackPower;
        this.defense = defense;
        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy;
        this.ultimateBar = 0;
        this.skills = new ArrayList<>();
        this.activeEffects = new ArrayList<>();
    }

    public int getAttackPower() { return attackPower + attackBuff; }
    public int getDefense() { return defense + defenseBuff; }
    public int getAttackPowerBuff() { return attackPower + attackBuff; }
    public int getDefenseBuff() { return defense + defenseBuff; }
    public int getBaseAttack() { return attackPower; }
    public int getBaseDefense() { return defense; }
    public int getUlt() { return ultimateBar; }
    public int getMaxHP() { return maxHP; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getCritChance() { return critChance; }
    public int getCritDamage() { return critDamage; }
    public int getDodgeChance() { return dodgeChance; }

    public void setCritChance(int value) { this.critChance = value; }
    public void setCritDamage(int value) { this.critDamage = value; }
    public void setDodgeChance(int value) { this.dodgeChance = value; }

    public void setUltimateBar(int value) { this.ultimateBar = value; }

    public void clearDebuffs() {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            StatusEffect effect = activeEffects.get(i);
            if (isNegativeEffect(effect)) {
                if (effect.getAttribute() != null) {
                    switch (effect.getAttribute()) {
                        case ATTACK: attackBuff -= effect.getValue(); break;
                        case DEFENSE: defenseBuff -= effect.getValue(); break;
                        case CRIT_CHANCE: critChance -= effect.getValue(); break;
                        case CRIT_DAMAGE: critDamage -= effect.getValue(); break;
                        case DODGE_CHANCE: dodgeChance -= effect.getValue(); break;
                    }
                }
                activeEffects.remove(i);
            }
        }
        System.out.println(name + "'s debuffs have been cleared.");
    }

    private boolean isNegativeEffect(StatusEffect effect) {
        return effect.getType() == StatusEffect.Type.DOT ||
               effect.getType() == StatusEffect.Type.POISON ||
               effect.getType() == StatusEffect.Type.STUN ||
               effect.getType() == StatusEffect.Type.DEBUFF;
    }

    public void setCurrentEnergy(int value) {
        this.currentEnergy = Math.max(0, Math.min(maxEnergy, value));
    }

    public void updateAttributeBuffs(Attribute attr, int value) {
        switch (attr) {
            case ATTACK: attackBuff += value; break;
            case DEFENSE: defenseBuff += value; break;
            case CRIT_CHANCE: critChance += value; break;
            case CRIT_DAMAGE: critDamage += value; break;
            case DODGE_CHANCE: dodgeChance += value; break;
        }
    }

    public void addSkill(Skill skill) { skills.add(skill); }

    public boolean tryDodge() {
        int chance = this.dodgeChance;
        return random.nextInt(100) < chance;
    }

    public void applyDamage(int damage) {
        if (currentHP > 0) {
            if (tryDodge()) {
                System.out.println(name + " dodged the attack!");
                return;
            }

            for (int i = activeEffects.size() - 1; i >= 0; i--) {
                StatusEffect effect = activeEffects.get(i);
                if (effect.getType() == StatusEffect.Type.SHIELD) {
                    int shieldValue = effect.getValue();
                    if (shieldValue > 0) {
                        if (damage <= shieldValue) {
                            int absorbed = damage;
                            effect.setValue(shieldValue - damage);
                            damage = 0;
                            System.out.println(name + "'s shield absorbed " + absorbed + " damage. Remaining shield: " + effect.getValue());
                        } else {
                            int absorbed = shieldValue;
                            damage -= shieldValue;
                            effect.setValue(0);
                            activeEffects.remove(i);
                            System.out.println(name + "'s shield absorbed " + absorbed + " damage and broke!");
                        }
                    }
                    break;
                }
            }

            if (damage > 0) {
                int actualDamage = Math.max(1, damage - defense);
                currentHP = Math.max(0, currentHP - actualDamage);
            }
        }
    }

    public Hero clone() {
        Hero clone = new Hero(this.name, this.maxHP, this.attackPower, this.defense, this.maxEnergy);
        clone.currentHP = this.currentHP;
        clone.currentEnergy = this.currentEnergy;
        clone.ultimateBar = this.ultimateBar;
        for (Skill skill : this.skills) clone.addSkill(skill);
        for (StatusEffect effect : this.activeEffects) clone.addStatusEffect(effect.clone());
        return clone;
    }

    public void heal(int amount) { currentHP = Math.min(maxHP, currentHP + amount); }

    public int calculateDamage(int baseAttack) {
        int damage = baseAttack;
        if (random.nextInt(100) < critChance) {
            damage = (int) (baseAttack * ((critDamage + 100) / 100.0));
            System.out.println(name + " landed a critical hit for " + damage + " damage!");
        }
        return damage;
    }

    public void gainEnergy(int amount) { currentEnergy = Math.min(maxEnergy, currentEnergy + amount); }
    public void gainUltimateBar(int amount) { ultimateBar = Math.min(100, ultimateBar + amount); }

    public void reset() {
        currentHP = maxHP;
        currentEnergy = maxEnergy;
        ultimateBar = 0;
        activeEffects.clear();
    }

    public void addStatusEffect(StatusEffect effect) { activeEffects.add(effect); }

    public void reduceStatusEffectDurations() {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            StatusEffect effect = activeEffects.get(i);
            effect.reduceDuration();
            if (!effect.isActive()) {
                if (effect.getType() == StatusEffect.Type.BUFF || effect.getType() == StatusEffect.Type.DEBUFF) {
                    updateAttributeBuffs(effect.getAttribute(), -effect.getValue());
                }
                activeEffects.remove(i);
            }
        }
    }

    public void updateStatusEffects() {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            StatusEffect effect = activeEffects.get(i);
            switch (effect.getType()) {
                case STUN: break;
                case DOT:
                case POISON:
                    applyDamage(effect.getValue());
                    System.out.println(name + " takes " + effect.getValue() + " damage from " + effect.getType() + "!");
                    break;
                case HEAL_OVER_TIME:
                    heal(effect.getValue());
                    System.out.println(name + " heals " + effect.getValue() + " HP from Healing Overtime!");
                    break;
                case BUFF:
                    if (!effect.isApplied()) {
                        updateAttributeBuffs(effect.getAttribute(), effect.getValue());
                        effect.setApplied(true);
                    }
                    System.out.println(name + "'s " + effect.getAttribute() + " is buffed by " + effect.getValue() + " for " + effect.getDuration() + " turn(s)!");
                    break;
                case DEBUFF:
                    if (!effect.isApplied()) {
                        updateAttributeBuffs(effect.getAttribute(), -effect.getValue());
                        effect.setApplied(true);
                    }
                    System.out.println(name + "'s " + effect.getAttribute() + " is debuffed by " + effect.getValue() + " for " + effect.getDuration() + " turn(s)!");
                    break;
                case SHIELD: break;
            }
        }
    }

    public boolean isStunned() {
        for (StatusEffect effect : activeEffects) {
            if (effect.getType() == StatusEffect.Type.STUN) return true;
        }
        return false;
    }

    public boolean isTaunted() {
        for (StatusEffect effect : activeEffects) {
            if (effect.getType() == StatusEffect.Type.TAUNT) return true;
        }
        return false;
    }

    public void setCurrentHP(int value) { this.currentHP = Math.max(0, Math.min(maxHP, value)); }
    public boolean isDead() { return currentHP <= 0; }
    public String getName() { return name; }
    public int getCurrentHP() { return currentHP; }
    public int getCurrentEnergy() { return currentEnergy; }
    public int getUltimateBar() { return ultimateBar; }
    public boolean isAlive() { return currentHP > 0; }
    public List<Skill> getSkills() { return skills; }
    public List<StatusEffect> getActiveEffects() { return activeEffects; }
}