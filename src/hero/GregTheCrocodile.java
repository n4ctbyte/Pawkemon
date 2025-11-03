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

public class GregTheCrocodile extends Hero {
    private int defenseBuff = 0;

    public GregTheCrocodile() {
        super("Greg The Crocodile", 130, 20, 8, 100);
        addSkill(new BasicAttack());
        addSkill(new ThickMudSkill());
        addSkill(new WarningBiteSkill());
        addSkill(new DeathRollSkill());
    }

    private class ThickMudSkill extends Skill implements SkillWithTargetType {
        public ThickMudSkill() {
            super("Thick Mud", 30, 3);
        }

        @Override
        public void use(Hero user, Hero target) {
            int damage = (int) (target.getMaxHP() * 0.03) + user.getAttackPower();
            target.applyDamage(damage);
            // Defense buff
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 2, (int) (user.getDefense() * 0.10), Attribute.DEFENSE));
            System.out.println(target.getName() + " takes " + damage + " damage from Thick Mud!");
            System.out.println(user.getName() + "'s defense increases by 10% for 2 turns.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    private class WarningBiteSkill extends Skill implements SkillWithTargetType {
        public WarningBiteSkill() {
            super("Warning Bite", 30, 3);
        }

        @Override
        public void use(Hero user, Hero target) {
            target.addStatusEffect(new StatusEffect(StatusEffect.Type.STUN, 1, 0, null));
            System.out.println(target.getName() + " is stunned for 1 turn by Warning Bite!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    private class DeathRollSkill extends Skill implements SkillWithTargetType {
        public DeathRollSkill() {
            super("Death Roll", 70, 0);
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

                System.out.println(ally.getName() + " gains shield for " + shield + " HP.");
            }
            System.out.println(user.getName() + " uses Death Roll, debuffs enemies and shields allies!");
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }
}