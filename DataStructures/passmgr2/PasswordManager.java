package passmgr2;

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
    public static PasswordTrie<String> trie = new PasswordTrie<>();

    public void initialize() throws IOException {
        if (!f.exists()) {
            // Create the database if it doesn't exist
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASSWORD_DB, false))) {
                bw.close();
            }
        } else {
            // Read the existing file and check if auto-fill column is missing
            List<String> updatedEntries = new ArrayList<>();
            boolean updateRequired = false;

            try (BufferedReader br = new BufferedReader(new FileReader(PASSWORD_DB))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(COLUMN_DELIMITER);
                    if (fields.length == 3) {
                        // The old format without the auto-fill column, add default "NO"
                        line += COLUMN_DELIMITER + "NO";
                        updateRequired = true;
                    }
                    updatedEntries.add(line);
                }
            }

            // If update is required, rewrite the file with the new format
            if (updateRequired) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(PASSWORD_DB, false))) {
                    for (String entry : updatedEntries) {
                        bw.write(entry);
                        bw.newLine();
                    }
                }
            }

            // Now parse the updated entries into PasswordEntry objects
            passwords = updatedEntries.stream().map(this::parsePasswordEntry)
                    .collect(Collectors.toList());
            for (PasswordEntry pe : passwords) {
                trie.insert(pe.getAccount(), pe);
            }
        }
    }

    /**
     * Parses lines in passwordDB.txt to retrieve account name, category, and password with version
     * history. Uses COLUMN_DELIMITER and PASSWORD_DELIMITER to split
     * 
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

        boolean autoEnabled = false;
        if (fields.length > 3) {
            autoEnabled = fields[3].trim().equalsIgnoreCase("YES");
        }
        return new PasswordEntry(account, category, history, autoEnabled);
    }

    /**
     * Passes through user input for account from the main menu. Prompts the user to enter an
     * account category and password. Password is immediately encrypted and added as first node in
     * PasswordHistory DoublyLinkedList. A new PasswordEntry (PE) object is created with the
     * account, category, and PasswordHistory. The PE object is added to the passwords ArrayList,
     * and the writePassword method is called to append account details to the end of
     * passwordDB.txt.
     * 
     * @param account
     */
    public void create(String account) {
        System.out.print("Enter Category: ");
        String category = PasswordUI.input.nextLine().trim();

        PasswordHistory<String> ph = new PasswordHistory<>();
        ph.addFirst(generate("Create", null));

        System.out.println();

        boolean autoEnabled = false;
        System.out.println("Enable Auto-Fill for this account?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        while (true) {
            System.out.print("Enter Option: ");
            if (PasswordUI.input.hasNextInt()) {
                int option = PasswordUI.input.nextInt();
                PasswordUI.input.nextLine();
                if (option >= 1 && option <= 2) {
                    switch (option) {
                        case 1:
                            autoEnabled = true;
                            break;
                        case 2:
                            autoEnabled = false;
                            break;
                    }
                    break;
                }
            } else {
                System.out.println("INVALID");
                PasswordUI.input.nextLine();
            }
        }

        PasswordEntry pe = new PasswordEntry(account, category, ph, autoEnabled);
        passwords.add(pe);

        trie.insert(account, pe);

        writePassword(pe, "Create");
        System.out.println("Password created successfully!");
        PasswordUI.mainMenu();
    }

    /**
     * The generate method is the primary mechanism for creating and updating passwords. The user
     * Has the option to generate a strong password, enter a manual password, or use an existing
     * password.
     * 
     * @param action
     * @param pe
     * @return password
     */
    public String generate(String action, PasswordEntry pe) {
        String password = "";
        System.out.println();
        System.out.println("Please choose from the following options: ");
        System.out.println("1. Generate a strong password automatically");
        System.out.println("2. Enter a password manually");
        if (!action.equals("Create")) {
            System.out.println("3. Use a previous password");
        }

        while (true) {
            System.out.print("Enter Option: ");
            if (PasswordUI.input.hasNextInt()) {
                int option = PasswordUI.input.nextInt();
                PasswordUI.input.nextLine();

                if ((option >= 1 && option <= 3 && !action.equals("Create"))
                        || (option >= 1 && option <= 2 && action.equals("Create"))) {
                    switch (option) {
                        case 1:
                            Random random = new Random();
                            int length = random.nextInt(8, 15);
                            password = pwSec.encrypt(PasswordGenerator.generatePassword(length));
                            System.out.println("Your new password is: " + pwSec.decrypt(password));
                            break;
                        case 2:
                            System.out.print("Enter a new password: ");
                            password = pwSec.encrypt(PasswordUI.input.nextLine().trim());
                            break;
                        case 3:
                            if (pe != null) {
                                System.out.println("Please choose from your previous passwords: ");
                                int count = 1;
                                List<String> peHistory = new ArrayList<>();
                                for (String s : pe.getPasswordHistory()) {
                                    peHistory.add(s);
                                    if (s != null)
                                        System.out.println(count++ + ". " + pwSec.decrypt(s));
                                }
                                while (true) {
                                    System.out.print("Enter Option: ");
                                    if (PasswordUI.input.hasNextInt()) {
                                        int historyOption = PasswordUI.input.nextInt();
                                        PasswordUI.input.nextLine();
                                        if (historyOption >= 1
                                                && historyOption <= peHistory.size()) {
                                            password = peHistory.get(historyOption - 1);
                                            break;
                                        }
                                    } else {
                                        System.out.println("INVALID");
                                        PasswordUI.input.nextLine();
                                    }
                                }
                            }
                            break;
                    }
                    break;
                }
            } else {
                System.out.println("INVALID");
                PasswordUI.input.nextLine();
            }
        }

        return password;
    }

    /**
     * Prompts the user to enter an account name, which is trimmed and stored in a string. Calls the
     * exists method to search for account name in passwords list (case-insensitive). If an account
     * is returned, account details are displayed and the handle method is called. If an account is
     * not returned, and the action is "Create" account, the create method is invoked. Else, the
     * user is notified the account wasn't found and is returned to the main menu.
     * 
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
     * 
     * @param pe
     */
    private void displayAccountDetails(PasswordEntry pe) {
        System.out.println("Account Name: " + pe.getAccount());
        System.out.println("Account Category: " + pe.getCategory());
        System.out.println("Current Password: " + pwSec.decrypt(pe.getPassword()));
    }

    /**
     * Handle logic based on defined action (search, update, delete, create)
     * 
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
     * is called, which updates passwordDB.txt. The console confirms the password has been updated
     * and the user is taken back to the main menu.
     * 
     * @param pe
     */
    private void update(PasswordEntry pe) {
        pe.setPassword(generate("Update", pe));
        writePassword(pe, "Update");
        System.out.println("Password updated successfully!");
        PasswordUI.mainMenu();
    }

    /**
     * The user is prompted to confirm they want to delete the account. If YES (Option "1"), the pe
     * object is removed from the passwords ArrayList and the dbUpdate method is called to update
     * passwordDB.txt. If NO (Option "2"), the user is taken back to the main menu.
     * 
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
                    trie.delete(pe.getAccount());
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
     * passwordDB.txt is rewritten based on the latest collection of PasswordEntries in the
     * passwords ArrayList.
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
     * Filters the passwords ArrayList based on a case-insensitive account string. Returns the first
     * PE match, else returns null.
     * 
     * @param account
     * @return PasswordEntry object, if exists
     */
    public PasswordEntry exists(String account) {
        return passwords.stream().filter(e -> e.getAccount().equalsIgnoreCase(account)).findFirst()
                .orElse(null);
    }

    /**
     * If the action is "Update" password, the dbUpdate method is called to update passwordDB.txt.
     * If the action is "Create" password, a BufferedWriter is invoked to write the account details
     * (using formatPasswordEntry method) and a new line to passwordDB.txt.
     * 
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
     * 
     * @param pe
     * @return formatted password and associated metadata.
     */
    private String formatPasswordEntry(PasswordEntry pe) {
        return pe.getAccount() + COLUMN_DELIMITER + pe.getCategory() + COLUMN_DELIMITER
                + pe.getPasswordString() + COLUMN_DELIMITER + (pe.isAutoEnabled() ? "YES" : "NO");
    }

    /**
     * Using Trie data structure to efficiently search for accounts by prefix.
     */
    public void searchPrefix() {
        System.out.print("Enter Account Name / URL PREFIX (e.g. first 3 characters): ");
        PasswordUI.input.nextLine();
        String prefix = PasswordUI.input.nextLine().trim();
        List<PasswordEntry> matches = trie.searchByPrefix(prefix);

        if (matches.isEmpty()) {
            System.out.println("No matching accounts found for the given prefix.");
        } else {
            System.out.println("Matching Accounts");
            for (PasswordEntry entry : matches) {
                System.out.println(entry.buildAccountDetails(pwSec));
            }
        }
        PasswordUI.mainMenu();
    }

    /**
     * Simulate Auto-Fill is a new feature in PassMe v2.0. When the user searches for their account,
     * the method checks for the PasswordEntry's autoEnabled boolean field. If true, the account
     * details are automatically displayed. If false, the user has to enter the password manually
     * and will be shown whether the entered password is correct.
     */
    public void simulateAutoFill() {
        System.out.print("Enter Account Name / URL: ");
        PasswordUI.input.nextLine();
        String account = PasswordUI.input.nextLine().trim();
        PasswordEntry pe = exists(account);

        if (pe == null) {
            System.out.println("Account not found!");
        } else if (pe.isAutoEnabled()) {
            System.out.println("Auto-fill enabled - access granted.");
            System.out.println(pe.buildAccountDetails(pwSec));
        } else {
            System.out.print("Auto-fill not enabled. Please enter your password manually: ");
            String passwordInput = PasswordUI.input.nextLine().trim();
            if (pwSec.decrypt(pe.getPassword()).equals(passwordInput)) {
                System.out.println("Password correct - access granted.");
                System.out.println(pe.buildAccountDetails(pwSec));
            } else {
                System.out.println("Password incorrect - access denied.");
            }
        }
        PasswordUI.mainMenu();
    }

    /**
     * Search by Category uses the PasswordTrie
     */
    public void searchByCategory() {
        Set<String> categorySet = new HashSet<>();
        for (PasswordEntry pe : passwords) {
            categorySet.add(pe.getCategory());
        }
        int count = 1;
        List<String> categories = new ArrayList<>(categorySet);
        System.out.println("Choose from the following categories:");
        for (String cat : categories) {
            System.out.println(count++ + ". " + cat);
        }
        while (true) {
            System.out.print("Enter Option: ");
            if (PasswordUI.input.hasNextInt()) {
                int categoryOption = PasswordUI.input.nextInt();
                if (categoryOption >= 1 && categoryOption <= count - 1) {
                    System.out.println();
                    String selectedCategory = categories.get(categoryOption - 1);
                    System.out.println("Accounts in Category " + selectedCategory + ": ");
                    for (PasswordEntry entry : passwords) {
                        if (entry.getCategory().equalsIgnoreCase(selectedCategory)) {
                            System.out.println(entry.buildAccountDetails(pwSec));
                        }
                    }
                    break;
                }
            }
            System.out.println("INVALID");
            PasswordUI.input.nextLine();
        }
        PasswordUI.mainMenu();
    }
}
