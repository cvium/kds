package kds;

/**
 * Created by clausvium on 22/12/15.
 */
public interface KDS<PointType, EventType extends Event<PointType>> {
    boolean audit(double t) throws Exception;
    void initialize();
    void update(EventType event, double t);
}
