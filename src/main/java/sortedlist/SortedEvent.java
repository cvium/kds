package sortedlist;

import kds.solvers.EigenSolver;
import utils.Primitive;

/**
 * Created by clausvium on 21/12/15.
 */
public class SortedEvent<P extends Primitive> extends kds.Event<P>{
    private SortedList<P> sl;

    public SortedEvent(SortedList<P> sl, double failureTime, P a, P b) {
        super(failureTime, a, b);
        this.sl = sl;
    }

    public SortedEvent(SortedList<P> sl, P a, P b) {
        super(a, b);
        this.sl = sl;
    }

    public SortedEvent() {}

    @Override
    public void computeFailureTime(EigenSolver solver, double t) {

    }


    @Override
    public void process(double t) {
        sl.process(this, t);
    }
}
