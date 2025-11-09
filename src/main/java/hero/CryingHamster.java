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

public class CryingHamster extends Hero {

    public CryingHamster() {
        super("Crying Hamster", 70, 10, 10, 200);
        
        BasicAttack basic = new BasicAttack();
        basic.setDescription("Serangan dasar (100% ATK) yang juga menghasilkan 10 poin Ultimate.");
        addSkill(basic);
        
        addSkill(new TearsOfCutenessSkill());
        addSkill(new CutenessOverflowSkill());
        addSkill(new DivineTearsSkill());
    }

    private class TearsOfCutenessSkill extends Skill implements SkillWithTargetType {
        public TearsOfCutenessSkill() {
            super("Tears of Cuteness", 40, 2);
            this.description = "Menyembuhkan 1 kawan sebesar 30% dari Max HP target.";
        }

        @Override
        public void use(Hero user, Hero target) {
            int heal = (int) (target.getMaxHP() * 0.30);
            target.heal(heal);
            BattleLogger.getInstance().log(target.getName() + " is healed for " + heal + " HP by Tears of Cuteness!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ALLY;
        }
    }

    public class CutenessOverflowSkill extends Skill implements SkillWithTargetType, AoeSkill {
        public CutenessOverflowSkill() {
            super("Cuteness Overflow", 50, 4);
            this.description = "Menyerang semua musuh (25% ATK) dan memberikan SHIELD (15% Max HP) ke semua kawan selama 2 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {
        }

        public void useAOE(Hero user, List<Hero> targets) {
            for (Hero target : targets) {
                int damage = (int) (user.getAttackPower() * 0.25);
                target.applyDamage(damage);
                BattleLogger.getInstance().log(target.getName() + " takes " + damage + " damage from Cuteness Overflow!");
            }
            BattleLogger.getInstance().log(user.getName() + " attacks enemies!");
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            useAOE(user, targets);
            for (Hero ally : player.getTeam()) {
                int shield = (int) (ally.getMaxHP() * 0.15);
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.SHIELD, 2, shield, Attribute.SHIELD_AMOUNT));
                BattleLogger.getInstance().log(ally.getName() + " gains shield for " + shield + " HP.");
            }
            BattleLogger.getInstance().log(user.getName() + " shields allies!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }

    private class DivineTearsSkill extends Ultimate {
        public DivineTearsSkill() {
            super("Divine Tears", 0, TargetType.SINGLE_ALLY);
            this.description = "(ULTIMATE) Menghidupkan kembali (Revive) 1 kawan yang telah mati dengan 50% HP.";
        }

        @Override
        public void use(Hero user, Hero target) {
            if (target.isDead()) {
                target.setCurrentHP((int) (target.getMaxHP() * 0.50));
                BattleLogger.getInstance().log(target.getName() + " is revived with 50% HP by Divine Tears!");
            } else {
                BattleLogger.getInstance().log(target.getName() + " is not dead and cannot be revived");
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