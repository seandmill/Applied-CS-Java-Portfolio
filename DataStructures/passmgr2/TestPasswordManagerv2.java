package passmgr2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPasswordManagerv2 {

    // Create several PasswordHistory objects to test
    PasswordHistory<String> pw1 = new PasswordHistory<>();
    PasswordHistory<String> pw2 = new PasswordHistory<>();
    PasswordHistory<String> pw3 = new PasswordHistory<>();
    PasswordHistory<String> pwEmpty = new PasswordHistory<>();


    // Create PasswordSecurity objects to test encrypt and decrypt methods
    PasswordSecurity PSShiftRight = new PasswordSecurity(3);
    PasswordSecurity PSShiftLeft = new PasswordSecurity(-5);

    // Create PasswordManager objects
    private ByteArrayOutputStream outputStream;

    // Run the initial setup before running test cases
    @Before
    public void setUp() throws Exception {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        pw1.addFirst("password1");
        pw1.addFirst("password2");
        pw1.addFirst("password3");
        pw1.addFirst("password4");
        pw1.addFirst("password5");
        pw1.addFirst("password6");
        pw1.addFirst("password7");

        pw2.addFirst("0vals123");
        pw2.addFirst("sq1ares987");
        pw2.addFirst("rect@ngl3!s");

        pw3.addFirst("!?#*%&$");
        pw3.addFirst("1876 !!! AZYX");

    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        System.setIn(System.in);
    }

    // The first() method of PasswordHistory should return, but not remove, the head element
    @Test
    public final void testFirst() {
        assertEquals("password7", pw1.first());
        assertEquals("password7", pw1.first());
    }

    // addFirst() should set the new object (string) as the first node
    @Test
    public final void testAddFirst() {
        pw1.addFirst("password8");
        assertEquals("password8", pw1.first());
        assertFalse(pw1.equals(pw2));
    }

    // The size() method should return no greater than 5 objects in the PasswordHistory list
    @Test
    public final void testSize() {
        assertTrue(pw1.size() <= 5);
        assertTrue(pw2.size() <= 5);
        assertTrue(pw3.size() == 2);
    }

    // Test contains() method in PasswordHistory class
    @Test
    public final void testContains() {
        assertTrue(pw1.contains("password5"));
        assertTrue(pw2.contains("sq1ares987"));
        assertFalse(pw3.contains("MISSING PASSWORD"));
    }

    // Test removeLast() method in PasswordHistory class
    @Test
    public final void testRemoveLast() {
        pw2.removeLast();
        assertFalse(pw2.contains("0vals123"));
    }

    // Test handling empty pwHistory
    @Test
    public final void testEmptyPH() {
        assertEquals(null, pwEmpty.first());
        assertEquals(null, pwEmpty.removeLast());
    }

    // The encrypt() method should shift alphanumeric values by specified shift value
    @Test
    public final void testEncrypt() {
        String passA = PSShiftRight.encrypt(pw2.first());
        String passB = PSShiftLeft.encrypt(pw2.first());
        assertEquals("uhfw@qjo6!v", passA);
        assertEquals("mzxo@ibg8!n", passB);
    }

    // The decrypt() method should shift alphanumeric values back to specified shift
    @Test
    public final void testDecrypt() {
        String passA = PSShiftRight.decrypt("uhfw@qjo6!v");
        String passB = PSShiftLeft.decrypt("mzxo@ibg8!n");
        assertEquals("rect@ngl3!s", passA);
        assertEquals("rect@ngl3!s", passB);
    }

    // Test encrypt() and then decrypt() for both shift left and shift right
    @Test
    public final void testEncryptThenDecrypt() {
        String original = "TestPassword123!";
        String encrypt = PSShiftRight.encrypt(original);
        String decrypt = PSShiftRight.decrypt(encrypt);
        assertEquals(original, decrypt);

        encrypt = PSShiftLeft.encrypt(original);
        decrypt = PSShiftLeft.decrypt(encrypt);
        assertEquals(original, decrypt);
    }

    // Test encrypt() for special characters (no modifications)
    @Test
    public void testEncryptSpecial() {
        String special = "#*(%&@(&$(*$)))!!@";
        String encrypt = PSShiftRight.encrypt(special);
        assertEquals(special, encrypt);
    }

    // Test PasswordEntry methods - get and set
    @Test
    public final void testPasswordEntry() {
        PasswordEntry pe = new PasswordEntry("TEST ACCOUNT", "TEST CATEGORY", pw3, equals("YES"));
        assertEquals(pe.getAccount(), "TEST ACCOUNT");
        assertEquals(pe.getCategory(), "TEST CATEGORY");
        assertEquals(pe.getPassword(), "1876 !!! AZYX");
        pe.setPassword("NEW PASSWORD");
        assertEquals(pe.getPassword(), "NEW PASSWORD");
    }

    /**
     * Test creating new PasswordEntry objects with existing PasswordHistory objects. Test creating
     * new ArrayList<PasswordEntry> and adding objects to list. Test ArrayList contains these
     * PasswordEntry objects. Test get methods for PasswordEntry, retrieved from ArrayList.
     */
    @Test
    public final void testPasswordsList() {
        List<PasswordEntry> passwords = new ArrayList<>();
        PasswordEntry testAccount1 =
                new PasswordEntry("TEST ACCOUNT 1", "Home", pw1, equals("YES"));
        PasswordEntry testAccount2 =
                new PasswordEntry("TEST ACCOUNT 2", "Banking", pw2, equals("YES"));
        PasswordEntry testAccount3 =
                new PasswordEntry("TEST ACCOUNT 3", "Sports", pw3, equals("YES"));
        PasswordEntry testAccount4 =
                new PasswordEntry("TEST ACCOUNT 4", "Productivity", pwEmpty, equals("YES"));

        passwords.add(testAccount1);
        passwords.add(testAccount2);
        passwords.add(testAccount3);
        passwords.add(testAccount4);

        assertTrue(passwords.contains(testAccount1));
        assertTrue(passwords.contains(testAccount2));
        assertTrue(passwords.contains(testAccount3));
        assertTrue(passwords.contains(testAccount4));

        assertFalse(passwords
                .contains(new PasswordEntry("", "", new PasswordHistory<>(), equals("YES"))));

        assertEquals(passwords.get(0).getAccount(), "TEST ACCOUNT 1");
        assertEquals(passwords.get(3).getCategory(), "Productivity");
        assertEquals(passwords.get(1).getPassword(), "rect@ngl3!s");
        assertEquals(passwords.get(3).getPassword(), null);
    }

    // Test writing password version history string (used in passwordDB)
    @Test
    public void testGetPasswordString() {
        PasswordEntry pe = new PasswordEntry("TEST ACCOUNT 1", "Business", pw3, equals("YES"));
        String passwordString = "1876 !!! AZYX ///// !?#*%&$";
        assertEquals(pe.getPasswordString(), passwordString);
    }

    // Test overriden equals method for PasswordHistory DoublyLinkedList
    @Test
    public void testPHEquals() {
        assertFalse(pw1.equals(null));
        assertTrue(pw1.equals(pw1));
        assertFalse(pw1.equals("Not PasswordHistory"));
        assertFalse(pw1.equals(pw2));

        PasswordHistory<String> pw1Copy = new PasswordHistory<>();
        pw1Copy.addFirst("password3");
        pw1Copy.addFirst("password4");
        pw1Copy.addFirst("password5");
        pw1Copy.addFirst("password6");
        pw1Copy.addFirst("password7");

        assertTrue(pw1.equals(pw1Copy));

        pw1Copy.addFirst(pw1Copy.removeLast());

        assertFalse(pw1.equals(pw1Copy));
    }

    // Test the PasswordUI and PasswordManager methods
    @Test
    public void testPasswordUI() throws IOException {

        // Simulate inputs for menu selection, search option, and exit option
        String simulatedInput = "EXCL\n1\nNEWACC456\nTESTCATEGORY\n1\n1\n4\nNEWACC456\n"
                + "1\n1\nNEWACC456\nTESTCATEGORY\n2\nPWD\n8\nBLAH\n2\n"
                + "5\nTHISDOESNOTEXIST\n5\nNEW\n6\nNOCAT\n1\n"
                + "1\nNEWACC789\nTESTCATEGORY\n1\n1\n7\nNEWACC789\n"
                + "7\nNEWACC456\nPWD\n3\nNEWACC456\n1\n3\nNEWACC456\n18\nBLAH\n2\nPWD!!\n"
                + "3\nNEWACC456\n3\n18\nBLAH\n1\n4\nNEWACC789\n8\nBLAH\n2\n2\nNEWACC456\n2\nNEWACC123456\n"
                + "1\nNEWACC456\n7\nNEWACC123456\n7\nNEWACC456\nWRONGPWD\n"
                + "4\nNEWACC789\n1\n4\nNEWACC456\n1\n8\n"; // Termination line
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Run the main method
        PasswordUI.main(null);

        // Verify expected output strings
        String output = outputStream.toString().trim();
        assertTrue(output.contains("----------------MAIN MENU----------------"));
        assertTrue(output.contains("1. (ENHANCED!) Create new password"));
        assertTrue(output.contains("2. Search existing password by account / URL"));
        assertTrue(output.contains("3. (ENHANCED!) Update existing password"));
        assertTrue(output.contains("4. Delete account & password"));
        assertTrue(output.contains("5. (NEW!) Search by account prefix"));
        assertTrue(output.contains("6. (NEW!) Search by category"));
        assertTrue(output.contains("7. (NEW!) Simulate login w/ auto-fill (browser feature)"));
        assertTrue(output.contains("8. Exit"));
        assertTrue(output.contains("Enter Option:"));
        assertTrue(output.contains("INVALID"));
        assertTrue(output.contains("Enter Account Name / URL:"));
        assertTrue(output.contains("Enter Category:"));
        assertTrue(output.contains("Please choose from the following options:"));
        assertTrue(output.contains("1. Generate a strong password automatically"));
        assertTrue(output.contains("2. Enter a password manually"));
        assertTrue(output.contains("Your new password is:"));
        assertTrue(output.contains("Enable Auto-Fill for this account?"));
        assertTrue(output.contains("1. Yes"));
        assertTrue(output.contains("2. No"));
        assertTrue(output.contains("Password created successfully!"));
        assertTrue(output.contains("Enter a new password:"));
        assertTrue(output.contains("Enter Account Name / URL PREFIX (e.g. first 3 characters)"));
        assertTrue(output.contains("No matching accounts found for the given prefix."));
        assertTrue(output.contains("Matching Accounts"));
        assertTrue(output.contains("Accounts in Category"));
        assertTrue(output.contains("Account: NEW"));
        assertTrue(output.contains("Account not found!"));
        assertTrue(output.contains("Password already exists for this account."));
        assertTrue(output.contains("Password incorrect - access denied."));

        // Instantiate PasswordUI
        PasswordUI ui = new PasswordUI();
        assertNotNull(ui);

    }

    @Test
    public void testPasswordGenerator() {
        PasswordGenerator pg = new PasswordGenerator();

        assertNotNull(pg);

        int small = 4, medium = 10, large = 40;

        assertNotNull(PasswordGenerator.generatePassword(small));
        assertNotNull(PasswordGenerator.generatePassword(medium));
        assertNotNull(PasswordGenerator.generatePassword(large));
    }

    @Test
    public void testPasswordTrie() {
        PasswordTrie<String> passwordTrie = new PasswordTrie<>();

        PasswordEntry entry = new PasswordEntry("Amazon", "Shopping", pw1, false);

        passwordTrie.insert("Amazon", entry);

        List<PasswordEntry> search = passwordTrie.searchByPrefix("Amazon");

        assertFalse(search.isEmpty()); // There should be at least one matching entry
        assertEquals(1, search.size()); // There should be one result
        assertEquals("Amazon", search.get(0).getAccount()); // The account name should be 'Amazon'
    }

}
