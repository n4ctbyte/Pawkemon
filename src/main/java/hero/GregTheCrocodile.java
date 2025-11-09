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
import game.BattleLogger;

public class GregTheCrocodile extends Hero {
    private int defenseBuff = 0;

    public GregTheCrocodile() {
        super("Greg The Crocodile", 130, 10, 20, 100);
        
        BasicAttack basic = new BasicAttack();
        basic.setDescription("Serangan dasar (100% ATK) yang juga menghasilkan 10 poin Ultimate.");
        addSkill(basic);
        
        addSkill(new ThickMudSkill());
        addSkill(new WarningBiteSkill());
        addSkill(new DeathRollSkill());
    }

    private class ThickMudSkill extends Skill implements SkillWithTargetType {
        public ThickMudSkill() {
            super("Thick Mud", 30, 3);
            this.description = "Menyerang 1 musuh (damage berdasarkan 3% Max HP target + ATK Greg). Memberi BUFF DEF (+10%) pada diri sendiri selama 2 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {
            int damage = (int) (target.getMaxHP() * 0.03) + user.getAttackPower();
            target.applyDamage(damage);
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 2, (int) (user.getDefense() * 0.10), Attribute.DEFENSE));
            BattleLogger.getInstance().log(target.getName() + " takes " + damage + " damage from Thick Mud!");
            BattleLogger.getInstance().log(user.getName() + "'s defense increases by 10% for 2 turns.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    private class WarningBiteSkill extends Skill implements SkillWithTargetType {
        public WarningBiteSkill() {
            super("Warning Bite", 30, 3);
            this.description = "Menyerang 1 musuh dan memberikan STUN selama 1 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {
            target.addStatusEffect(new StatusEffect(StatusEffect.Type.STUN, 2, 0, null));
            BattleLogger.getInstance().log(target.getName() + " is stunned for 1 turn by Warning Bite!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    public class DeathRollSkill extends Ultimate {
        public DeathRollSkill() {
            super("Death Roll", 0, TargetType.ALL_ENEMIES);
            this.description = "(ULTIMATE) Menyerang semua musuh, memberi DEBUFF ATK & DEF (-15) selama 2 giliran. Sekaligus memberi SHIELD (10% Max HP Greg) ke semua kawan selama 2 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero target : targets) {
                target.addStatusEffect(new StatusEffect(StatusEffect.Type.DEBUFF, 2, 15, Attribute.ATTACK));
                target.addStatusEffect(new StatusEffect(StatusEffect.Type.DEBUFF, 2, 15, Attribute.DEFENSE));
            }

            for (Hero ally : player.getTeam()) {
                int shield = (int) (user.getMaxHP() * 0.10);
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.SHIELD, 2, shield, null));
                BattleLogger.getInstance().log(ally.getName() + " gains shield for " + shield + " HP.");
            }
            BattleLogger.getInstance().log(user.getName() + " uses Death Roll, debuffs enemies and shields allies!");
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }
}