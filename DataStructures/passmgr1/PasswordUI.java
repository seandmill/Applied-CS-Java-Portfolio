package passmgr1;

import java.util.Scanner;
import java.io.IOException;

public class PasswordUI {

    // initialize class variables to be used across passmgr1 package
    public static Scanner input = new Scanner(System.in);
    public static PasswordManager pm = new PasswordManager();

    /**
     * Main (static) method, invokes PasswordManager's initialize method and calls
     * the mainMenu method
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        pm.initialize();
        System.out.println("---------------PassMe v1.0---------------");
        System.out.println("*****************************************");
        System.out.println();
        mainMenu();
    }

    /**
     * Main method is the primary console UI. The user is always returned to the main menu
     * after every interaction. The application will not exit unless the user explicitly 
     * chooses Option "5"
     */
    public static void mainMenu() {
        System.out.println();
        System.out.println("----------------MAIN MENU----------------");
        System.out.println("1. Create new password");
        System.out.println("2. Search (lookup) existing password");
        System.out.println("3. Update existing password");
        System.out.println("4. Delete account & password");
        System.out.println("5. Exit");
        System.out.println();

        while (true) {
            System.out.print("Enter Option: ");
            if (input.hasNextInt()) {
                int option = input.nextInt();
                if(option >= 1 && option <= 5) {
                    switch (option) {
                        //create password
                        case 1: pm.search("Create");
                        break;
                        case 2: pm.search("Search");
                        break;
                        case 3: pm.search("Update");
                        break;
                        case 4: pm.search("Delete");
                        break;
                        case 5:
                        System.out.println("Thanks for using PassMe v1.0! Goodbye.");
                        break;
                    }
                    break;
                }
            }
            System.out.println("INVALID");
            input.nextLine();
        }

    }
}
