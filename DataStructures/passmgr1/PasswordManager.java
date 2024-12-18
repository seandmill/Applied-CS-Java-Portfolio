package passmgr1;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class PasswordManager {

    // initialize constants and class variables
    private static final String PASSWORD_DB = "passwordDB.txt";
    private static final String COLUMN_DELIMITER = " ,./ ";
    private static final String PASSWORD_DELIMITER = " ///// ";
    private static final File f = new File(PASSWORD_DB);
    public static List<PasswordEntry> passwords = new ArrayList<>();
    public static final PasswordSecurity pwSec = new PasswordSecurity(15);

    public void initialize() throws IOException {
        if (!f.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASSWORD_DB, false))) {
            }
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(PASSWORD_DB))) {
                passwords = br.lines().map(this::parsePasswordEntry).collect(Collectors.toList());
            }
        }
    }

    /**
     * Parses lines in passwordDB.txt to retrieve account name, category, and password
     * with version history. Uses COLUMN_DELIMITER and PASSWORD_DELIMITER to split
     * @param line
     * @return new PasswordEntry
     */
    private PasswordEntry parsePasswordEntry(String line) {
        String[] fields = line.split(COLUMN_DELIMITER);
        if (fields.length < 3)
            return null;
        String account = fields[0].trim();
        String category = fields[1].trim();
        String[] passwordHistory = fields[2].trim().split(PASSWORD_DELIMITER);

        PasswordHistory<String> history = new PasswordHistory<>();
        for (int i = passwordHistory.length - 1; i >= 0; i--) {
            history.addFirst(passwordHistory[i]);
        }
        return new PasswordEntry(account, category, history);
    }

    /**
     * Passes through user input for account from the main menu. Prompts the user
     * to enter an account category and password. Password is immediately encrypted
     * and added as first node in PasswordHistory DoublyLinkedList. A new PasswordEntry (PE)
     * object is created with the account, category, and PasswordHistory. The PE object is added
     * to the passwords ArrayList, and the writePassword method is called to append account
     * details to the end of passwordDB.txt.
     * @param account
     */
    public void create(String account) {
        System.out.print("Enter Category: ");
        String category = PasswordUI.input.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = pwSec.encrypt(PasswordUI.input.nextLine());

        PasswordHistory<String> ph = new PasswordHistory<>();
        ph.addFirst(password);
        PasswordEntry pe = new PasswordEntry(account, category, ph);
        passwords.add(pe);

        writePassword(pe, "Create");
        System.out.println("Password created successfully!");
        PasswordUI.mainMenu();
    }

    /**
     * Prompts the user to enter an account name, which is trimmed and stored in a string.
     * Calls the exists method to search for account name in passwords list (case-insensitive).
     * If an account is returned, account details are displayed and the handle method is called.
     * If an account is not returned, and the action is "Create" account, the create method is invoked.
     * Else, the user is notified the account wasn't found and is returned to the main menu.
     * @param action
     */
    public void search(String action) {
        PasswordUI.input.nextLine();
        System.out.print("Enter Account Name / URL: ");
        String account = PasswordUI.input.nextLine().trim();
        PasswordEntry pe = exists(account);
        if (pe != null) {
            displayAccountDetails(pe);
            handle(action, pe);
        } else if (action.equals("Create")) {
            create(account);
        } else {
            System.out.println("Account not found!");
            PasswordUI.mainMenu();
        }
    }

    /**
     * Displays the account details using the PasswordEntry classes get() methods
     * @param pe
     */
    private void displayAccountDetails(PasswordEntry pe) {
        System.out.println("Account Name: " + pe.getAccount());
        System.out.println("Account Category: " + pe.getCategory());
        System.out.println("Current Password: " + pwSec.decrypt(pe.getPassword()));
    }

    /**
     * Handle logic based on defined action (search, update, delete, create)
     * @param action
     * @param pe
     */
    private void handle(String action, PasswordEntry pe) {
        switch (action) {
            case "Search":
                PasswordUI.mainMenu();
                break;
            case "Update":
                update(pe);
                break;
            case "Delete":
                delete(pe);
                break;
            case "Create":
                System.out.println("Password already exists for this account.");
                PasswordUI.mainMenu();
                break;
        }
    }

    /**
     * Prompts user to enter a new password for the account. This password is immediately encrypted,
     * and the associated PasswordEntry (pe) setPassword method is invoked. The writePassword method
     * is called, which updates passwordDB.txt. The console confirms the password has been updated and
     * the user is taken back to the main menu.
     * @param pe
     */
    private void update(PasswordEntry pe) {
        System.out.print("Please enter a new password: ");
        String newPassword = pwSec.encrypt(PasswordUI.input.nextLine());
        pe.setPassword(newPassword);
        writePassword(pe, "Update");
        System.out.println("Password updated successfully!");
        PasswordUI.mainMenu();
    }

    /**
     * The user is prompted to confirm they want to delete the account. If YES (Option "1"),  the
     * pe object is removed from the passwords ArrayList and the dbUpdate method is called to update
     * passwordDB.txt. If NO (Option "2"), the user is taken back to the main menu.
     * @param pe
     */
    private void delete(PasswordEntry pe) {
        System.out.println("Are you sure you want to delete this account?");
        System.out.println("1. Yes");
        System.out.println("2. No");

        while (true) {
            System.out.print("Enter Option: ");
            if (PasswordUI.input.hasNextInt()) {
                int option = PasswordUI.input.nextInt();
                if (option == 1) {
                    passwords.remove(pe);
                    dbUpdate();
                    System.out.println("Password deleted successfully!");
                    break;
                } else if (option == 2) {
                    break;
                } else {
                    System.out.println("INVALID");
                }
            } else {
                System.out.println("INVALID");
                PasswordUI.input.nextLine();
            }
        }
        PasswordUI.mainMenu();
    }

    /**
     * passwordDB.txt is rewritten based on the latest collection of PasswordEntries in the passwords ArrayList.
     */
    private void dbUpdate() {
        try {
            List<String> updatedEntries =
                    passwords.stream().map(this::formatPasswordEntry).collect(Collectors.toList());
            Files.write(Paths.get(PASSWORD_DB), updatedEntries);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Filters the passwords ArrayList based on a case-insensitive account string. Returns the first PE match, else
     * returns null.
     * @param account
     * @return PasswordEntry object, if exists
     */
    public PasswordEntry exists(String account) {
        return passwords.stream().filter(e -> e.getAccount().equalsIgnoreCase(account)).findFirst()
                .orElse(null);
    }

    /**
     * If the action is "Update" password, the dbUpdate method is called to update passwordDB.txt. If the action is
     * "Create" password, a BufferedWriter is invoked to write the account details (using formatPasswordEntry method)
     * and a new line to passwordDB.txt.
     * @param pe
     * @param type
     */
    private void writePassword(PasswordEntry pe, String type) {
        if (type.equals("Update")) {
            dbUpdate();
        } else if (type.equals("Create")) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASSWORD_DB, true))) {
                bw.write(formatPasswordEntry(pe));
                bw.newLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Formats the password entry in our database-friendly format, including COLUMN_DELIMITER
     * @param pe
     * @return
     */
    private String formatPasswordEntry(PasswordEntry pe) {
        return pe.getAccount() + COLUMN_DELIMITER + pe.getCategory() + COLUMN_DELIMITER
                + pe.getPasswordString();
    }
}
