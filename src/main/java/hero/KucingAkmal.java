package hero;

import game.Hero;
import game.Player;
import skill.BasicAttack;
import skill.Skill;
import skill.SkillWithTargetType;
import skill.Ultimate;
import game.StatusEffect;
import game.Attribute;
import game.TargetType;
import java.util.List;
import java.util.Random;
import game.BattleLogger;

public class KucingAkmal extends Hero {
    private static final Random random = new Random();

    public KucingAkmal() {
        super("Kucing Akmal", 100, 30, 0, 100);
        
        BasicAttack basic = new BasicAttack();
        basic.setDescription("Serangan dasar (100% ATK) yang juga menghasilkan 10 poin Ultimate.");
        addSkill(basic);
        
        addSkill(new WhiskerVortexSkill());
        addSkill(new RotailStrikeSkill());
        addSkill(new AkmalCycloneSkill());
    }

    private class WhiskerVortexSkill extends Skill implements SkillWithTargetType {
        public WhiskerVortexSkill() {
            super("Whisker Vortex", 40, 3);
            this.description = "Menghapus semua debuff dari diri sendiri dan meningkatkan ATK sebesar 10% (dari ATK dasar) selama 3 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {
            int baseAttack = user.getBaseAttack();
            int buffAmount = (int) (baseAttack * 0.10);
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 3, buffAmount, Attribute.ATTACK));
            BattleLogger.getInstance().log(user.getName() + " uses Whisker Vortex! Attack increased by " + buffAmount + " and debuffs cleared.");
            user.clearDebuffs();
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SELF;
        }
    }

    private class RotailStrikeSkill extends Skill implements SkillWithTargetType {
        public RotailStrikeSkill() {
            super("Rotail Strike", 30, 2);
            this.description = "Menyerang 1 musuh (120% ATK). Memiliki 40% peluang untuk memberi STUN (1 giliran) dan DOT (10 dmg/giliran) selama 5 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {
            int damage = (int) (user.getAttackPower() * 1.20);
            target.applyDamage(damage);
            BattleLogger.getInstance().log(user.getName() + " uses Rotail Strike on " + target.getName() + " for " + damage + " damage!");

            if (random.nextDouble() < 0.40) {
                target.addStatusEffect(new StatusEffect(StatusEffect.Type.DOT, 5, 10, null));
                BattleLogger.getInstance().log(target.getName() + " is affected by damage over time from Rotail Strike!");

                target.addStatusEffect(new StatusEffect(StatusEffect.Type.STUN, 1, 0, null));
                BattleLogger.getInstance().log(target.getName() + " is stunned for 1 turns by Rotail Strike!");
            }
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    public class AkmalCycloneSkill extends Ultimate {
        public AkmalCycloneSkill() {
            super("Akmal Cyclone", 0, TargetType.ALL_ENEMIES);
            this.description = "(ULTIMATE) Menyerang semua musuh (240% ATK) dan memberi DOT (10 dmg/giliran) pada mereka semua selama 3 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {
        }

        public void useAOE(Hero user, List<Hero> targets) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 2.40);
                target.applyDamage(damage);
                BattleLogger.getInstance().log(user.getName() + " uses Akmal Cyclone on " + target.getName() + " for " + damage + " damage!");
                target.addStatusEffect(new StatusEffect(StatusEffect.Type.DOT, 3, 10, null));
                BattleLogger.getInstance().log(target.getName() + " is affected by damage over time from Akmal Cyclone!");
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