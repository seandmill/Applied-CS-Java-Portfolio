package passmgr1;

public class PasswordHistory<E> {
    // ---------------- nested Node class ----------------
    public static class Node<E> {
        private E element; // reference to the element stored at this node
        private Node<E> prev; // reference to the previous node in the list
        private Node<E> next; // reference to the subsequent node in the list

        public Node(E e, Node<E> p, Node<E> n) {
            element = e;
            prev = p;
            next = n;
        }

        public E getElement() {
            return element;
        }

        public Node<E> getPrev() {
            return prev;
        }

        public Node<E> getNext() {
            return next;
        }

        public void setPrev(Node<E> p) {
            prev = p;
        }

        public void setNext(Node<E> n) {
            next = n;
        }
    } // ----------- end of nested Node class -----------

    // instance variables of the DoublyLinkedList
    private Node<E> head; // head sentinel
    private Node<E> tail; // tail sentinel
    private int size = 0; // number of elements in the list

    /** Constructs a new empty list. */
    public PasswordHistory() {
        head = new Node<>(null, null, null); // create head
        tail = new Node<>(null, head, null); // tail is preceded by head
        head.setNext(tail); // head is followed by tail
    }

    // public accessor methods
    /** Returns the number of elements in the linked list. */
    public int size() {
        return size;
    }

    /** Tests whether the linked list is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns (but does not remove) the first element of the list. */
    public E first() {
        if (isEmpty())
            return null;
        return head.getNext().getElement(); // first element is beyond head
    }

    // public update methods
    /** Adds an element to the front of the list. */
    public void addFirst(E e) {
        if(size == 5) removeLast();
        addBetween(e, head, head.getNext()); // place just after the head
    }

    /** Removes and returns the last element of the list. */
    public E removeLast() {
        if (isEmpty())
            return null; // nothing to remove
        return remove(tail.getPrev()); // last element is before tail
    }

    /** Adds an element to the linked list in between the given nodes. */
    private void addBetween(E e, Node<E> p, Node<E> s) {
        Node<E> newest = new Node<>(e, p, s); // new node is linked
        p.setNext(newest);
        s.setPrev(newest);
        size++;
    }

    /** Removes the given node from the list and returns its element. */
    private E remove(Node<E> node) {
        Node<E> predecessor = node.getPrev();
        Node<E> successor = node.getNext();
        predecessor.setNext(successor);
        successor.setPrev(predecessor);
        size--;
        return node.getElement();
    }

    /**
     * Builds the password version history string to be stored in passwordDB.txt
     * @return password history string
     */
    public String buildPasswordString() {
        if(isEmpty()) return null;
        Node<E> start = head.getNext();
        String passwordString = "";
        while(start.getElement() != null) {
            if(start.getNext().getElement() == null)
                passwordString += start.getElement();
            else
                passwordString += start.getElement() + " ///// ";
            start = start.getNext();
        }
        return passwordString;
    }

    /**
     * Creates a contains method for our DoublyLinkedList. Walks through each node, searching
     * for object o.
     * @param o
     * @return true if object is contained in list, else false.
     */
    public boolean contains(Object o) {
        Node<E> start = head.getNext();
        while(start.getElement() != null) {
            if(start.getElement().equals(o)) return true;
        start = start.getNext();
        }
        return false;
    }

    /** Overrides the equals method for equivalence testing. */
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (!(o instanceof PasswordHistory))
            return false;
        PasswordHistory<?> other = (PasswordHistory<?>) o;
        if (size != other.size)
            return false;
        Node<E> walkA = head.getNext();
        Node<?> walkB = other.head.getNext();
        while (walkA != tail) {
            if (!walkA.getElement().equals(walkB.getElement()))
                return false;
            walkA = walkA.getNext();
            walkB = walkB.getNext();
        }
        return true;
    }

}
