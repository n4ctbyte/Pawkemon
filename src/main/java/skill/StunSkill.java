package skill;
import game.BattleLogger;
import game.Hero;
import game.StatusEffect;
import game.TargetType;

public class StunSkill extends Skill implements SkillWithTargetType {
    private int duration;
    private TargetType targetType;

    public StunSkill(String name, int energyCost, int cooldown, int duration, TargetType targetType) {
        super(name, energyCost, cooldown);
        this.duration = duration;
        this.targetType = targetType;
    }

    @Override
    public void use(Hero user, Hero target) {
        target.addStatusEffect(new StatusEffect(StatusEffect.Type.STUN, duration, 0, null));
        BattleLogger.getInstance().log(user.getName() + " stuns " + target.getName() + " for " + duration + " turn(s)!");
    }

    @Override
    public TargetType getTargetType() {
        return targetType;
    }
}