package passmgr2;

public class PasswordEntry {

    // Declare instance variables for PasswordEntry
    private final String account;
    private final String category;
    private final PasswordHistory<String> passwordHistory;
    private boolean autoEnabled;

    /**
     * Constructor method, setting account, category, and PasswordHistory equal to our provided
     * variables.
     * 
     * @param account
     * @param category
     * @param passwordHistory
     * @param autoEnabled
     */
    public PasswordEntry(String account, String category, PasswordHistory<String> passwordHistory,
            boolean autoEnabled) {
        this.account = account;
        this.category = category;
        this.passwordHistory = passwordHistory;
        this.autoEnabled = autoEnabled;
    }

    /**
     * Get method for account
     * 
     * @return account name string
     */
    public String getAccount() {
        return account;
    }

    /**
     * Get method for account category
     * 
     * @return account category string
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get method for password, invoking the first method in PasswordHistory class
     * 
     * @return current password string
     */
    public String getPassword() {
        return passwordHistory.first();
    }

    /**
     * Get method for auto-fill enabled
     */
    public boolean isAutoEnabled() {
        return autoEnabled;
    }

    /**
     * Get method for PasswordHistory
     */
    public PasswordHistory<String> getPasswordHistory() {
        return passwordHistory;
    }

    /**
     * Set method for password, invoking the addFirst method in PasswordHistory class
     * 
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

    public String buildAccountDetails(PasswordSecurity p) {
        return "Account: " + getAccount() + " | Category: " + getCategory() + " | Password: "
                + p.decrypt(getPassword());
    }

}
