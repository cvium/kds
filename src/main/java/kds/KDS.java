package kds;

import java.util.ArrayList;

/**
 * Created by clausvium on 22/12/15.
 */
public interface KDS<PointType, EventType extends Event<PointType>> {
    boolean audit(double t);
    void initialize(double starttime);
    void update(EventType event, double t);
    EventQueue<EventType> getEventQueue();
    ArrayList<PointType> getPrimitives();
}
