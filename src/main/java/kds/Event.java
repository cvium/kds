package kds;


public abstract class Event<PointType> implements Comparable<Event> {
    Certificate cert;

    public abstract void process(double t);
    public void setCertificate(Certificate cert) {
        this.cert = cert;
    }

    public Certificate getCertificate() {
        return this.cert;
    }

    public Event(Certificate cert) {
        this.cert = cert;
    }

    public Event() {}

    @Override
    public int compareTo(Event other) {
        double me = this.getCertificate().getFailureTime();
        double him = other.getCertificate().getFailureTime();
        if (Math.abs(me - him) <= 1e-10) {
            return 0;
        } else {
            return me < him ? -1 : 1;
        }
    }
}