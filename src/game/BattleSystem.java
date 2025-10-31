package game;
import game.Player;
import game.Hero;
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
            // Panggil update efek setelah turn selesai
            for (Hero h : player1.getTeam()) {
                h.updateStatusEffects();
            }
            for (Hero h : player2.getTeam()) {
                h.updateStatusEffects();
            }
            switchTurn();
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
            System.out.println("  " + h.getName() + " HP: " + h.getCurrentHP() + "/" + h.maxHP +
                    " | Energy: " + h.getCurrentEnergy() + "/" + h.maxEnergy +
                    " | Ultimate: " + h.getUltimateBar() + "%");
        }
    }

    private void handleTurn() {
        System.out.println("\n" + currentPlayer.getName() + "'s turn!");

        Hero activeHero = chooseHero();
        if (activeHero == null || activeHero.isStunned()) {
            if (activeHero != null && activeHero.isStunned()) {
                System.out.println(activeHero.getName() + " is stunned and skips turn!");
            }
            return;
        }

        Skill skill = chooseSkill(activeHero);
        if (skill == null) return;

        // --- Bagian penting: Pilih target berdasarkan skill ---
        List<Hero> targets = chooseTargetsForSkill(activeHero, skill);
        if (targets.isEmpty()) {
            System.out.println("No valid targets for this skill.");
            return;
        }

        // --- Gunakan skill berdasarkan apakah AOE atau single ---
        if (skill instanceof SkillWithTargetType) {
            SkillWithTargetType typedSkill = (SkillWithTargetType) skill;
            TargetType targetType = typedSkill.getTargetType();

            if (isAOE(targetType)) {
                // Jika AOE, coba panggil method useAOE jika ada
                invokeAOESkill(activeHero, skill, targets); // <-- Casting ke Skill untuk invokeAOESkill
            } else {
                // Jika single target, gunakan seperti biasa
                for (Hero target : targets) {
                    skill.use(activeHero, target); // <-- SkillWithTargetType extends Skill, jadi ini aman
                }
            }
        } else {
            // Jika skill tidak mengimplementasi SkillWithTargetType, gunakan single target
            if (!targets.isEmpty()) {
                skill.use(activeHero, targets.get(0));
            }
        }

        // Ultimate bar naik jika bukan basic attack
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

    // Method untuk memilih hero
    private Hero chooseHero() {
        System.out.println("Choose your hero:");
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

    // Method untuk memilih skill
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
                selected.startCooldown();
                return selected;
            } else {
                System.out.println("Not enough energy or skill on cooldown!");
                return null;
            }
        }
        System.out.println("Invalid choice!");
        return null;
    }

    // Method untuk memilih target berdasarkan skill
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
            // Jika skill tidak mengimplementasi targetType, gunakan single target
            Hero singleTarget = chooseEnemy();
            return singleTarget != null ? Arrays.asList(singleTarget) : new ArrayList<>();
        }
    }

    // Method untuk memilih hero dari tim sendiri
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

    // Method untuk memilih musuh (digunakan untuk single target)
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

    // Method untuk mengecek apakah skill adalah AOE
    private boolean isAOE(TargetType targetType) {
        return targetType == TargetType.ALL_ALLIES || targetType == TargetType.ALL_ENEMIES;
    }

    // Method untuk memanggil skill AOE
    private void invokeAOESkill(Hero user, Skill skill, List<Hero> targets) {
        // Kita cek apakah skill memiliki method useAOE
        // Kita gunakan reflection untuk memanggil method useAOE jika ada
        try {
            java.lang.reflect.Method method = skill.getClass().getMethod("useAOE", Hero.class, List.class);
            method.invoke(skill, user, targets);
        } catch (Exception e) {
            // Jika method useAOE tidak ditemukan, gunakan method biasa ke semua target
            for (Hero target : targets) {
                skill.use(user, target);
            }
        }
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
}