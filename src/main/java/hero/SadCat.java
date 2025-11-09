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

public class SadCat extends Hero {

    public SadCat() {
        super("Sad Cat", 75, 10, 10, 200);
        
        BasicAttack basic = new BasicAttack();
        basic.setDescription("Serangan dasar (100% ATK) yang juga menghasilkan 10 poin Ultimate.");
        addSkill(basic);
        
        addSkill(new MelancholyMelodySkill());
        addSkill(new DrainingWhisperSkill());
        addSkill(new CrescendoOfHopeSkill());
    }

    public class MelancholyMelodySkill extends Skill implements SkillWithTargetType {
        public MelancholyMelodySkill() {
            super("Melancholy Melody", 40, 3);
            this.description = "Memberikan BUFF ATK ke semua kawan selama 3 giliran. Kawan dengan HP < 50% mendapat buff 35% ATK, sementara yang lain mendapat 25% ATK.";
        }

        @Override
        public void use(Hero user, Hero target) {}

        public void useAOE(Hero user, List<Hero> targets) {
            for (Hero ally : targets) {
                int buff = (ally.getCurrentHP() < ally.getMaxHP() * 0.5) ? (int) (0.35 * ally.getAttackPower()) : (int) (0.25 * ally.getAttackPower());
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 3, buff, Attribute.ATTACK));
            }
            BattleLogger.getInstance().log(user.getName() + " boosts team's attack!");
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            useAOE(user, targets);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ALLIES;
        }
    }

    public class DrainingWhisperSkill extends Skill implements SkillWithTargetType {
        public DrainingWhisperSkill() {
            super("Draining Whisper", 50, 4);
            this.description = "Menyerap 35% Energi dari 1 musuh dan membagikannya secara rata ke semua kawan yang masih hidup. Musuh yang diserang juga terkena DEBUFF ATK (-15%) selama 3 giliran.";
        }

        @Override
        public void use(Hero user, Hero target) {
            BattleLogger.getInstance().log("Peringatan: Draining Whisper dipanggil tanpa data Player!");
            use(user, target, null);
        }

        public void use(Hero user, Hero target, Player player) {
            int energyDrained = (int) (target.getCurrentEnergy() * 0.35);
            target.setCurrentEnergy(target.getCurrentEnergy() - energyDrained);
            BattleLogger.getInstance().log(target.getName() + " kehilangan " + energyDrained + " energi.");
            
            target.addStatusEffect(new StatusEffect(StatusEffect.Type.DEBUFF, 3, (int) (0.15 * target.getAttackPower()), Attribute.ATTACK));
            BattleLogger.getInstance().log(target.getName() + " terkena debuff Attack -15% selama 2 giliran.");

            if (player != null) {
                List<Hero> aliveAllies = player.getAliveHeroes();
                if (!aliveAllies.isEmpty()) {
                    int energyPerAlly = energyDrained / aliveAllies.size();
                    BattleLogger.getInstance().log(user.getName() + " membagikan " + energyDrained + " energi ke tim!");
                    
                    for (Hero ally : aliveAllies) {
                        ally.gainEnergy(energyPerAlly);
                        BattleLogger.getInstance().log(ally.getName() + " menerima " + energyPerAlly + " energi.");
                    }
                }
            }
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    public class CrescendoOfHopeSkill extends Ultimate {
        public CrescendoOfHopeSkill() {
            super("Crescendo of Hope", 0, TargetType.ALL_ALLIES);
            this.description = "(ULTIMATE) Memberikan BUFF ATK & DEF (+20) selama 3 giliran, memulihkan 15% Max Energy, dan menghapus semua DEBUFF (Cleanses) dari semua kawan.";
        }

        @Override
        public void use(Hero user, Hero target) {}

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero ally : targets) { 
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 3, 20, Attribute.ATTACK));
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 3, 20, Attribute.DEFENSE));
                int energyRestore = (int) (ally.getMaxEnergy() * 0.15);
                ally.gainEnergy(energyRestore);
                ally.clearDebuffs();
                BattleLogger.getInstance().log(ally.getName() + " receives +20% attack and defense, gains " + energyRestore + " energy, and debuffs cleared.");
            }
            BattleLogger.getInstance().log(user.getName() + " uses Crescendo of Hope! All allies are buffed, restored, and cleansed.");
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ALLIES;
        }
    }
}