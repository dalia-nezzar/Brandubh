import java.util.Scanner;

public class GameLauncher {
    public static void main(String[] args) {
        System.out.println("Choose the game version:");
        System.out.println("1. Console Version");
        System.out.println("2. Graphical Version");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if (choice == 1) {
            // Execute the console version
            BrandubhConsole game = new BrandubhConsole();
            game.start(args);
        } else if (choice == 2) {
            // Execute the graphical version
            //JavaFXGame.launch(JavaFXGame.class, args);
        } else {
            System.out.println("Invalid choice. Exiting the game.");
        }

        scanner.close();
    }
}
