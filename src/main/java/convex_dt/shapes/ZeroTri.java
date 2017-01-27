package convex_dt.shapes;

import kds.KDSPoint;
import utils.Helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

/**
 * Created by cvium on 15-12-2016.
 */
public class ZeroTri implements ConvexShape {
    /**
     * Predicate to test whether point d lies in the smallest circle containing a, b and c on its boundary.
     *
     * @param a
     * @param b
     * @param c
     * @param d query point
     * @return circleEnum
     */
    @Override
    public circleEnum inCircle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
        KDSPoint first, second, third; // the three corners of the triangle

        // points sorted on their lexicographical order
        ArrayList<KDSPoint> points = new ArrayList<>();
        points.add(a);
        points.add(b);
        points.add(c);
        Collections.sort(points);

        // sorted on y
        ArrayList<KDSPoint> points_sorted_y = new ArrayList<>();
        points_sorted_y.add(a);
        points_sorted_y.add(b);
        points_sorted_y.add(c);
        points_sorted_y.sort(new Comparator<KDSPoint>() {
            @Override
            public int compare(KDSPoint o1, KDSPoint o2) {
                if (o1.getY() < o2.getY()) return -1;

                return Math.abs(o1.getY() - o2.getY()) < 1e-6 ? 0 : 1;
            }
        });

        // are they all on the vertical line?
        if (Helpers.onLine(a, b, 90.0) && Helpers.onLine(b, c, 90)) {
            // find the corners of the triangle
            second = Collections.max(points);
            third = Collections.min(points);
            first = findIntersection(second, 30, third, -30);
        }
        // or the 30 line or -30?
        else if ((Helpers.onLine(a, b, 30.0) && Helpers.onLine(b, c, 30)) ||
                (Helpers.onLine(a, b, -30.0) && Helpers.onLine(b, c, -30))) {
            // find the corners of the triangle
            first = Collections.min(points);
            second = Collections.max(points);

            third = findIntersection(first, 30, second, 90);
        }
        // the following checks for two points on the same 90 line
        else if (Math.abs(points.get(2).getX()-points.get(1).getX()) < 1e-6 ) {
            if ((isAbove(points.get(2), points.get(0), 30) || Helpers.onLine(points.get(2), points.get(0), 30))
                    && (!isAbove(points.get(1), points.get(0), -30) || Helpers.onLine(points.get(1), points.get(0), -30))) {
                first = points.get(0);
                second = findIntersection(first, 30, points.get(2), 90);
                third = findIntersection(first, -30, points.get(1), 90);
            } else {
                Logger.getGlobal().info("Not a valid circle");
                return circleEnum.INVALID;
            }
        }
//        // now we check the 30 and -30 line, since the logic for 90 cannot be applied to 30 and -30
//        else if (Helpers.onLine(points_sorted_y.get(2), points_sorted_y.get(1), 30)) {
//            // the third points _must_ lie on or to the right of the 90 line through the highest point
//            // and on or to the right of the -30 line through the second highest point
//            if (!rightOf(points_sorted_y.get(2), points_sorted_y.get(0)) || isAbove(points_sorted_y.get(1), points_sorted_y.get(0), -30)) {
//                Logger.getGlobal().info("Not a valid circle");
//                return circleEnum.INVALID;
//            } else {
//                third = points_sorted_y.get(0);
//
//                first = findIntersection(points_sorted_y.get(1), 30, third, -30);
//                second = findIntersection(points_sorted_y.get(2), 30, third, 90);
//            }
//        }
//        // -30 line
//        else if(Helpers.onLine(points_sorted_y.get(0), points_sorted_y.get(1), -30)) {
//            // the third points _must_ lie on or to the right of the 90 line through the lowest point
//            // and on or to the left of the 30 line through the second lowest point
//            if (rightOf(points_sorted_y.get(0), points_sorted_y.get(2)) || !isAbove(points_sorted_y.get(1), points_sorted_y.get(2), 30)) {
//                Logger.getGlobal().info("Not a valid circle");
//                return circleEnum.INVALID;
//            } else {
//                second = points_sorted_y.get(2);
//
//                first = findIntersection(points_sorted_y.get(1), -30, second, 30);
//                third = findIntersection(points_sorted_y.get(0), -30, second, 90);
//            }
//        }
        // if none of them are on a common 90 line, each point must be on an edge in the triangle
        else {
            // we start by grabbing the point with highest x coordinate, it must necessarily lie on the 90 line.
            // Call this point A.
            KDSPoint point_a = points.get(2);

            // next we grab the point with the lowest y coordinate, but we have to remove the point we just took
            // in case it's the lowest y. We call this point B.
            points_sorted_y.remove(point_a);
            KDSPoint point_b = points_sorted_y.get(0);

            // Point B _must_ lie on or below the -30 line through A
            if (isAbove(point_a, point_b, -30)) {
                Logger.getGlobal().info("Not a valid circle");
                return circleEnum.INVALID;
            }

            // here comes point C
            KDSPoint point_c = points_sorted_y.get(1);
            // two cases for the validity of the triangle. For both cases, C must lie on or above the -30 line through B

            // if B lies below the 30 line through A, then C must lie above the 30 line through A
            if (isBelow(point_b, point_c, -30) || (isBelow(point_a, point_b, 30) && isBelow(point_a, point_c, 30))) {
                Logger.getGlobal().info("Not a valid circle");
                return circleEnum.INVALID;
            }
            // otherwise, C must lie on or above the 30 line through B
            else if (isBelow(point_b, point_c, -30) || isBelow(point_b, point_c, 30)) {
                Logger.getGlobal().info("Not a valid circle");
                return circleEnum.INVALID;
            }

            // now to find where these fuckers intersect
            first = findIntersection(point_b, -30, point_c, 30);
            second = findIntersection(point_c, 30, point_a, 90);
            third = findIntersection(point_b, -30, point_a, 90);
        }

