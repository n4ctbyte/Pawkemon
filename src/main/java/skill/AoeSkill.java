package skill;

import game.Hero;
import java.util.*;

public interface AoeSkill {
    void useAOE(Hero user, List<Hero> targets);
}