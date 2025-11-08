package hero;

import game.Hero;
import game.Player;
import skill.AoeSkill;
import skill.BasicAttack;
import skill.Skill;
import skill.SkillWithTargetType;
import skill.Ultimate;
import game.StatusEffect;
import game.Attribute;
import game.TargetType;
import java.util.List;

public class CryingHamster extends Hero {

    public CryingHamster() {
        super("Crying Hamster", 70, 10, 10, 200);
        addSkill(new BasicAttack());
        addSkill(new TearsOfCutenessSkill());
        addSkill(new CutenessOverflowSkill());
        addSkill(new DivineTearsSkill());
    }

    private class TearsOfCutenessSkill extends Skill implements SkillWithTargetType {
        public TearsOfCutenessSkill() {
            super("Tears of Cuteness", 40, 2);
        }

        @Override
        public void use(Hero user, Hero target) {
            int heal = (int) (target.getMaxHP() * 0.30);
            target.heal(heal);
            System.out.println(target.getName() + " is healed for " + heal + " HP by Tears of Cuteness!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ALLY;
        }
    }

    public class CutenessOverflowSkill extends Skill implements SkillWithTargetType, AoeSkill {
        public CutenessOverflowSkill() {
            super("Cuteness Overflow", 50, 4);
        }

        @Override
        public void use(Hero user, Hero target) {
        }

        public void useAOE(Hero user, List<Hero> targets) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 0.25);
                target.applyDamage(damage);
                System.out.println(target.getName() + " takes " + damage + " damage from Cuteness Overflow!");
            }
            System.out.println(user.getName() + " attacks enemies!");
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            useAOE(user, targets);
            for (Hero ally : player.getTeam()) {
                int shield = (int) (ally.getMaxHP() * 0.15);
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.SHIELD, 2, shield, Attribute.SHIELD_AMOUNT));
                System.out.println(ally.getName() + " gains shield for " + shield + " HP.");
            }
            System.out.println(user.getName() + " shields allies!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }

    private class DivineTearsSkill extends Ultimate {
        public DivineTearsSkill() {
            super("Divine Tears", 0, TargetType.SINGLE_ALLY);
        }

        @Override
        public void use(Hero user, Hero target) {
            if (target.isDead()) {
                target.setCurrentHP((int) (target.getMaxHP() * 0.50));
                System.out.println(target.getName() + " is revived with 50% HP by Divine Tears!");
            }
            else {
                System.out.println(target.getName() + " is not dead and cannot be revived");
                return;
            }
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ALLY;
        }
    }
}