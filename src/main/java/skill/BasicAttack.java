package skill;

import game.Hero;
import game.TargetType;
import game.BattleLogger;

public class BasicAttack extends Skill implements SkillWithTargetType {

    public BasicAttack() {
        super("Basic Attack", 0, 0); 
        this.description = "Serangan dasar (100% ATK). Memberi 10 Ult Poin dan 20 Energi ke pengguna.";
    }

    @Override
    public void use(Hero user, Hero target) {
        int damage = user.calculateDamage(user.getAttackPower()); 
        
        BattleLogger.getInstance().log(user.getName() + " uses Basic Attack on " 
            + target.getName() + " for " + damage + " damage!");
        
        target.applyDamage(damage);

        user.gainUltimateBar(10);
        user.gainEnergy(20);
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.SINGLE_ENEMY;
    }
}