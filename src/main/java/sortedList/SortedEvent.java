package sortedList;

import kds.Certificate;

/**
 * Created by clausvium on 21/12/15.
 */
public class SortedEvent<PointType> extends kds.Event{
    private Certificate<SortedEvent> cert;
    private SortedList sl;
    private PointType a;
    private PointType b;

    public PointType getA() {
        return a;
    }

    public void setA(PointType a) {
        this.a = a;
    }

    public PointType getB() {
        return b;
    }

    public void setB(PointType b) {
        this.b = b;
    }

    public SortedEvent(Certificate<SortedEvent> cert, SortedList sl, PointType a, PointType b) {
        super(cert);
        this.cert = cert;
        cert.setEvent(this);
        this.sl = sl;
        this.a = a;
        this.b = b;
    }

    public SortedEvent() {}

    @Override
    public void process(double t) {
        sl.update(this, t);
    }
}
