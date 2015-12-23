package kds;

/**
 * Created by clausvium on 21/12/15.
 */
public class Certificate<EventType> {
    double failureTime;
    EventType event;
    boolean valid;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Certificate(EventType event) {
        this.event = event;
        this.valid = true;

    }

    public Certificate() {
        this.valid = true;
    }

    public void setFailureTime(double failureTime) {
        this.failureTime = failureTime;
    }
    public double getFailureTime() {
        return this.failureTime;
    }

    public EventType getEvent() {
        return this.event;
    }
    public void setEvent(EventType event) {
        this.event = event;
    }
}
