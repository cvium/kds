package sortedlist;

import kds.Event;
import kds.KDSPoint;

/**
 * Created by clausvium on 21/12/15.
 */
public class SortedEvent<P> extends kds.Event<P>{
    private SortedList sl;

    public SortedEvent(SortedList sl, double failureTime, P a, P b) {
        super(failureTime, a, b);
        this.sl = sl;
    }

    public SortedEvent(SortedList sl, P a, P b) {
        super(a, b);
        this.sl = sl;
    }

    public SortedEvent() {}

    @Override
    public void computeFailureTime(double t, P[] ps) {

    }


    @Override
    public void process(double t) {
        sl.update(this, t);
    }
}
