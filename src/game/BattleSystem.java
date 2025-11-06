package game;

import game.Player;
import game.Hero;
import skill.AoeSkill;
import skill.Skill;
import skill.SkillWithTargetType;
import java.util.*;

public class BattleSystem {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Scanner scanner = new Scanner(System.in);

    public BattleSystem(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = player1;
    }

    public void startBattle() {
        System.out.println("Battle Start! " + player1.getName() + " vs " + player2.getName());

        while (player1.isTeamAlive() && player2.isTeamAlive()) {
            printStatus();
            handleTurn();
            for (Hero h : player1.getTeam()) {
                h.updateStatusEffects();
            }
            for (Hero h : player2.getTeam()) {
                h.updateStatusEffects();
            }
            switchTurn();
            reduceAllStatusEffectDurations();
        }

        if (player1.isTeamAlive()) {
            System.out.println(player1.getName() + " wins!");
        } else {
            System.out.println(player2.getName() + " wins!");
        }
    }

    private void printStatus() {
        System.out.println("\n--- Current Status ---");
        printTeamStatus(player1);
        printTeamStatus(player2);
    }

    private void printTeamStatus(Player p) {
        System.out.println(p.getName() + "'s team:");
        for (Hero h : p.getTeam()) {
            System.out.print("  " + h.getName() + 
                    " HP: " + h.getCurrentHP() + "/" + h.getMaxHP() +
                    " | Energy: " + h.getCurrentEnergy() + "/" + h.getMaxEnergy() +
                    " | Ultimate: " + h.getUltimateBar() + "%" +
                    " | Attack: " + h.getAttackPower() + 
                    " | Defense: " + h.getDefense());

            List<StatusEffect> effects = h.getActiveEffects();
            if (!effects.isEmpty()) {
                System.out.print(" | Effects: ");
                for (int i = 0; i < effects.size(); i++) {
                    StatusEffect effect = effects.get(i);
                    if (effect.getType() == StatusEffect.Type.BUFF || effect.getType() == StatusEffect.Type.DEBUFF) {
                        System.out.print(effect.getAttribute() + "_" + effect.getType() + "(" + effect.getValue() + ")(" + effect.getDuration() + ")");
                    } else if (effect.getType() == StatusEffect.Type.SHIELD) {
                        System.out.print("SHIELD(" + effect.getValue() + ")(" + effect.getDuration() + ")");
                    } else {
                        System.out.print(effect.getType() + "(" + effect.getDuration() + ")");
                    }
                    if (i < effects.size() - 1) System.out.print(", ");
                }
            }
            System.out.println();
        }
    }

    private void handleTurn() {
        System.out.println("\n" + currentPlayer.getName() + "'s turn!");

        Hero activeHero = chooseHero();
        if (activeHero == null) {
            System.out.println("No valid hero selected. Turn skipped.");
            return;
        }

        Skill skill = chooseSkill(activeHero);
        if (skill == null) return;

        List<Hero> targets = chooseTargetsForSkill(activeHero, skill);
        if (targets.isEmpty()) {
            System.out.println("No valid targets for this skill.");
            return;
        }

        if (skill instanceof SkillWithTargetType) {
            SkillWithTargetType typedSkill = (SkillWithTargetType) skill;
            TargetType targetType = typedSkill.getTargetType();

            if (isAOE(targetType)) {
                invokeAOESkill(activeHero, skill, targets);
            } else {
                for (Hero target : targets) {
                    skill.use(activeHero, target);
                }
            }
        } else {
            if (!targets.isEmpty()) {
                skill.use(activeHero, targets.get(0));
            }
        }

        if (!(skill instanceof skill.BasicAttack)) {
            activeHero.gainUltimateBar(20);
        }

        for (Hero h : currentPlayer.getTeam()) {
            h.gainEnergy(10);
        }

        for (Skill s : activeHero.getSkills()) {
            s.reduceCooldown();
        }
    }

