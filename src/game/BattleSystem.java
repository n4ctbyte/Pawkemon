package game;

import game.Player;
import game.Hero;
import skill.AoeSkill;
import skill.BasicAttack;
import skill.Skill;
import skill.SkillWithTargetType;
import skill.Ultimate;
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

        Skill skill = null;

        while (true) {
            skill = chooseSkill(activeHero);
            if (skill == null) {
                activeHero = chooseHero();
                if (activeHero == null) {
                    System.out.println("No valid hero selected. Turn skipped.");
                    return;
                }
                continue;
            }

            List<Hero> targets = chooseTargetsForSkill(activeHero, skill);
            if (targets.isEmpty()) {
                continue;
            }

            if (!(skill instanceof BasicAttack)) {
                activeHero.currentEnergy -= skill.getEnergyCost();
                skill.startCooldown();
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

            if (skill instanceof Ultimate) {
                activeHero.setUltimateBar(0);
            }

            break;
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
        while (true) {
            System.out.println("Choose a skill for " + hero.getName() + ":");
            int i = 0;
            for (Skill s : hero.getSkills()) {
                String status;
                if (s instanceof Ultimate) {
                    status = (hero.getUltimateBar() >= 100) ? " [READY]" : " [ULTIMATE NOT READY]";
                } else {
                    status = s.isReady() ? " [READY]" : " [COOLDOWN: " + s.getCurrentCooldown() + "]";
                }
                System.out.println((i + 1) + ". " + s.getName() + " (Cost: " + s.getEnergyCost() + ")" + status);
                i++;
            }
            System.out.println((i + 1) + ". Back");

            int choice = scanner.nextInt() - 1;

            if (choice == i) {
                System.out.println("Going back to choose hero.");
                return null;
            }

            if (choice >= 0 && choice < hero.getSkills().size()) {
                Skill selected = hero.getSkills().get(choice);

                if (selected instanceof Ultimate) {
                    if (hero.getUltimateBar() >= 100) {
                        return selected;
                    } else {
                        System.out.println("Ultimate not ready! Choose another skill.");
                        continue;
                    }
                } else {
                    if (selected.isReady() && hero.getCurrentEnergy() >= selected.getEnergyCost()) {
                        return selected;
                    } else {
                        System.out.println("Not enough energy or skill on cooldown! Choose another skill.");
                        continue;
                    }
                }
            }
            System.out.println("Invalid choice! Try again.");
        }
    }

    private List<Hero> chooseTargetsForSkill(Hero user, Skill skill) {
        boolean isTaunted = false;
        Hero taunter = null;
        for (StatusEffect effect : user.getActiveEffects()) {
            if (effect.getType() == StatusEffect.Type.TAUNT) {
                isTaunted = true;
                Player enemyTeam = (currentPlayer == player1) ? player2 : player1;
                for (Hero h : enemyTeam.getTeam()) {
                    if (h.getName().equals(effect.getSource())) {
                        taunter = h;
                        break;
                    }
                }
                break;
            }
        }

        if (isTaunted && taunter != null) {
            if (skill instanceof SkillWithTargetType) {
                SkillWithTargetType typedSkill = (SkillWithTargetType) skill;
                TargetType targetType = typedSkill.getTargetType();

                if (targetType == TargetType.SINGLE_ENEMY) {
                    return Arrays.asList(taunter);
                } else if (targetType == TargetType.ALL_ENEMIES) {
                    return Arrays.asList(taunter);
                } else {
                    return Arrays.asList(taunter);
                }
            } else {
                return Arrays.asList(taunter);
            }
        }

        if (skill instanceof SkillWithTargetType) {
            SkillWithTargetType typedSkill = (SkillWithTargetType) skill;
            TargetType targetType = typedSkill.getTargetType();

            switch (targetType) {
                case SELF:
                    return Arrays.asList(user);
                case SINGLE_ALLY:
                    Hero ally = chooseAlly(skill);
                    if (ally == null) return new ArrayList<>();
                    return Arrays.asList(ally);
                case SINGLE_ENEMY:
                    Hero enemy = chooseEnemy();
                    if (enemy == null) return new ArrayList<>();
                    return Arrays.asList(enemy);
                case ALL_ALLIES:
                    if (skill.getName().equals("Divine Tears")) {
                        return new ArrayList<>(currentPlayer.getTeam());
                    } else {
                        return new ArrayList<>(currentPlayer.getAliveHeroes());
                    }
                case ALL_ENEMIES:
                    Player enemyTeam = (currentPlayer == player1) ? player2 : player1;
                    return new ArrayList<>(enemyTeam.getAliveHeroes());
                default:
                    System.out.println("Unknown target type: " + targetType);
                    return new ArrayList<>();
            }
        } else {
            Hero singleTarget = chooseEnemy();
            if (singleTarget == null) return new ArrayList<>();
            return Arrays.asList(singleTarget);
        }
    }

    private Hero chooseAlly(Skill skill) {
        while (true) {
            System.out.println("Choose an ally:");
            int i = 0;
            List<Hero> availableAllies = new ArrayList<>();
            for (Hero h : currentPlayer.getTeam()) {
                if (skill.getName().equals("Divine Tears")) {
                    String status = h.isAlive() ? " (HP: " + h.getCurrentHP() + ")" : " (DEAD)";
                    System.out.println((i + 1) + ". " + h.getName() + status);
                    availableAllies.add(h);
                    i++;
                } else {
                    if (h.isAlive()) {
                        System.out.println((i + 1) + ". " + h.getName() + " (HP: " + h.getCurrentHP() + ")");
                        availableAllies.add(h);
                        i++;
                    }
                }
            }
            System.out.println((i + 1) + ". Back");

            if (availableAllies.isEmpty()) {
                System.out.println("No valid allies to choose.");
                return null;
            }

            int choice = scanner.nextInt() - 1;

            if (choice == i) {
                System.out.println("Going back to choose skill.");
                return null;
            }

            if (choice >= 0 && choice < availableAllies.size()) {
                Hero selected = availableAllies.get(choice);

                if (skill.getName().equals("Divine Tears") && selected.isAlive()) {
                    System.out.println(selected.getName() + " is still alive! You can only revive dead heroes.");
                    continue;
                }

                return selected;
            }
            System.out.println("Invalid choice! Try again.");
        }
    }

    private Hero chooseEnemy() {
        while (true) {
            Player targetTeam = (currentPlayer == player1) ? player2 : player1;
            System.out.println("Choose target:");
            int i = 0;
            for (Hero h : targetTeam.getAliveHeroes()) {
                System.out.println((i + 1) + ". " + h.getName() + " (HP: " + h.getCurrentHP() + ")");
                i++;
            }
            System.out.println((i + 1) + ". Back");

            int choice = scanner.nextInt() - 1;

            if (choice == i) {
                System.out.println("Going back to choose skill.");
                return null;
            }

            if (choice >= 0 && choice < targetTeam.getAliveHeroes().size()) {
                return targetTeam.getAliveHeroes().get(choice);
            }
            System.out.println("Invalid choice! Try again.");
        }
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

    private void reduceAllStatusEffectDurations() {
        for (Hero h : player1.getTeam()) {
            h.reduceStatusEffectDurations();
        }
        for (Hero h : player2.getTeam()) {
            h.reduceStatusEffectDurations();
        }
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
}