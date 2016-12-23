package kds;


public abstract class Event<Primitive> implements Comparable<Event> {
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

    Primitive a;
    Primitive b;
    Primitive c;
    Primitive d;

    public Primitive getA() {
        return a;
    }

    public void setA(Primitive a) {
        this.a = a;
    }

    public Primitive getB() {
        return b;
    }

    public void setB(Primitive b) {
        this.b = b;
    }

    public Primitive getC() {
        return c;
    }

    public void setC(Primitive c) {
        this.c = c;
    }

    public Primitive getD() {
        return d;
    }

    public void setD(Primitive d) {
        this.d = d;
    }

    public abstract void process(double t);

    public Event(Primitive a, Primitive b) {
        this.a = a;
        this.b = b;
    }

    public Event(double failureTime, Primitive a, Primitive b) {
        this.a = a;
        this.b = b;
        this.failureTime = failureTime;
    }

    public Event() {}

    public abstract void computeFailureTime(double t, Primitive...primitives);

    //public abstract Event createEvent();

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