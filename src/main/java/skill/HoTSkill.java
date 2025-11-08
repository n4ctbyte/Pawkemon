package skill;

import game.Hero;
import game.StatusEffect;
import game.TargetType;
import game.BattleLogger;

public class HoTSkill extends Skill implements SkillWithTargetType {
    private int healPerTurn;
    private int duration;
    private TargetType targetType;

    public HoTSkill(String name, int energyCost, int cooldown, int healPerTurn, int duration, TargetType targetType) {
        super(name, energyCost, cooldown);
        this.healPerTurn = healPerTurn;
        this.duration = duration;
        this.targetType = targetType;
    }

    @Override
    public void use(Hero user, Hero target) {
        target.addStatusEffect(new StatusEffect(StatusEffect.Type.HEAL_OVER_TIME, duration, healPerTurn, null));
        BattleLogger.getInstance().log(user.getName() + " applies " + name + " to " + target.getName() + " for " + duration + " turns!");
    }

    @Override public TargetType getTargetType() {
        return targetType;
    }
}