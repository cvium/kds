package kds;


public abstract class Event<PointType> implements Comparable<Event> {
    double failureTime;
    boolean valid = true;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public double getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(double failureTime) {
        this.failureTime = failureTime;
    }

    PointType a;
    PointType b;

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

    public abstract void process(double t);

    public Event(PointType a, PointType b) {
        this.a = a;
        this.b = b;
    }

    public Event(double failureTime, PointType a, PointType b) {
        this.a = a;
        this.b = b;
        this.failureTime = failureTime;
    }

    public Event() {}

    @Override
    public int compareTo(Event other) {
        double me = this.failureTime;
        double him = other.failureTime;
        if (Math.abs(me - him) <= 1e-10) {
            return 0;
        } else {
            return me < him ? -1 : 1;
        }
    }
}