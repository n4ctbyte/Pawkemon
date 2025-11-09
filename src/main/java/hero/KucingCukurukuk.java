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
import game.BattleLogger;

public class KucingCukurukuk extends Hero {
    public KucingCukurukuk() {
        super("Kucing Cukurukuk", 80, 25, 0, 100);
        
        BasicAttack basic = new BasicAttack();
        basic.setDescription("Serangan dasar (100% ATK) yang juga menghasilkan 10 poin Ultimate.");
        addSkill(basic);
        
        addSkill(new StoicStanceSkill());
        addSkill(new PsychoScoopSkill());
        addSkill(new SandRequiemSkill());
    }

    private class StoicStanceSkill extends Skill implements SkillWithTargetType {
        public StoicStanceSkill() {
            super("Stoic Stance", 40, 4);
            this.description = "Memberikan BUFF pada diri sendiri: CRIT CHANCE +50% dan CRIT DAMAGE +50% (Durasi 4).";
        }

        @Override
        public void use(Hero user, Hero target) {
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 4, 50, Attribute.CRIT_CHANCE));
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 4, 50, Attribute.CRIT_DAMAGE));
            BattleLogger.getInstance().log(user.getName() + " enters Stoic Stance! Crit chance +50%, Crit damage +50% for 2 turns.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SELF;
        }
    }

    public class PsychoScoopSkill extends Skill implements SkillWithTargetType, AoeSkill {
        public PsychoScoopSkill() {
            super("Psycho Scoop", 30, 3);
            this.description = "Menyerang semua musuh (110% ATK) dan memberikan BUFF DODGE CHANCE +40% pada diri sendiri (Durasi 2).";
        }

        @Override
        public void use(Hero user, Hero target) {
        }

        public void useAOE(Hero user, List<Hero> targets) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 1.10);
                target.applyDamage(damage);
                BattleLogger.getInstance().log(user.getName() + " uses Psycho Scoop on " + target.getName() + " for " + damage + " damage!");
            }

            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 2, 40, Attribute.DODGE_CHANCE));
            BattleLogger.getInstance().log(user.getName() + " gains 40% dodge chance for 2 turn.");
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            useAOE(user, targets);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }

    public class SandRequiemSkill extends Ultimate {
        public SandRequiemSkill() {
            super("Sand Requiem", 0, TargetType.ALL_ENEMIES);
            this.description = "(ULTIMATE) Menyerang semua musuh dengan damage besar (200% ATK).";
        }

        @Override
        public void use(Hero user, Hero target) {}

        public void useAOE(Hero user, List<Hero> targets) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 2.00);
                target.applyDamage(damage);
                BattleLogger.getInstance().log(user.getName() + " uses Sand Requiem on " + target.getName() + " for " + damage + " damage!");
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