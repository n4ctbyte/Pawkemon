package skill;

import game.Hero;

public class Ultimate extends Skill {
    public Ultimate(String name, int energyCost) {
        super(name, energyCost, 0);
    }

    @Override
    public void use(Hero user, Hero target) {
        int damage = user.getAttackPower() * 2;
        target.applyDamage(damage);
        user.setUltimateBar(0);
        System.out.println(user.getName() + " uses " + name + " on " + target.getName() + " for " + damage + " damage!");
    }
}