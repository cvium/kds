package convex_dt.shapes;

import ProGAL.geom2d.Line;
import ProGAL.geom2d.LineSegment;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;
import utils.Helpers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import static utils.Helpers.*;

/**
 * Created by cvium on 15-12-2016.
 */
public class ZeroTriShape implements ConvexShape {
    private J2DScene scene;

    public J2DScene getScene() {
        return scene;
    }

    @Override
    public void setScene(J2DScene scene) {
        this.scene = scene;
    }

    public ZeroTriShape() {}

    public ZeroTriShape(J2DScene scene) {
        this.scene = scene;
    }

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
        points_sorted_y.sort((o1, o2) -> {
            if (o1.getY() < o2.getY()) return -1;

            return Math.abs(o1.getY() - o2.getY()) < 1e-6 ? 0 : 1;
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

        LineSegment l1 = new LineSegment(first.getPoint(), second.getPoint());
        LineSegment l2 = new LineSegment(first.getPoint(), third.getPoint());
        LineSegment l3 = new LineSegment(second.getPoint(), third.getPoint());
        scene.addShape(l1, Color.BLACK);
        scene.addShape(l2, Color.BLACK);
        scene.addShape(l3, Color.BLACK);
        scene.repaint();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {}
        scene.removeShape(l1);
        scene.removeShape(l2);
        scene.removeShape(l3);
        scene.repaint();

        if (inTriangle(first, second, third, d)) return circleEnum.INSIDE;
        else if (Helpers.onLine(first, d, 30) || Helpers.onLine(first, d, -30) || Helpers.onLine(second, d, 90))
            return circleEnum.ON;
        else return circleEnum.OUTSIDE;
    }

    private boolean inTriangle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
        // d must be above the -30 degree line through a
        if (!isAbove(a, d, -30)) {
            return false;
        }
        // d must be below the 30 degree line through a
        if (!isBelow(a, d, 30)) {
            return false;
        }
        // d must be to the left of the 90 degree line through b/c
        if (!leftOf(b, d)) {
            return false;
        }

        return true;
    }
    
//    public boolean inTriangle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
//        double A = 1/2 * (-b.getY() * c.getX() + a.getY() * (-b.getX() + c.getX()) + a.getX() * (b.getY() - c.getY()) + b.getX() * c.getY());
//        double sign = A < 0 ? -1 : 1;
//        double s = (a.getY() * c.getX() - a.getX() * c.getY() + (c.getY() - a.getY()) * d.getX() + (a.getX() - c.getX()) * d.getY()) * sign;
//        double t = (a.getX() * b.getY() - a.getY() * b.getX() + (a.getY() - b.getY()) * d.getX() + (b.getX() - a.getX()) * d.getY()) * sign;
//
//        return s > 0 && t > 0 && (s + t) < 2 * A * sign;
//    }


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
        Line line_b, line_c;

        // the first two cases pertain to when at least one of b and c is on the vertical line
        if ((b.getX() > c.getX() && !isAbove(b, c, -30.0))
                || (b.getX() == c.getX() && b.getY() >= c.getY())) {
            b_angle = 90;
            c_angle = -30;
            line_b = new Line(b.getPoint(), new Point(b.getX(), b.getY()-5));
            line_c = new Line(c.getPoint(), new Point(c.getX()-5, getYCoordinate(c, c.getX()-5, -30)));
        } else if ((c.getX() > b.getX() && !isBelow(c, b, 30.0))
                || (c.getY() <= b.getY())) {
            b_angle = 30;
            c_angle = 90;
            line_c = new Line(c.getPoint(), new Point(c.getX(), c.getY()-5));
            line_b = new Line(b.getPoint(), new Point(b.getX()-5, getYCoordinate(b, b.getX()-5, 30)));
        } else {
            // neither of them can be on the vertical line. b is always on -30 and c on 30 then.
            b_angle = -30;
            c_angle = 30;
            line_b = new Line(b.getPoint(), new Point(b.getX()-5, getYCoordinate(b, b.getX()-5, -30)));
            line_c = new Line(c.getPoint(), new Point(c.getX()-5, getYCoordinate(c, c.getX()-5, 30)));
        }

        scene.addShape(line_b, Color.RED);
        scene.addShape(line_c, Color.RED);
        scene.repaint();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {}
        scene.removeShape(line_b);
        scene.removeShape(line_c);
        scene.repaint();
        // the three cases for INSIDE, although the first case is split up
        // case 1: a lies strictly interior to the infinite circle.
        if (b_angle == 90 && c_angle == -30 && !rightOf(b, a) && isAbove(c, a, -30)) {
            return infCircleEnum.INSIDE;
        } else if (c_angle == 90 && b_angle == 30 && !rightOf(c, a) && isBelow(b, a, 30)) {
            return infCircleEnum.INSIDE;
        } else if (b_angle != 90 && c_angle != 90 && isAbove(b, a, -30) && isBelow(c, a, 30)) {
            return infCircleEnum.INSIDE;
        }
        // case 2: a lies on the boundary of the infinite circle to the right of line bc.
        else if ((Helpers.onLine(b, a, b_angle) || Helpers.onLine(c, a, c_angle)) &&
                Helpers.rightOf(b, c, a)) {
            return infCircleEnum.INSIDE;
        }
        // case 3: segment bc lies on the boundary of the infinite circle and a lies in bc’s interior.
        else if ((Helpers.onLine(b, c, 30) || Helpers.onLine(b, c, -30)) &&
                Math.abs(b.getDistance(a) + a.getDistance(c) - b.getDistance(c)) <= 1e-6) {
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

        Logger.getGlobal().warning("ZERO TRI INF CIRCLE TEST FAILED!!!!!!");
        return infCircleEnum.INVALID;
    }
}
