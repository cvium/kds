package utils;

import ProGAL.dataStructures.Pair;
import ProGAL.dataStructures.Set;
import ProGAL.dataStructures.SortToolPoint2dAroundOrigo;
import ProGAL.dataStructures.SorterQuick;
import ProGAL.geom2d.Point;
import dcel.HalfEdge;
import kds.KDS;
import kds.KDSPoint;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by cvium on 03-12-2016.
 */
public class Helpers {
    /**
     * A rather inefficient helper to sort the list of points in ccw order, but I have no time to do it right.
     * It should only be used with 3 or so points.
     *
     * @param points list of points to be sorted in ccw order around centroid
     * @return list of points sorted in ccw order around their centroid
     */
    public static ArrayList<KDSPoint> sortCCW(ArrayList<KDSPoint> points) {
        Set<Point> pts = new Set<>();

        for (KDSPoint p : points) {
            pts.insert(p.getPoint());
        }
        SorterQuick sorter = new SorterQuick();
        sorter.Sort(pts, new SortToolPoint2dAroundOrigo());

        ArrayList<KDSPoint> sorted = new ArrayList<>();

        for (Point p : pts) {
            for (KDSPoint kds_p : points) {
                if (kds_p.getPoint() == p) sorted.add(kds_p);
            }
        }

        if (!isCCW(sorted.get(0), sorted.get(1), sorted.get(2))) {
            Collections.reverse(sorted);
        }

        if (!isCCW(sorted.get(0), sorted.get(1), sorted.get(2))) {
            throw new RuntimeException();
        }

        return sorted;
    }
    /* The following are some predicates */

    /**
     * Returns true if the line through a with angle (relative to horizontal axis) also goes through b
     *
     * @param a the point which the line goes through
     * @param b the query point
     * @param angle the angle between the line through a and the horizontal axis
     * @return true if a and b are on the same line with angle 'angle'
     */
    public static boolean onLine(KDSPoint a, KDSPoint b, double angle) {
        return Math.abs(Math.tan(angle) * (b.getX() - a.getX()) - b.getY() + a.getY()) < 1e-6;
    }

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
        //assert a != b && b != c;
        double edge1 = (b.getX() - a.getX()) * (b.getY() + a.getY());
        double edge2 = (c.getX() - b.getX()) * (c.getY() + b.getY());
        double edge3 = (a.getX() - c.getX()) * (a.getY() + c.getY());

        return edge1 + edge2 + edge3 < 1e-10;
    }

    /**
     * Returns a pair (CCW, CW) of edges incident to point wrt edge. Assumes edge.dest is point.
     *
     * @param edge
     * @param point
     * @return
     */
    public static Pair<HalfEdge, HalfEdge> getCCWAndCW(HalfEdge edge, KDSPoint point) {
        assert edge.getDestination() == point;

        HalfEdge prev = point.getIncidentEdge();
        HalfEdge next = point.getIncidentEdge().getPrev();
        if (next == null) {
            System.out.println("twin.next is null! Assuming the incident edge is unconnected.");
            return new Pair<>(prev, prev);
        }
        next = next.getTwin();

        HalfEdge ccw = prev, cw = prev;

        do {
            assert prev.getOrigin() == point.getIncidentEdge().getOrigin();
            assert next.getOrigin() == point.getIncidentEdge().getOrigin();

            // we're done when edge lies inbetween prev and next
//            if (isCCW(prev.getDestination(), edge.getOrigin(), next.getDestination())) {
//                ccw = prev;
//                cw = next;
//                break;
//            }
            // edge.org can either be left of prev or right of prev
            if (leftOf(prev.getOrigin(), prev.getDestination(), edge.getOrigin())) {
                // either next.dest is to the left of the line edge.dest -> edge.org
                // or it's to the right of prev.org -> prev.dest
                if (leftOf(edge.getDestination(), edge.getOrigin(), next.getDestination()) ||
                        rightOf(prev.getOrigin(), prev.getDestination(), next.getDestination())) {
                    ccw = prev;
                    cw = next;
                    break;
                }
            } else if (rightOf(prev.getOrigin(), prev.getDestination(), edge.getOrigin())) {
                if (rightOf(prev.getOrigin(), prev.getDestination(), next.getDestination()) &&
                        leftOf(edge.getDestination(), edge.getOrigin(), next.getDestination())) {
                    ccw = prev;
                    cw = next;
                    break;
                }
            }

            prev = next;
            next = next.getPrev().getTwin();
        } while (prev != point.getIncidentEdge() && next != null);

        return new Pair<>(ccw, cw);
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

    /**
     *
     * @param a origin
     * @param b destination
     * @param c query point
     * @return true if c is left of a->b
     */
    public static boolean leftOf(KDSPoint a, KDSPoint b, KDSPoint c) {
        return (b.getX() - a.getX())*(c.getY() - a.getY()) - (b.getY() - a.getY())*(c.getX() - a.getX()) > 0;
    }

    /**
     *
     * @param a origin
     * @param b destination
     * @param c query point
     * @return true if c is right of a->b
     */
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
        if (e.getTwin() != null && e.getTwin().getPrev() == null) return null;
        return e.getTwin().getPrev().getTwin();
    }

    public static void pointsToFile(ArrayList<KDSPoint> kps) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (KDSPoint kp : kps) {
            String init = "points.add(new KDSPoint(";
            String x = "new double[]{";
            String y = "new double[]{";
            for (int i = 0; i < kp.getCoeffsX().length; ++i) {
                x += kp.getCoeffsX()[i];
                if (i < kp.getCoeffsX().length-1) {
                    x += ",";
                } else {
                    x += "}";
                }
            }
            String mid = ",";
            for (int i = 0; i < kp.getCoeffsY().length; ++i) {
                y += kp.getCoeffsY()[i];
                if (i < kp.getCoeffsY().length-1) {
                    y += ",";
                } else {
                    y += "}";
                }
            }
            String end = "));";
            lines.add(init + x + mid + y + end);
        }
        Path file = Paths.get("coeffs.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    public static void pointsToFileGoogle(ArrayList<KDSPoint> kps) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (KDSPoint kp : kps) {
            String a = "";
            for (int i = 0; i < kp.getCoeffsX().length; ++i) {
                a += kp.getCoeffsX()[i] + "*x^" + i;
                if (i < kp.getCoeffsX().length - 1) {
                    a += "+";
                } else {
                    a += kp.getCoeffsX()[i];
                }
            }
            lines.add(a);
        }
        Path file = Paths.get("coeffs_google.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }
}
