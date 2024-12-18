package passmgr1;

public class PasswordEntry {

    // Declare instance variables for PasswordEntry
    private final String account;
    private final String category;
    private final PasswordHistory<String> passwordHistory;

    /**
     * Constructor method, setting account, category, and PasswordHistory equal to our provided
     * variables.
     * @param account
     * @param category
     * @param passwordHistory
     */
    public PasswordEntry(String account, String category, PasswordHistory<String> passwordHistory) {
        this.account = account;
        this.category = category;
        this.passwordHistory = passwordHistory;
    }

    /**
     * Get method for account
     * @return account name string
     */
    public String getAccount() {
        return account;
    }

    /**
     * Get method for account category
     * @return account category string
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get method for password, invoking the first method in PasswordHistory class
     * @return current password string
     */
    public String getPassword() {
        return passwordHistory.first();
    }

    /**
     * Set method for password, invoking the addFirst method in PasswordHistory class
     * @param password
     */
    public void setPassword(String password) {
        passwordHistory.addFirst(password);
    }

    /** 
     * Get method for password string, invoking the buildPasswordString method in PasswordHistory
     * class.
     */
    public String getPasswordString() {
        return passwordHistory.buildPasswordString();
    }

}
