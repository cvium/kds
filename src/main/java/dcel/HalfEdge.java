package dcel;

import ProGAL.geom2d.LineSegment;
import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;

import java.awt.*;

/**
 * Created by cvium on 29-11-2016.
 */
public class HalfEdge {
    private KDSPoint origin;
    private KDSPoint destination;
    private Face face;
    private HalfEdge prev;
    private HalfEdge next;
    private HalfEdge twin;
    private boolean isBridge;
    private LineSegment lineSegment;

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
        //assert prev.getDestination() == this.origin;
        return prev;
    }

    public void setPrev(HalfEdge prev) {
        // prev half-edge must have this.origin as its destination
        //assert prev.getDestination() == this.origin;
        this.prev = prev;
    }

    public HalfEdge getNext() {
        // next half-edge must have this.destination as its origin
        //assert next.getOrigin() == this.destination;
        return next;
    }

    public void setNext(HalfEdge next) {
        // next half-edge must have this.destination as its origin
        assert next.getOrigin() == this.destination;
        this.next = next;
    }

    public HalfEdge getTwin() {
        return twin;
    }
    public void setTwin(HalfEdge twin) {
        this.twin = twin;
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

    // some bookkeeping for convex dt

    public boolean isBridge() {
        return isBridge;
    }

    public void markBridge() {
        isBridge = true;
    }

//    public HalfEdge connect(HalfEdge other) {
//        // TODO: does this work?
//        HalfEdge newEdge = new HalfEdge(destination, other.getOrigin());
//        next = newEdge;
//        other.setPrev(newEdge);
//        newEdge.setNext(other);
//        newEdge.setPrev(this);
//        // hackish way to create the twin
//        newEdge.getTwin();
//        return newEdge;
//    }

    public void draw(J2DScene scene, double t, Color c) {
        if (lineSegment != null)
            scene.removeShape(lineSegment);
        lineSegment = new LineSegment(origin.getPoint(t), destination.getPoint(t));
        scene.addShape(lineSegment, c);
        origin.draw(scene, t, java.awt.Color.RED);
        scene.repaint();
//        if (next != null)
//            next.draw(scene, 0, false);
//        if (prev != null)
//            prev.draw(scene, 0, false);
    }

    public void draw(J2DScene scene, double t, boolean next) {
        if (lineSegment != null)
            scene.removeShape(lineSegment);
        lineSegment = new LineSegment(origin.getPoint(0), destination.getPoint(0));
        Color color = java.awt.Color.GREEN;
        scene.addShape(lineSegment, color);
    }
}
