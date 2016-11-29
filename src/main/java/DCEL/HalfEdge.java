package DCEL;

import kds.KDSPoint;

/**
 * Created by cvium on 29-11-2016.
 */
public class HalfEdge {
    private KDSPoint origin;
    private KDSPoint destination;
    private Face face;
    private HalfEdge prev;
    private HalfEdge next;

    public HalfEdge() {

    }

    public HalfEdge(KDSPoint origin, KDSPoint destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public HalfEdge(KDSPoint origin, KDSPoint destination, Face face) {
        this.origin = origin;
        this.destination = destination;
        this.face = face;
    }

    public HalfEdge(KDSPoint origin, KDSPoint destination, Face face, HalfEdge prev, HalfEdge next) {
        this.origin = origin;
        this.destination = destination;
        this.face = face;
        this.prev = prev;
        this.next = next;
    }

    public KDSPoint getOrigin() {
        return origin;
    }

    public void setOrigin(KDSPoint origin) {
        this.origin = origin;
    }

    public KDSPoint getDestination() {
        return destination;
    }

    public void setDestination(KDSPoint destination) {
        this.destination = destination;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public HalfEdge getPrev() {
        // prev half-edge must have this.origin as its destination
        assert prev.getDestination() == this.origin;
        return prev;
    }

    public void setPrev(HalfEdge prev) {
        // prev half-edge must have this.origin as its destination
        assert prev.getDestination() == this.origin;
        this.prev = prev;
    }

    public HalfEdge getNext() {
        // next half-edge must have this.destination as its origin
        assert next.getOrigin() == this.destination;
        return next;
    }

    public void setNext(HalfEdge next) {
        // next half-edge must have this.destination as its origin
        assert next.getOrigin() == this.destination;
        this.next = next;
    }

    public HalfEdge twin() {
        return new HalfEdge(destination, origin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HalfEdge)) return false;

        HalfEdge halfEdge = (HalfEdge) o;

        if (origin != null ? !origin.equals(halfEdge.origin) : halfEdge.origin != null) return false;
        return destination != null ? destination.equals(halfEdge.destination) : halfEdge.destination == null;
    }

    @Override
    public int hashCode() {
        int result = origin != null ? origin.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }
}
