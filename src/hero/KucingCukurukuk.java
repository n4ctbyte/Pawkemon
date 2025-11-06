package hero;

import game.Hero;
import game.Player;
import skill.AoeSkill;
import skill.BasicAttack;
import skill.Skill;
import skill.SkillWithTargetType;
import game.StatusEffect;
import game.Attribute;
import game.TargetType;
import java.util.List;

public class KucingCukurukuk extends Hero {
    public KucingCukurukuk() {
        super("Kucing Cukurukuk", 80, 25, 0, 100);
        addSkill(new BasicAttack());
        addSkill(new StoicStanceSkill());
        addSkill(new PsychoScoopSkill());
        addSkill(new SandRequiemSkill());
    }

    private class StoicStanceSkill extends Skill implements SkillWithTargetType {
        public StoicStanceSkill() {
            super("Stoic Stance", 40, 4);
        }

        @Override
        public void use(Hero user, Hero target) {
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 4, 100, Attribute.CRIT_CHANCE)); // Asumsikan ada attribut CRIT_CHANCE
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 4, 100, Attribute.CRIT_DAMAGE)); // Asumsikan ada attribut CRIT_DAMAGE
            System.out.println(user.getName() + " enters Stoic Stance! Crit chance +100%, Crit damage +100% for 2 turns.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SELF;
        }
    }

    public class PsychoScoopSkill extends Skill implements SkillWithTargetType, AoeSkill {
        public PsychoScoopSkill() {
            super("Psycho Scoop", 30, 3);
        }

        @Override
        public void use(Hero user, Hero target) {
        }

        public void useAOE(Hero user, List<Hero> targets) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 1.10);
                target.applyDamage(damage);
                System.out.println(user.getName() + " uses Psycho Scoop on " + target.getName() + " for " + damage + " damage!");
            }

            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 2, 40, Attribute.DODGE_CHANCE));
            System.out.println(user.getName() + " gains 40% dodge chance for 2 turn.");
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            useAOE(user, targets);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }

    private class SandRequiemSkill extends Skill implements SkillWithTargetType {
    public SandRequiemSkill() {
        super("Sand Requiem", 70, 0);
    }

    @Override
    public void use(Hero user, Hero target) {
    }

    public void useAOE(Hero user, List<Hero> targets) {
        for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 2.00);
                target.applyDamage(damage);
                System.out.println(user.getName() + " uses Sand Requiem on " + target.getName() + " for " + damage + " damage!");
            }
            user.setUltimateBar(0);
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            useAOE(user, targets);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }
}