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

public class KucingCukurukuk extends Hero {
    public KucingCukurukuk() {
        super("Kucing Cukurukuk", 80, 25, 4, 100);
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
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 2, 30, Attribute.CRIT_CHANCE)); // Asumsikan ada attribut CRIT_CHANCE
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 2, 20, Attribute.CRIT_DAMAGE)); // Asumsikan ada attribut CRIT_DAMAGE
            System.out.println(user.getName() + " enters Stoic Stance! Crit chance +30%, Crit damage +20% for 2 turns.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SELF;
        }
    }

    private class PsychoScoopSkill extends Skill implements SkillWithTargetType {
        public PsychoScoopSkill() {
            super("Psycho Scoop", 30, 3);
        }

        @Override
        public void use(Hero user, Hero target) {
            // Asumsikan semua musuh sebagai target
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 1.10);
                target.applyDamage(damage);
                System.out.println(user.getName() + " uses Psycho Scoop on " + target.getName() + " for " + damage + " damage!");
            }
            // Dodge chance buff
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 1, 40, Attribute.DODGE_CHANCE)); // Asumsikan ada attribut DODGE_CHANCE
            System.out.println(user.getName() + " gains +40% dodge chance for 1 turn.");
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
            // Asumsikan semua musuh sebagai target
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 2.00);
                target.applyDamage(damage);
                System.out.println(user.getName() + " uses Sand Requiem on " + target.getName() + " for " + damage + " damage!");
            }
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }
}