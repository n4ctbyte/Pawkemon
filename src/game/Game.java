package game;

import hero.BananaCat;
import hero.CryingHamster;
import hero.GregTheCrocodile;
import hero.KucingAkmal;
import hero.KucingCukurukuk;
import hero.SadCat;

import java.util.Scanner;

public class Game {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== PAWKEMON ===");

        Player player1 = createPlayer("Player 1");
        Player player2 = createPlayer("Player 2");

        BattleSystem battle = new BattleSystem(player1, player2);
        battle.startBattle();
    }

    private static Player createPlayer(String playerName) {
        Player player = new Player(playerName);
        System.out.println("\n" + playerName + ", choose your 3 heroes:");

        for (int i = 0; i < 3; i++) {
            System.out.println("\nChoose hero #" + (i + 1) + ":");
            System.out.println("1. Kucing Akmal\n2. Kucing Cukurukuk\n3. Greg\n4. Banana Cat\n5. Sad Cat\n6. Crying Hamster");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    player.addHero(new KucingAkmal().clone());
                    System.out.println(playerName + " selected Kucing Akmal");
                    break;
                
                case 2:
                    player.addHero(new KucingCukurukuk().clone());
                    System.out.println(playerName + " selected Kucing Cukurukuk");
                    break;
                
                case 3:
                    player.addHero(new GregTheCrocodile().clone());
                    System.out.println(playerName + " selected Greg");
                    break;
                
                case 4:
                    player.addHero(new BananaCat().clone());
                    System.out.println(playerName + " selected Banana Cat");
                    break;
                
                case 5:
                    player.addHero(new SadCat().clone());
                    System.out.println(playerName + " selected Sad Cat");
                    break;
                
                case 6:
                    player.addHero(new CryingHamster().clone());
                    System.out.println(playerName + " selected Crying Hamster");
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
                    i--;
                    break;
            }
        }
        return player;
    }
}