package game;
import com.fasterxml.jackson.databind.ObjectMapper;

import skill.BasicAttack;
import skill.BuffSkill;
import skill.DebuffSkill;
import skill.DotSkill;
import skill.HealSkill;
import skill.HoTSkill;
import skill.Skill;
import skill.SkillData;
import skill.StunSkill;
import skill.Ultimate;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.util.*;

public class Game {
    private static Scanner scanner = new Scanner(System.in);
    private static List<HeroData> heroList;

    public static void main(String[] args) {
        loadHeroData();

        System.out.println("=== Pokemon-like Battle Game ===");

        Player player1 = createPlayer("Player 1");
        Player player2 = createPlayer("Player 2");

        BattleSystem battle = new BattleSystem(player1, player2);
        battle.startBattle();
    }

    private static void loadHeroData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            heroList = mapper.readValue(new File("src/heroes.json"), new TypeReference<List<HeroData>>() {});
            System.out.println("Hero data loaded successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load hero data: " + e.getMessage());
            System.exit(1);
        }
    }

    private static Player createPlayer(String playerName) {
        Player player = new Player(playerName);
        System.out.println("\n" + playerName + ", choose your 3 heroes:");

        for (int i = 0; i < 3; i++) {
            System.out.println("\nChoose hero #" + (i + 1) + ":");
            for (int j = 0; j < heroList.size(); j++) {
                System.out.println((j + 1) + ". " + heroList.get(j).name);
            }

            int choice = scanner.nextInt() - 1;
            if (choice >= 0 && choice < heroList.size()) {
                HeroData data = heroList.get(choice);
                Hero hero = new Hero(data.name, data.maxHP, data.attackPower, data.defense, data.maxEnergy);
                for (SkillData skillData : data.skills) {
                    Skill skill = createSkill(skillData);
                    hero.addSkill(skill);
                }
                player.addHero(hero);
                System.out.println(playerName + " selected " + data.name);
            } else {
                System.out.println("Invalid choice! Try again.");
                i--;
            }
        }
        return player;
    }

    private static Skill createSkill(SkillData data) {
    TargetType targetType = TargetType.valueOf(data.targetType);

    switch (data.type) {
        case "BasicAttack":
            return new BasicAttack();
        case "Ultimate":
            return new Ultimate(data.name, data.energyCost);
        case "HealSkill":
            return new HealSkill(data.name, data.energyCost, data.cooldown, data.healAmount, targetType);
        case "StunSkill":
            return new StunSkill(data.name, data.energyCost, data.cooldown, data.duration, targetType);
        case "DotSkill":
            return new DotSkill(data.name, data.energyCost, data.cooldown, data.damagePerTurn, data.duration, targetType);
        case "HoTSkill":
            return new HoTSkill(data.name, data.energyCost, data.cooldown, data.healPerTurn, data.duration, targetType);
        case "BuffSkill":
            return new BuffSkill(data.name, data.energyCost, data.cooldown, data.buffAmount, data.duration, Attribute.valueOf(data.attributeType), targetType);
        case "DebuffSkill":
            return new DebuffSkill(data.name, data.energyCost, data.cooldown, data.debuffAmount, data.duration, Attribute.valueOf(data.attributeType), targetType);
        default:
            return new BasicAttack();
        }
    }
}