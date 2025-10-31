package skill;

import game.Hero;
import skill.BasicAttack;

public class BasicAttack extends Skill {
    public BasicAttack() {
        super("Basic Attack", 0, 0);
    }

    @Override
    public void use(Hero user, Hero target) {
        int damage = user.getAttackPower();
        target.applyDamage(damage);
        System.out.println(user.getName() + " uses Basic Attack on " + target.getName() + " for " + damage + " damage!");
    }
}