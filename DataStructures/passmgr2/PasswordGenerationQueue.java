package passmgr2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerationQueue<E> {
    private E[] data;
    private int front = 0;
    private int size = 0;

    // Initialize the Queue at specified capacity
    @SuppressWarnings("unchecked")
    public PasswordGenerationQueue(int capacity) {
        data = (E[]) new Object[capacity];
    }

    // Returns if the queue is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Adds new data to the queue
    public void enqueue(E e) throws IllegalStateException {
        if (size == data.length)
            throw new IllegalStateException("Queue is full");
        int avail = (front + size) % data.length;
        data[avail] = e;
        size++;
    }

    // removes data from the queue
    public E dequeue() {
        if (isEmpty())
            return null;
        E answer = data[front];
        data[front] = null;
        front = (front + 1) % data.length;
        size--;
        return answer;
    }

    /**
     * Provides an extra layer of randomization for the queue by dequeueing into an ArrayList, using
     * the sort method of the Collections class, and adding back to the queue.
     */
    public void shuffle() {
        List<E> list = new ArrayList<>();

        while (!isEmpty()) {
            list.add(dequeue());
        }

        Collections.shuffle(list);

        for (E e : list) {
            enqueue(e);
        }
    }
}
