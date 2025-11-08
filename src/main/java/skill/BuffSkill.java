package skill;
import game.Attribute;
import game.Hero;
import game.StatusEffect;
import game.TargetType;
import game.BattleLogger;

public class BuffSkill extends Skill implements SkillWithTargetType {
    private int buffAmount;
    private int duration;
    private Attribute attribute;
    private TargetType targetType;

    public BuffSkill(String name, int energyCost, int cooldown, int buffAmount, int duration, Attribute attribute, TargetType targetType) {
        super(name, energyCost, cooldown);
        this.buffAmount = buffAmount;
        this.duration = duration;
        this.attribute = attribute;
        this.targetType = targetType;
    }

    @Override
    public void use(Hero user, Hero target) {
        target.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, duration, buffAmount, attribute));
        BattleLogger.getInstance().log(user.getName() + " buffs " + target.getName() + "'s " + attribute + " by " + buffAmount + " for " + duration + " turns!");
    }

    @Override
    public TargetType getTargetType() {
        return targetType;
    }
}