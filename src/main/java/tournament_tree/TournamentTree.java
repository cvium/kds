package tournament_tree;

import kds.Event;
import kds.EventQueue;
import kds.KDS;
import kds.KDSPoint;

import java.util.ArrayList;

/**
 * Created by clausvium on 22/12/16.
 */
public class TournamentTree<Primitive, EventType extends Event<Primitive>> implements KDS<Primitive, EventType> {
    @Override
    public boolean audit(double t) {
        return false;
    }

    @Override
    public void initialize(double starttime) {

    }

    @Override
    public void update(EventType event, double t) {

    }

    @Override
    public EventQueue<EventType> getEventQueue() {
        return null;
    }

    public ArrayList<Primitive> getPrimitives() {
        return null;
    }
}
