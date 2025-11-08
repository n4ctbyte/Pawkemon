package skill;

import game.Hero;
import game.TargetType;
import game.BattleLogger;

public class HealSkill extends Skill implements SkillWithTargetType {
    private int healAmount;
    private TargetType targetType;

    public HealSkill(String name, int energyCost, int cooldown, int healAmount, TargetType targetType) {
        super(name, energyCost, cooldown);
        this.healAmount = healAmount;
        this.targetType = targetType;
    }

    @Override
    public void use(Hero user, Hero target) {
        user.heal(healAmount);
        BattleLogger.getInstance().log(user.getName() + " heals " + healAmount + " HP!");
    }

    @Override
    public TargetType getTargetType() {
        return targetType;
    }
}