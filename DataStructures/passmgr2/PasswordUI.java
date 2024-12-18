package passmgr2;

import java.util.Scanner;
import java.io.IOException;

public class PasswordUI {

    // initialize class variables to be used across passmgr1 package
    public static Scanner input = new Scanner(System.in);
    public static PasswordManager pm = new PasswordManager();

    /**
     * Main (static) method, invokes PasswordManager's initialize method and calls the mainMenu
     * method
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        pm.initialize();
        System.out.println("---------------PassMe v2.0---------------");
        System.out.println("*****************************************");
        System.out.println();
        mainMenu();
    }

    /**
     * Main method is the primary console UI. The user is always returned to the main menu after
     * every interaction. The application will not exit unless the user explicitly chooses Option
     * "8"
     */
    public static void mainMenu() {
        System.out.println();
        System.out.println("----------------MAIN MENU----------------");
        System.out.println("1. (ENHANCED!) Create new password");
        System.out.println("2. Search existing password by account / URL");
        System.out.println("3. (ENHANCED!) Update existing password");
        System.out.println("4. Delete account & password");
        System.out.println("5. (NEW!) Search by account prefix");
        System.out.println("6. (NEW!) Search by category");
        System.out.println("7. (NEW!) Simulate login w/ auto-fill (browser feature)");
        System.out.println("8. Exit");
        System.out.println("-----------------------------------------");
        System.out.println();

        while (true) {
            System.out.print("Enter Option: ");
            if (input.hasNextInt()) {
                int option = input.nextInt();
                if (option >= 1 && option <= 8) {
                    switch (option) {
                        // create password
                        case 1:
                            pm.search("Create");
                            break;
                        case 2:
                            pm.search("Search");
                            break;
                        case 3:
                            pm.search("Update");
                            break;
                        case 4:
                            pm.search("Delete");
                            break;
                        case 5:
                            pm.searchPrefix();
                            break;
                        case 6:
                            pm.searchByCategory();
                            break;
                        case 7:
                            pm.simulateAutoFill();
                            break;
                        case 8:
                            System.out.println("Thanks for using PassMe v2.0! Goodbye.");
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
