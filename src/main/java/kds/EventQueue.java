package kds;

import java.util.PriorityQueue;

/**
 * Created by clausvium on 21/12/15.
 */
public class EventQueue {
    public PriorityQueue<Event> queue;

    public EventQueue() {
        this.queue = new PriorityQueue<>();
    }
}
