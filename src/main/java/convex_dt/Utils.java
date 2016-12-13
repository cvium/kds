package convex_dt;

import dcel.HalfEdge;
import kds.KDSPoint;

/**
 * Created by cvium on 03-12-2016.
 */
public class Utils {
    /* The following are some predicates */

    public static boolean isCCW(HalfEdge a, HalfEdge b) {
        if (a == null || b == null) {
            return false;
        }

        assert a.getDestination() == b.getOrigin();
        return isCCW(a.getOrigin(), a.getDestination(), b.getDestination());
    }

    public static boolean isCCW(KDSPoint a, KDSPoint b, KDSPoint c) {
        if (a == null || b == null || c == null) {
            return false;
        }
        assert a != b && b != c;
        double edge1 = (b.getX() - a.getX()) * (b.getY() + a.getY());
        double edge2 = (c.getX() - b.getX()) * (c.getY() + b.getY());
        double edge3 = (a.getX() - c.getX()) * (a.getY() + c.getY());

        return edge1 + edge2 + edge3 < 1e-10;
    }

    public static boolean lowerThan(KDSPoint a, KDSPoint b) {
        return a.getY() < b.getY();
    }

    public static boolean lessThan(KDSPoint a, KDSPoint b) {
        return a.compareTo(b) == -1;
    }

    public static boolean lowerThan(HalfEdge a, HalfEdge b) {
        KDSPoint al = a.getOrigin().getPoint(0).y() < a.getDestination().getPoint(0).y() ? a.getOrigin() : a.getDestination();
        KDSPoint bl = b.getOrigin().getPoint(0).y() < b.getDestination().getPoint(0).y() ? b.getOrigin() : b.getDestination();

        return al.getY() < bl.getY();
    }

    public static boolean onLine(KDSPoint a, KDSPoint b, KDSPoint c) {
        // If the determinant of the triangle containing these points is 0, they are collinear
        // aka. the triangle area test
        double part_1 = a.getX() * (b.getY() - c.getY());
        double part_2 = a.getY() * (b.getX() - c.getX());
        double part_3 = (b.getX() * c.getY() - b.getY() * c.getX());
        return Math.abs(part_1 - part_2 + part_3) <= 1e-10;
    }

    public static boolean leftOf(KDSPoint a, KDSPoint b, KDSPoint c) {
        return (b.getX() - a.getX())*(c.getY() - a.getY()) - (b.getY() - a.getY())*(c.getX() - a.getX()) > 0;
    }

    public static boolean rightOf(KDSPoint a, KDSPoint b, KDSPoint c) {
        return (b.getX() - a.getX())*(c.getY() - a.getY()) - (b.getY() - a.getY())*(c.getX() - a.getX()) < 0;
    }

    /* The following are some annoying helpers to find the relevant edges (C)CW from some edge E */

    public static HalfEdge oNext(HalfEdge e) {
        if (e.getPrev() == null) return null;
        return e.getPrev().getTwin();
    }

    public static HalfEdge oPrev(HalfEdge e) {
        if (e.getTwin().getNext() == null) return null;
        return e.getTwin().getNext();
    }

    public static HalfEdge dNext(HalfEdge e) {
        return e.getTwin().getPrev();
    }

    public static HalfEdge dPrev(HalfEdge e) {
        return e.getNext().getTwin();
    }

    public static HalfEdge lNext(HalfEdge e) {
        return e.getNext();
    }

    public static HalfEdge lPrev(HalfEdge e) {
        return e.getPrev();
    }

    public static HalfEdge rNext(HalfEdge e) {
        if (e.getTwin().getNext() == null) return null;
        return e.getTwin().getNext().getTwin();
    }

    public static HalfEdge rPrev(HalfEdge e) {
        if (e.getTwin().getPrev() == null) return null;
        return e.getTwin().getPrev().getTwin();
    }
}
