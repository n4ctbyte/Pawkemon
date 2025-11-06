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

public class BananaCat extends Hero {
    public BananaCat() {
        super("Banana Cat", 120, 10, 20, 100);
        addSkill(new BasicAttack());
        addSkill(new BananaBopSkill());
        addSkill(new BananaGuardSkill());
        addSkill(new BananaHeartRoarSkill());
    }

    private class BananaBopSkill extends Skill implements SkillWithTargetType {
        public BananaBopSkill() {
            super("Banana Bop", 30, 3);
        }

        @Override
        public void use(Hero user, Hero target) {
            boolean hasShield = false;
            for (StatusEffect effect : user.getActiveEffects()) {
                if (effect.getType() == StatusEffect.Type.SHIELD && effect.getValue() > 0) {
                    hasShield = true;
                    break;
                }
            }

            int stunTurns = hasShield ? 3 : 2;
            target.addStatusEffect(new StatusEffect(StatusEffect.Type.STUN, stunTurns, 0, null));
            System.out.println(target.getName() + " is stunned for " + (stunTurns - 1) + " turn(s) by Banana Bop!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    private class BananaGuardSkill extends Skill implements SkillWithTargetType {
        public BananaGuardSkill() {
            super("Banana Guard", 40, 3);
        }

        @Override
        public void use(Hero user, Hero target) {
            int shield = (int) (user.getMaxHP() * 0.20);
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.SHIELD, 4, shield, Attribute.SHIELD_AMOUNT));
            System.out.println(user.getName() + " gains shield for " + shield + " HP.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SELF;
        }
    }

    private class BananaHeartRoarSkill extends Skill implements SkillWithTargetType {
        public BananaHeartRoarSkill() {
            super("Banana Heart Roar", 70, 0);
        }

        @Override
        public void use(Hero user, Hero target) {
            
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero enemy : targets) {
                enemy.addStatusEffect(new StatusEffect(StatusEffect.Type.TAUNT, 1, 0, null));
            }
            
            for (Hero ally : player.getTeam()) {
                int shield = (int) (ally.getMaxHP() * 0.10);
                System.out.println(ally.getName() + " gains shield for " + shield + " HP.");
            }
            System.out.println(user.getName() + " forces enemies to attack him and shields allies!");
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ENEMIES;
        }
    }
}