        if (inTriangle(first, second, third, d)) return circleEnum.INSIDE;
        else if (Helpers.onLine(first, d, 30) || Helpers.onLine(first, d, -30) || Helpers.onLine(second, d, 90))
            return circleEnum.ON;
        else return circleEnum.OUTSIDE;
    }
    
    public boolean inTriangle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
        double A = 1/2 * (-b.getY() * c.getX() + a.getY() * (-b.getX() + c.getX()) + a.getX() * (b.getY() - c.getY()) + b.getX() * c.getY());
        double sign = A < 0 ? -1 : 1;
        double s = (a.getY() * c.getX() - a.getX() * c.getY() + (c.getY() - a.getY()) * d.getX() + (a.getX() - c.getX()) * d.getY()) * sign;
        double t = (a.getX() * b.getY() - a.getY() * b.getX() + (a.getY() - b.getY()) * d.getX() + (b.getX() - a.getX()) * d.getY()) * sign;

        return s > 0 && t > 0 && (s + t) < 2 * A * sign;
    }

    /**
     *
     * @param a
     * @param a_angle the angle the line through a has with horizontal line
     * @param b
     * @param b_angle the angle the line through a has with horizontal line
     * @return
     */
    private KDSPoint findIntersection(KDSPoint a, double a_angle, KDSPoint b, double b_angle) {
        assert a_angle != b_angle;

        double x = (Math.tan(Math.toRadians(a_angle)) * a.getX() - Math.tan(Math.toRadians(b_angle)) * b.getX() + b.getY() - a.getY()) / (Math.tan(Math.toRadians(a_angle)) - Math.tan(Math.toRadians(b_angle)));
        double y = Math.tan(Math.toRadians(a_angle)) * (x - a.getX()) + a.getY();

        return new KDSPoint(new double[]{x}, new double[]{y});
    }

    /**
     *
     * @param a
     * @param b query point
     * @param angle
     * @return
     */
    private boolean isAbove(KDSPoint a, KDSPoint b, double angle) {
        double y = Math.tan(Math.toRadians(angle)) * (b.getX() - a.getX()) + a.getY();

        return b.getY() - y > 1e-6;
    }

    /**
     *
     * @param a
     * @param b query point
     * @param angle
     * @return
     */
    private boolean isBelow(KDSPoint a, KDSPoint b, double angle) {
        double y = Math.tan(Math.toRadians(angle)) * (b.getX() - a.getX()) + a.getY();

        return y - b.getY() > 1e-6;
    }

    /**
     * Specialized stupid function to determine if a point lies to the left or to the right of the directed half-line
     * through a with angle 90
     *
     * @param a
     * @param b query point
     * @return
     */
    private boolean rightOf(KDSPoint a, KDSPoint b) {
        return a.getX() < b.getX();
    }

    /**
     *
     * @param a
     * @param b query point
     * @return
     */
    private boolean leftOf(KDSPoint a, KDSPoint b) {
        return b.getX() < a.getX();
    }
    /**
     * Returns true if b is to the right of the line through a with 'angle' relative to horizontal axis
     *
     * @param a
     * @param b
     * @param angle
     * @return
     */
    private boolean rightOf(KDSPoint a, KDSPoint b, double angle) {
        // find another point on the line through 'a' with angle 'angle' that lies to the left of the vertical line
        // through a ie. smaller x coordinate
        double x = a.getX() - 5;
        double y = Math.tan(Math.toRadians(angle)) * (x - a.getX()) + a.getY();

        return !Helpers.isCCW(a, new KDSPoint(new double[]{x}, new double[]{y}), b);
    }

    private double getYCoordinate(KDSPoint p, double x, double angle) {
        return Math.tan(Math.toRadians(angle)) * (x - p.getX()) + p.getY();
    }

    private double getXCoordinate(KDSPoint p, double y, double angle) {
        return (y - Math.tan(Math.toRadians(angle)) * p.getX() - p.getY()) / Math.tan(Math.toRadians(angle));
    }
    /**
     * Predicate test to determine where point a lies wrt. to the infinite circle with b and c on the boundary.
     *
     * @param a The query point
     * @param b First point on the boundary of the infinite circle
     * @param c The second point on the boundary of the infinite circle
     * @return inside, outside, before, after
     */
    @Override
    public infCircleEnum inInfCircle(KDSPoint a, KDSPoint b, KDSPoint c) {
        // if standing in the corner and facing the triangle's interior, point b is always on the right edge
        // and point c on the left edge, since the infinite circle must touch b->c from the right

        double b_angle, c_angle;
        KDSPoint b_point, c_point;

        // the first two cases pertain to when at least one of b and c is on the vertical line
        if ((b.getX() > c.getX() && !isAbove(b, c, -30.0))
                || (b.getX() == c.getX() && b.getY() >= c.getY())) {
            b_angle = 90;
            c_angle = -30;
            b_point = new KDSPoint(new double[]{b.getX()}, new double[]{b.getY() + 5});
            c_point = new KDSPoint(new double[]{c.getX()-5}, new double[]{getYCoordinate(c, c.getX()-5, c_angle)});
        } else if ((c.getX() > b.getX() && !isBelow(c, b, 30.0))
                || (c.getY() <= b.getY())) {
            b_angle = 30;
            c_angle = 90;
            b_point = new KDSPoint(new double[]{b.getX()-5}, new double[]{getYCoordinate(b, b.getX()-5, b_angle)});
            c_point = new KDSPoint(new double[]{c.getX()}, new double[]{c.getY() - 5});
        } else {
            // neither of them can be on the vertical line. b is always on -30 and c on 30 then.
            b_angle = -30;
            c_angle = 30;
            b_point = new KDSPoint(new double[]{b.getX()+5}, new double[]{getYCoordinate(b, b.getX()+5, b_angle)});
            c_point = new KDSPoint(new double[]{c.getX()+5}, new double[]{getYCoordinate(c, c.getX()+5, c_angle)});
        }

        // the three cases for INSIDE, although the first case is split up
        if (b_angle == 90 && c_angle == -30 && !rightOf(b, a) && isAbove(c, a, -30)) {
            return infCircleEnum.INSIDE;
        } else if (c_angle == 90 && b_angle == 30 && !rightOf(c, a) && isBelow(b, a, 30)) {
            return infCircleEnum.INSIDE;
        } else if (b_angle != 90 && c_angle != 90 && isAbove(b, a, -30) && isBelow(c, a, 30)) {
            return infCircleEnum.INSIDE;
        } else if ((Helpers.onLine(b, b_point, a) || Helpers.onLine(c, c_point, a)) &&
                !Helpers.isCCW(b, c, a)) {
            return infCircleEnum.INSIDE;
        } else if ((b_angle == 90 || c_angle == 90) && Math.abs(b.getDistance(a) + a.getDistance(c) - b.getDistance(c)) <= 1e-6) {
            return infCircleEnum.INSIDE;
        }

        // OUTSIDE
        if (Helpers.isCCW(b, c, a)) {
            return infCircleEnum.OUTSIDE;
        }

        // BEFORE
        if (Helpers.onLine(a, b, c) && a.getDistance(c) > b.getDistance(c) && a.getDistance(b) < a.getDistance(c))
            return infCircleEnum.BEFORE;

        // AFTER
        if (Helpers.onLine(b, c, a) && a.getDistance(b) > c.getDistance(b) && a.getDistance(c) < a.getDistance(b))
            return infCircleEnum.BEFORE;


//        if (b.getY() >= c.getY()) {
//            // b.x == c.x is the same as them being on the 90 line
//            if (Helpers.onLine(b, c, -30.0) || b.getX() <= c.getX()) {
//                b_angle = 30;
//                c_angle = 90;
//            } else {
//                // in this case they are either on the same 30 line or not, doesn't really matter
//                b_angle = 90;
//                c_angle = -30;
//            }
//        } else {
//
//        }
//
//        if (Helpers.onLine(b, c, 30.0)) {
//            if (b.getY() > c.getY()) {
//                // b must be on 90 line and c on the -30 line
//                b_angle = 90;
//                c_angle = -30;
//            } else {
//                // b must be in the leftmost corner and c on the 30 line
//                b_angle = 30;
//                c_angle = 30;
//            }
//        } else if (Helpers.onLine(b, c, -30.0)) {
//            if (b.getY() > c.getY()) {
//                // b must be on 30 line and c on 90 line
//                b_angle = 30;
//                c_angle = 90;
//            } else {
//                // b must be in lower right corner (90 line) and c on -30 line
//                b_angle = -30;
//                c_angle = -30;
//            }
//        } else if (Helpers.onLine(b, c, 90.0)) {
//            if (b.getY() < c.getY()) {
//                // b must be on -30 line and c on 30 line
//                b_angle = -30;
//                c_angle = 30;
//            } else {
//                // they must both be on 90 line, but which corner?
//                b_angle = 30;
//                c_angle = 90;
//            }
//        } else {
//
//        }


        Logger.getGlobal().warning("ZERO TRI INF CIRCLE TEST FAILED!!!!!!");
        return infCircleEnum.INVALID;
    }
}
