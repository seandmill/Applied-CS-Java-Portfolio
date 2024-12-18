package passmgr2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Password Trie utilizes a custom Trie implementation to store PasswordEntry objects by their
 * account name. This enables the user to efficiently search for a custom prefix, and return the
 * PasswordEntries whose account name matches the prefix.
 */
public class PasswordTrie<E> {
    // ---------------- nested Node class ----------------
    public static class Node<E> {
        Map<Character, Node<E>> children;
        boolean isEndOfWord;
        PasswordEntry entry;

        public Node() {
            children = new HashMap<>();
            isEndOfWord = false;
            entry = null;
        }
    }

    // Declare the root node
    private Node<E> root;

    // Constructor class for the Trie
    public PasswordTrie() {
        root = new Node<E>();
    }

    /**
     * The insert method of the trie uses the account name string, looping through each character of
     * the string and uses the putIfAbsent method to insert the character<>node pair only if the
     * character does not exist. Once the loop is complete, we set the node's isEndOfWord attribute
     * to true and the node's PasswordEntry object equal to the entry parameter.
     * 
     * @param account
     * @param entry
     */
    public void insert(String account, PasswordEntry entry) {
        Node<E> node = root;
        for (char ch : account.toLowerCase().toCharArray()) {
            node.children.putIfAbsent(ch, new Node<E>());
            node = node.children.get(ch);
        }
        node.isEndOfWord = true;
        node.entry = entry;
    }

    /**
     * The Search by Prefix method is the primary mechanism for the user functionality. This method
     * builds a list of matching PasswordEntry objects by looping through all of the characters in
     * the prefix String parameter.
     * 
     * @param prefix
     * @return
     */
    public List<PasswordEntry> searchByPrefix(String prefix) {
        Node<E> node = root;
        for (char ch : prefix.toLowerCase().toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return new ArrayList<>();
            }
            node = node.children.get(ch);
        }
        return collectAllEntries(node);
    }

    // Helper method to collect all PasswordEntries from a given Node
    private List<PasswordEntry> collectAllEntries(Node<E> node) {
        List<PasswordEntry> entries = new ArrayList<>();
        if (node.isEndOfWord && node.entry != null) {
            entries.add(node.entry);
        }
        for (Node<E> child : node.children.values()) {
            entries.addAll(collectAllEntries(child));
        }
        return entries;
    }

    // Recursive method to remove an account from the PasswordTrie
    public boolean delete(String account) {
        return delete(root, account.toLowerCase(), 0);
    }

    /**
     * When a password is deleted, the associated PasswordEntry and nodes must be deleted as well.
     * This is a recursive function that deletes nodes until it reaches the end of the word.
     * 
     * @param current
     * @param account
     * @param index
     * @return
     */
    public boolean delete(Node<E> current, String account, int index) {
        if (index == account.length()) {
            if (!current.isEndOfWord)
                return false;
            current.isEndOfWord = false;
            current.entry = null;

            return current.children.isEmpty();
        }
        char ch = account.charAt(index);
        Node<E> nextNode = current.children.get(ch);
        if (nextNode == null)
            return false;

        boolean deleteCurrentNode = delete(nextNode, account, index + 1);

        if (deleteCurrentNode) {
            current.children.remove(ch);
            return current.children.isEmpty() && !current.isEndOfWord;
        }
        return false;
    }
}
