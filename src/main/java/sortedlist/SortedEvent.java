package sortedlist;

import kds.KDSPoint;

/**
 * Created by clausvium on 21/12/15.
 */
public class SortedEvent extends kds.Event<KDSPoint>{
    private SortedList sl;

    public SortedEvent(SortedList sl, double failureTime, KDSPoint a, KDSPoint b) {
        super(failureTime, a, b);
        this.sl = sl;
    }

    public SortedEvent(SortedList sl, KDSPoint a, KDSPoint b) {
        super(a, b);
        this.sl = sl;
    }

    public SortedEvent() {}

    @Override
    public void process(double t) {
        sl.update(this, t);
    }
}
