package kds;

import java.util.*;

/**
 * Created by clausvium on 21/12/15.
 */
public class EventQueue<EventType extends Event> {
    public TreeMap<Double, ArrayList<EventType>> queue;

    public EventQueue() {
        this.queue = new TreeMap<>();
    }

    public void add(EventType e) {
        double failureTime = e.getCertificate().getFailureTime();
        ArrayList<EventType> es = queue.get(failureTime);
        if (es != null) {
            es.add(e);
        } else {
            es = new ArrayList<>();
            es.add(e);
            queue.put(e.getCertificate().getFailureTime(), es);
        }
    }

    public void remove(EventType e) {
        double failureTime = e.getCertificate().getFailureTime();
        ArrayList<EventType> es = queue.get(failureTime);

        if (es != null) {
            es.remove(e);
        }
    }

    public ArrayList<EventType> peek(double t) throws NoSuchElementException {
        NavigableMap<Double, ArrayList<EventType>> entries = queue.headMap(t, true);
        if (entries != null) {
            ArrayList<EventType> res = new ArrayList<>();
            for (ArrayList<EventType> e : entries.values()) {
                res.addAll(e);
            }
            return res;
        } else {
            throw new NoSuchElementException();
        }
    }

    public ArrayList<EventType> peek() throws NoSuchElementException {
        Map.Entry<Double, ArrayList<EventType>> entry = queue.firstEntry();
        if (entry != null) {
            return entry.getValue();
        } else {
            throw new NoSuchElementException();
        }
    }
    public ArrayList<EventType> poll(double t) {
        ArrayList<EventType> res = new ArrayList<>();
        while (queue.firstEntry().getKey() <= t) {
            res.addAll(queue.pollFirstEntry().getValue());
        }
        return res;
    }

    public ArrayList<EventType> poll() {
        return queue.pollFirstEntry().getValue();
    }

    public double firstKey() {
        return queue.firstKey();
    }
}
