package skill;

import game.Hero;
import game.Player;
import game.TargetType;
import java.util.List;

public class Ultimate extends Skill implements SkillWithTargetType {
    private TargetType targetType;

    public Ultimate(String name, int energyCost, TargetType targetType) {
        super(name, energyCost, 0);
        this.targetType = targetType;
    }

    @Override
    public void use(Hero user, Hero target) {
        int damage = user.getAttackPower() * 2;
        target.applyDamage(damage);
        System.out.println(user.getName() + " uses " + name + " on " + target.getName() + " for " + damage + " damage!");
        user.setUltimateBar(0);
    }

    public void useAOE(Hero user, List<Hero> targets, Player player) {}

    @Override
    public TargetType getTargetType() {
        return targetType;
    }
}