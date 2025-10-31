package skill;

import game.Hero;
import game.StatusEffect;
import game.TargetType;


public class DotSkill extends Skill implements SkillWithTargetType {
    private int damagePerTurn;
    private int duration;
    private TargetType targetType;

    public DotSkill(String name, int energyCost, int cooldown, int damagePerTurn, int duration, TargetType targetType) {
        super(name, energyCost, cooldown);
        this.damagePerTurn = damagePerTurn;
        this.duration = duration;
        this.targetType = targetType;
    }

    @Override
    public void use(Hero user, Hero target) {
        target.addStatusEffect(new StatusEffect(StatusEffect.Type.DOT, duration, damagePerTurn, null));
        System.out.println(user.getName() + " applies " + name + " to " + target.getName() + " for " + duration + " turns!");
    }

    @Override
    public TargetType getTargetType() {
        return targetType;
    }
}