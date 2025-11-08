package skill;
import game.Hero;

public class TauntSkill extends Skill {
    public TauntSkill(String name, int energyCost, int cooldown) {
        super(name, energyCost, cooldown);
    }

    @Override
    public void use(Hero user, Hero target) {
        System.out.println(user.getName() + " taunts " + target.getName() + "! Next turn, " + target.getName() + " must attack " + user.getName() + ".");
    }
}