package hero;

import game.Hero;
import skill.BasicAttack;
import skill.Skill;
import skill.SkillWithTargetType;
import game.StatusEffect;
import game.Attribute;
import game.TargetType;
import java.util.List;
import java.util.Random;

public class KucingAkmal extends Hero {
    private static final Random random = new Random();

    public KucingAkmal() {
        super("Kucing Akmal", 100, 25, 5, 100);
        addSkill(new BasicAttack());
        addSkill(new WhiskerVortexSkill());
        addSkill(new RotailStrikeSkill());
        addSkill(new AkmalCycloneSkill());
    }

    private class WhiskerVortexSkill extends Skill implements SkillWithTargetType {
        public WhiskerVortexSkill() {
            super("Whisker Vortex", 40, 3);
        }

        @Override
        public void use(Hero user, Hero target) {
            int baseAttack = user.getAttackPower();
            int buffAmount = (int) (baseAttack * 0.10);
            target.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 3, buffAmount, Attribute.ATTACK));
            target.clearDebuffs();
            System.out.println(user.getName() + " uses Whisker Vortex! Attack increased by " + buffAmount + " and debuffs cleared.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SELF;
        }
    }

    private class RotailStrikeSkill extends Skill implements SkillWithTargetType {
        public RotailStrikeSkill() {
            super("Rotail Strike", 30, 2);
        }

        @Override
        public void use(Hero user, Hero target) {
            int damage = (int) (user.getAttackPower() * 1.20);
            target.applyDamage(damage);
            System.out.println(user.getName() + " uses Rotail Strike on " + target.getName() + " for " + damage + " damage!");

            if (random.nextDouble() < 0.20) {
                target.addStatusEffect(new StatusEffect(StatusEffect.Type.POISON, 3, 10, null));
                System.out.println(target.getName() + " is poisoned by Rotail Strike!");
            }
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    private class AkmalCycloneSkill extends Skill implements SkillWithTargetType {
        public AkmalCycloneSkill() {
            super("Akmal Cyclone", 70, 0);
        }

        @Override
        public void use(Hero user, Hero target) {
        }

        public void useAOE(Hero user, List<Hero> targets) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 2.40);
                target.applyDamage(damage);
                System.out.println(user.getName() + " uses Akmal Cyclone on " + target.getName() + " for " + damage + " damage!");
                target.addStatusEffect(new StatusEffect(StatusEffect.Type.POISON, 3, 10, null));
                System.out.println(target.getName() + " is poisoned by Akmal Cyclone!");
            }
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }
}