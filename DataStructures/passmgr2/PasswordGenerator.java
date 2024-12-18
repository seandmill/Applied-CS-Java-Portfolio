package passmgr2;

import java.util.Random;

public class PasswordGenerator {
    // Define private fields
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";

    /**
     * Generate Password method initializes a Password Genereation Queue and queues new password
     * characters. These characters are picked at random by using the nextInt method of the Random
     * class and applying to the allCharacters string using the charAt method.
     * 
     * @param length
     * @return random password string
     */
    public static String generatePassword(int length) {
        String allCharacters = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;
        PasswordGenerationQueue<Character> queue = new PasswordGenerationQueue<>(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            queue.enqueue(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }
        // Initiate the shuffle method for extra layer of randomization
        queue.shuffle();

        // Use a String Builder for efficient append operation
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(queue.dequeue());
        }

        return password.toString();
    }

}
