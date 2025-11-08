package skill;

import game.Attribute;
import game.Hero;
import game.StatusEffect;
import game.TargetType;
import game.BattleLogger;

public class DebuffSkill extends Skill implements SkillWithTargetType {
    private int debuffAmount;
    private int duration;
    private Attribute attribute;
    private TargetType targetType;

    public DebuffSkill(String name, int energyCost, int cooldown, int debuffAmount, int duration, Attribute attribute, TargetType targetType) {
        super(name, energyCost, cooldown);
        this.debuffAmount = debuffAmount;
        this.duration = duration;
        this.attribute = attribute;
        this.targetType = targetType;
    }

    @Override
    public void use(Hero user, Hero target) {
        target.addStatusEffect(new StatusEffect(StatusEffect.Type.DEBUFF, duration, debuffAmount, attribute));
        BattleLogger.getInstance().log(user.getName() + " debuffs " + target.getName() + "'s " + attribute + " by " + debuffAmount + " for " + duration + " turns!");
    }

    @Override
    public TargetType getTargetType() {
        return targetType;
    }
}