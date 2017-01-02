package kds;

import utils.Primitive;

import java.util.ArrayList;

/**
 * Created by clausvium on 22/12/15.
 */
public interface KDS<PointType extends Primitive, EventType extends Event<PointType>> {
    boolean audit(double t);
    void initialize(double starttime);
    void initialize(double starttime, ArrayList<PointType> ps);
    void process(EventType event, double t);
    EventQueue<EventType> getEventQueue();
    ArrayList<PointType> getPrimitives();
}
