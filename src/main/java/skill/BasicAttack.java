package skill;

import game.Hero;
import skill.BasicAttack;
import game.BattleLogger;

public class BasicAttack extends Skill {
    public BasicAttack() {
        super("Basic Attack", 0, 0);
    }

    @Override
    public void use(Hero user, Hero target) {
        int damage = user.calculateDamage(user.getAttackPower());
        target.applyDamage(damage);
        BattleLogger.getInstance().log(user.getName() + " uses Basic Attack on " + target.getName() + " for " + damage + " damage!");
    }
}