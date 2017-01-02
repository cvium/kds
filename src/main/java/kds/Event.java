package kds;


import kds.solvers.EigenSolver;
import utils.Primitive;

public abstract class Event<P extends Primitive> implements Comparable<Event> {
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

    P a;
    P b;
    P c;
    P d;

    public P getA() {
        return a;
    }

    public void setA(P a) {
        this.a = a;
    }

    public P getB() {
        return b;
    }

    public void setB(P b) {
        this.b = b;
    }

    public P getC() {
        return c;
    }

    public void setC(P c) {
        this.c = c;
    }

    public P getD() {
        return d;
    }

    public void setD(P d) {
        this.d = d;
    }

    public abstract void process(double t);

    public Event(P a, P b) {
        this.a = a;
        this.b = b;
    }

    public Event(double failureTime, P a, P b) {
        this.a = a;
        this.b = b;
        this.failureTime = failureTime;
    }

    public Event() {}

    public abstract void computeFailureTime(EigenSolver solver, double t);

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