    private Hero chooseHero() {
        while (true) {
            System.out.println("Choose your hero:");
            int i = 0;
            List<Hero> aliveHeroes = currentPlayer.getAliveHeroes();
            for (Hero h : aliveHeroes) {
                System.out.println((i + 1) + ". " + h.getName() + " (HP: " + h.getCurrentHP() + ")");
                i++;
            }

            if (aliveHeroes.isEmpty()) {
                System.out.println("No alive heroes!");
                return null;
            }

            int choice = scanner.nextInt() - 1;
            if (choice >= 0 && choice < aliveHeroes.size()) {
                Hero selectedHero = aliveHeroes.get(choice);
                if (selectedHero.isStunned()) {
                    System.out.println(selectedHero.getName() + " is stunned! Please choose another hero.");
                    continue;
                }
                return selectedHero;
            } else {
                System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private Skill chooseSkill(Hero hero) {
        System.out.println("Choose a skill for " + hero.getName() + ":");
        int i = 0;
        for (Skill s : hero.getSkills()) {
            String status = s.isReady() ? " [READY]" : " [COOLDOWN: " + s.getCurrentCooldown() + "]";
            System.out.println((i + 1) + ". " + s.getName() + " (Cost: " + s.getEnergyCost() + ")" + status);
            i++;
        }
        int choice = scanner.nextInt() - 1;
        if (choice >= 0 && choice < hero.getSkills().size()) {
            Skill selected = hero.getSkills().get(choice);
            if (selected.isReady() && hero.getCurrentEnergy() >= selected.getEnergyCost()) {
                hero.currentEnergy -= selected.getEnergyCost();
                if (selected.getCooldown() > 0) {
                    selected.startCooldown();
                }
                return selected;
            } else {
                System.out.println("Not enough energy or skill on cooldown!");
                return null;
            }
        }
        System.out.println("Invalid choice!");
        return null;
    }

    private void reduceAllStatusEffectDurations() {
        for (Hero h : player1.getTeam()) {
            h.reduceStatusEffectDurations();
        }
        for (Hero h : player2.getTeam()) {
            h.reduceStatusEffectDurations();
        }
    }

    private List<Hero> chooseTargetsForSkill(Hero user, Skill skill) {
        if (skill instanceof SkillWithTargetType) {
            SkillWithTargetType typedSkill = (SkillWithTargetType) skill;
            TargetType targetType = typedSkill.getTargetType();

            switch (targetType) {
                case SELF:
                    return Arrays.asList(user);
                case SINGLE_ALLY:
                    Hero ally = chooseAlly();
                    return ally != null ? Arrays.asList(ally) : new ArrayList<>();
                case SINGLE_ENEMY:
                    Hero enemy = chooseEnemy();
                    return enemy != null ? Arrays.asList(enemy) : new ArrayList<>();
                case ALL_ALLIES:
                    return new ArrayList<>(currentPlayer.getAliveHeroes());
                case ALL_ENEMIES:
                    Player enemyTeam = (currentPlayer == player1) ? player2 : player1;
                    return new ArrayList<>(enemyTeam.getAliveHeroes());
                default:
                    System.out.println("Unknown target type: " + targetType);
                    return new ArrayList<>();
            }
        } else {
            Hero singleTarget = chooseEnemy();
            return singleTarget != null ? Arrays.asList(singleTarget) : new ArrayList<>();
        }
    }

    private Hero chooseAlly() {
        System.out.println("Choose an ally:");
        int i = 0;
        for (Hero h : currentPlayer.getAliveHeroes()) {
            System.out.println((i + 1) + ". " + h.getName() + " (HP: " + h.getCurrentHP() + ")");
            i++;
        }
        int choice = scanner.nextInt() - 1;
        if (choice >= 0 && choice < currentPlayer.getAliveHeroes().size()) {
            return currentPlayer.getAliveHeroes().get(choice);
        }
        System.out.println("Invalid choice!");
        return null;
    }

    private Hero chooseEnemy() {
        Player targetTeam = (currentPlayer == player1) ? player2 : player1;
        System.out.println("Choose target:");
        int i = 0;
        for (Hero h : targetTeam.getAliveHeroes()) {
            System.out.println((i + 1) + ". " + h.getName() + " (HP: " + h.getCurrentHP() + ")");
            i++;
        }
        int choice = scanner.nextInt() - 1;
        if (choice >= 0 && choice < targetTeam.getAliveHeroes().size()) {
            return targetTeam.getAliveHeroes().get(choice);
        }
        System.out.println("Invalid choice!");
        return null;
    }

    private boolean isAOE(TargetType targetType) {
        return targetType == TargetType.ALL_ALLIES || targetType == TargetType.ALL_ENEMIES;
    }

    private void invokeAOESkill(Hero user, Skill skill, List<Hero> targets) {
        try {
            java.lang.reflect.Method method = skill.getClass().getMethod("useAOE", Hero.class, List.class, Player.class);
            Player player = (currentPlayer == player1) ? player1 : player2;
            method.invoke(skill, user, targets, player);
        } catch (NoSuchMethodException e) {
            try {
                java.lang.reflect.Method method = skill.getClass().getMethod("useAOE", Hero.class, List.class);
                method.invoke(skill, user, targets);
            } catch (Exception ex) {
                System.out.println("Could not invoke aoe skil " + ex.getMessage());
                for (Hero target : targets) {
                    skill.use(user, target);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not invoke aoe skil " + e.getMessage());
            for (Hero target : targets) {
                skill.use(user, target);
            }
        }
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
}
