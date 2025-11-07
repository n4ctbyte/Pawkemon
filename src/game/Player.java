package game;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Hero> team;

    public Player(String name) {
        this.name = name;
        this.team = new ArrayList<>();
    }

    public void addHero(Hero hero) {
        team.add(hero);
    }

    public List<Hero> getAliveHeroes() {
        List<Hero> alive = new ArrayList<>();
        for (Hero h : team) {
            if (h.isAlive()) alive.add(h);
        }
        return alive;
    }

    public List<Hero> getDeadHeros() {
        List<Hero> dead = new ArrayList<>();
        for (Hero h : team) {
            if (h.isDead()) {
                dead.add(h);
            }
        }
        return dead;
    }

    public boolean isTeamAlive() {
        return !getAliveHeroes().isEmpty();
    }

    public String getName() { return name; }
    public List<Hero> getTeam() { return team; }
}