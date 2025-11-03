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

public class SadCat extends Hero {

    public SadCat() {
        super("Sad Cat", 75, 12, 7, 100);
        addSkill(new BasicAttack());
        addSkill(new MelancholyMelodySkill());
        addSkill(new DrainingWhisperSkill());
        addSkill(new CrescendoOfHopeSkill());
    }

    private class MelancholyMelodySkill extends Skill implements SkillWithTargetType {
        public MelancholyMelodySkill() {
            super("Melancholy Melody", 40, 3);
        }

        @Override
        public void use(Hero user, Hero target) {
            // Buff all allies
            // Kita tidak bisa akses player di sini, jadi ubah ke AOE
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero ally : player.getTeam()) {
                int buff = (ally.getCurrentHP() < ally.getMaxHP() * 0.5) ? 35 : 25;
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 2, buff, Attribute.ATTACK));
            }
            System.out.println(user.getName() + " boosts team's attack by 25% (35% if HP < 50%) for 2 turns.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ALLIES;
        }
    }

    private class DrainingWhisperSkill extends Skill implements SkillWithTargetType {
        public DrainingWhisperSkill() {
            super("Draining Whisper", 50, 4);
        }

        @Override
        public void use(Hero user, Hero target) {
            int energyDrained = (int) (target.getCurrentEnergy() * 0.35);
            target.setCurrentEnergy(target.getCurrentEnergy() - energyDrained);
            // Bagi rata ke tim
            // Kita tidak bisa akses player di sini, jadi ubah ke AOE
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            Hero target = targets.get(0); // Asumsikan target adalah satu musuh
            int energyDrained = (int) (target.getCurrentEnergy() * 0.35);
            target.setCurrentEnergy(target.getCurrentEnergy() - energyDrained);
            // Bagi rata ke tim
            int energyPerAlly = energyDrained / player.getTeam().size();
            for (Hero ally : player.getTeam()) {
                ally.gainEnergy(energyPerAlly);
            }
            target.addStatusEffect(new StatusEffect(StatusEffect.Type.DEBUFF, 2, 15, Attribute.ATTACK));
            System.out.println(target.getName() + " loses " + energyDrained + " energy, which is distributed to allies.");
            System.out.println(target.getName() + " gets -15% attack for 2 turns.");
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.SINGLE_ENEMY;
        }
    }

    private class CrescendoOfHopeSkill extends Skill implements SkillWithTargetType {
        public CrescendoOfHopeSkill() {
            super("Crescendo of Hope", 70, 0);
        }

        @Override
        public void use(Hero user, Hero target) {
            // Asumsikan semua anggota tim sebagai target
        }

        public void useAOE(Hero user, List<Hero> targets, Player player) {
            for (Hero ally : player.getTeam()) {
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 3, 20, Attribute.ATTACK));
                ally.addStatusEffect(new StatusEffect(StatusEffect.Type.BUFF, 3, 20, Attribute.DEFENSE));
                ally.gainEnergy((int) (ally.getMaxEnergy() * 0.20));
                if (ally.getCurrentHP() < ally.getMaxHP() * 0.5) {
                    // Damage reduction
                    // Asumsikan ada efek damage reduction
                }
                ally.clearDebuffs();
            }
            System.out.println(user.getName() + " buffs allies, restores energy, and removes debuffs!");
            user.setUltimateBar(0);
        }

        @Override
        public TargetType getTargetType() {
            return TargetType.ALL_ALLIES;
        }
    }
}