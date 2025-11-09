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

public class BananaCat extends Hero {
    public BananaCat() {
        super("Banana Cat", 120, 10, 20, 100);
        
        BasicAttack basic = new BasicAttack();
        basic.setDescription("Serangan dasar (100% ATK) yang juga menghasilkan 10 poin Ultimate.");
        addSkill(basic);
        
        addSkill(new BananaBopSkill());
        addSkill(new BananaGuardSkill());
        addSkill(new BananaHeartRoarSkill());
    }

    private class BananaBopSkill extends Skill implements SkillWithTargetType {
        public BananaBopSkill() {
            super("Banana Bop", 30, 3);
            this.description = "Men-STUN 1 musuh selama 2 giliran. Jika Banana Cat memiliki SHIELD, durasi STUN diperpanjang menjadi 3 giliran.";
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
            BattleLogger.getInstance().log(target.getName() + " is stunned for " + (stunTurns - 1) + " turn(s) by Banana Bop!");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    private class BananaGuardSkill extends Skill implements SkillWithTargetType {
        public BananaGuardSkill() {
            super("Banana Guard", 40, 3);
            this.description = "Memberikan SHIELD pada diri sendiri sebesar 20% dari Max HP. SHIELD bertahan selama 4 giliran atau sampai hancur.";
        }

        @Override
        public void use(Hero user, Hero target) {
            int shield = (int) (user.getMaxHP() * 0.20);
            user.addStatusEffect(new StatusEffect(StatusEffect.Type.SHIELD, 4, shield, Attribute.SHIELD_AMOUNT));
            BattleLogger.getInstance().log(user.getName() + " gains shield for " + shield + " HP.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SELF;
        }
    }

    public class BananaHeartRoarSkill extends Ultimate {
        public BananaHeartRoarSkill() {
            super("Banana Heart Roar", 0, TargetType.ALL_ENEMIES);
            this.description = "(ULTIMATE) Memaksa semua musuh menyerang Banana Cat (TAUNT) selama 2 giliran. Sekaligus memberikan SHIELD (10% Max HP) ke semua kawan selama 2 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {}

        @Override
        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero enemy : targets) {
                StatusEffect tauntEffect = new StatusEffect(StatusEffect.Type.TAUNT, 2, 0, null);
                tauntEffect.setSource(user.getName());
                enemy.addStatusEffect(tauntEffect);
            }

            for (Hero ally : player.getTeam()) {
                int shield = (int) (ally.getMaxHP() * 0.10);
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.SHIELD, 2, shield, null));
                BattleLogger.getInstance().log(ally.getName() + " gains shield for " + shield + " HP.");
            }
            BattleLogger.getInstance().log(user.getName() + " forces enemies to attack him and shields allies!");
            user.setUltimateBar(0);
        }
    }
}