package hero;

import game.Hero;
import game.Player;
import skill.BasicAttack;
import skill.Skill;
import skill.SkillWithTargetType;
import game.StatusEffect;
import game.Attribute;
import game.TargetType;
import java.util.List;

public class CryingHamster extends Hero {

    public CryingHamster() {
        super("Crying Hamster", 70, 10, 6, 100);
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

    private class CutenessOverflowSkill extends Skill implements SkillWithTargetType {
        public CutenessOverflowSkill() {
            super("Cuteness Overflow", 50, 4);
        }

        @Override
        public void use(Hero user, Hero target) {
            // Asumsikan semua musuh sebagai target
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 0.25);
                target.applyDamage(damage);
                System.out.println(target.getName() + " takes " + damage + " damage from Cuteness Overflow!");
            }
            // Shield all allies
            for (Hero ally : player.getTeam()) {
                int shield = (int) (ally.getMaxHP() * 0.15);
                // Asumsikan ada mekanisme shield
                System.out.println(ally.getName() + " gains shield for " + shield + " HP.");
            }
            System.out.println(user.getName() + " attacks enemies and shields allies!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }

    private class DivineTearsSkill extends Skill implements SkillWithTargetType {
        public DivineTearsSkill() {
            super("Divine Tears", 70, 0);
        }

        @Override
        public void use(Hero user, Hero target) {
            // Revive one ally
            // Asumsikan ada mekanisme revive
            System.out.println(target.getName() + " is revived with 50% HP by Divine Tears!");
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ALLY; // Asumsikan target adalah yang sudah mati
        }
    }
}