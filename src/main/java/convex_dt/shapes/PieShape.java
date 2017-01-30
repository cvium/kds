package convex_dt.shapes;

import ProGAL.geom2d.Circle;
import ProGAL.geom2d.Line;
import ProGAL.geom2d.LineSegment;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import kds.KDS;
import kds.KDSPoint;
import utils.Helpers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import static utils.Helpers.*;

/**
 * Created by cvium on 03-12-2016.
 */
public class PieShape implements ConvexShape {
    private J2DScene scene;

    @Override
    public J2DScene getScene() {
        return scene;
    }

    @Override
    public void setScene(J2DScene scene) {
        this.scene = scene;
    }

    public PieShape() {
    }

    public PieShape(J2DScene scene) {
        this.scene = scene;
    }

    /**
     * Returns the rightmost point. Assumes the points do not have equal x-values.
     *
     * @param points
     * @return
     */
    private KDSPoint rightmost(Point[] points) {
        Point rightmost = null;
        for (Point p : points) {
            if (rightmost == null) rightmost = p;
            else if (rightmost.x() < p.x()) rightmost = p;
        }

        return new KDSPoint(rightmost);
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
        KDSPoint first; // the center of the pie

        ArrayList<KDSPoint> sorted_y = new ArrayList<>();
        sorted_y.add(a);
        sorted_y.add(b);
        sorted_y.add(c);
        // sorted on y coordinate in ascending order
        sorted_y.sort((o1, o2) -> {
            if (o1.getY() < o2.getY()) return -1;
            return Math.abs(o1.getY()-o2.getY()) < 1e-6 ? 0 : 1;
        });
        // sorted on x and then y
        ArrayList<KDSPoint> sorted_x = new ArrayList<>(sorted_y);
        Collections.sort(sorted_x);

        // first we check whether all the points lie on the arc
        Circle circle = new Circle(a.getPoint(), b.getPoint(), c.getPoint());
        Circle circle1 = new Circle(sorted_x.get(1).getPoint(), sorted_x.get(2).getPoint());
        KDSPoint circle1_center = new KDSPoint(circle1.getCenter());

        if (Helpers.onLine(new KDSPoint(circle.getCenter()), sorted_y.get(2), 30) &&
                Helpers.onLine(new KDSPoint(circle.getCenter()), sorted_y.get(0), -30)) {
            first = findIntersection(sorted_y.get(2), 30, sorted_y.get(0), -30);
        }
        // then we check whether the two rightmost points lie on the arc (last point must then be on 30/-30 line)
        // the following clusterfuck checks that all three points lie in the 60 degree wedge from the center of the
        // circle containing the two rightmost points on its boundary
        else if (!isAbove(circle1_center, a, 30) && !isAbove(circle1_center, b, 30) && !isAbove(circle1_center, c, 30)
                && !isBelow(circle1_center, a, -30) && !isBelow(circle1_center, b, -30) && !isBelow(circle1_center, c, -30)) {
            first = circle1_center;
            circle = circle1;
//            Point p_30 = new Point(first.getX() + 5, getYCoordinate(first, first.getX() + 5, 30));
//            Point p_minus30 = new Point(first.getX() + 5, getYCoordinate(first, first.getX() + 5, -30));
//
//            Point[] p30_intersections = circle1.intersections(new Line(first.getPoint(), p_30));
//            if (p30_intersections[0].x() < p30_intersections[1].x()) {
//                second = new KDSPoint(p30_intersections[1]);
//            } else {
//                second = new KDSPoint(p30_intersections[0]);
//            }
//            Point[] pminus30_intersections = circle1.intersections(new Line(first.getPoint(), p_minus30));
//            if (pminus30_intersections[0].x() < pminus30_intersections[1].x()) {
//                third = new KDSPoint(pminus30_intersections[1]);
//            } else {
//                third = new KDSPoint(pminus30_intersections[0]);
//            }
        }
        // if none of the above, then rightmost point is on arc and the other two on 30 and -30 line (just like in ZeroTriShape)
        else {
            // first there are some conditions for there to be a valid pie through these points
            // the rightmost point must be on the arc
            KDSPoint arc = sorted_x.get(0);
            sorted_y.remove(arc);
            // the two leftmost points
            KDSPoint p_thirty = sorted_y.get(1);
            KDSPoint p_neg_thirty = sorted_y.get(0);

            // p_thirty has to lie on or above the -30 and 30 lines through p_neg_thirty
            if (isBelow(p_neg_thirty, p_thirty, 30) || isBelow(p_neg_thirty, p_thirty, -30)) {
                Logger.getGlobal().info("Not a valid PIE");
                return circleEnum.INVALID;
            }

            // we need to find the center for the circle by finding the intersection of the 30 and -30 lines through
            first = findIntersection(p_thirty, 30, p_neg_thirty, -30);
            circle = new Circle(first.getPoint(), first.getPoint().distance(arc.getPoint()));

            // arc point must lie on or inside the wedge emanating from intersection point
            if (isAbove(first, arc, 30) || isBelow(first, arc, -30)) {
                Logger.getGlobal().info("Not a valid PIE");
                return circleEnum.INVALID;
            }
        }

        Line l1 = getLine(first, 30);
        Line l2 = getLine(first, -30);
        scene.addShape(l1, Color.BLACK);
        scene.addShape(l2, Color.BLACK);
        scene.addShape(circle, Color.BLACK);
        scene.repaint();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {}
        scene.removeShape(l1);
        scene.removeShape(l2);
        scene.removeShape(circle);
        scene.repaint();

        // INSIDE: if  d  is interior  to some placement of a smallest circle through a, b, and c
        if (isBelow(first, d, 30) && isAbove(first, d, -30) &&
                (circle.contains(d.getPoint()) || circle.onCircle(d.getPoint()))) {
            return circleEnum.INSIDE;
        }
        // OUTSIDE: if d is strictly exterior to all placements of a smallest circle through a, b, and c
        if (isAbove(first, d, 30) || isBelow(first, d, -30) ||
                !(circle.contains(d.getPoint()) || circle.onCircle(d.getPoint()))) {
            return circleEnum.OUTSIDE;
        }
        // There should be no onBefore and onAfter for pies as a pie is always uniquely defined for three points
        // if it exists
        return circleEnum.ON;
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
        Line line_b, line_c;

        // the first two cases pertain to when at least one of b and c is on the "vertical" line (60 or -60)
        // case 1: b is on the "vertical" line (and possibly c)
        if (!isBelow(b, c, 60) && b.getX() >= c.getX() && !isAbove(b, c, -30.0)) {
            b_angle = 60;
            c_angle = -30;
        }
        // case 2: c is on the "vertical" line (and possibly b)
        else if (!isAbove(c, b, -60) && c.getX() >= b.getX() && !isBelow(c, b, 30)) {
            b_angle = 30;
            c_angle = -60;
        } else if (b.getY() < c.getY()){
            // neither of them can be on the vertical line. b is always on -30 and c on 30 then.
            b_angle = -30;
            c_angle = 30;
        } else {
            c_angle = 30;
            b_angle = -30;
            // have to swap them
            KDSPoint tmp = b;
            b = c;
            c = tmp;

//            Logger.getGlobal().warning("INVALID INF PIE");
//            return infCircleEnum.INVALID;
        }
        line_b = new Line(b.getPoint(), new Point(b.getX()+5, getYCoordinate(b, b.getX()+5, b_angle)));
        line_c = new Line(c.getPoint(), new Point(c.getX()-5, getYCoordinate(c, c.getX()-5, c_angle)));

        scene.addShape(line_b, Color.RED);
        scene.addShape(line_c, Color.RED);
        scene.repaint();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {}
        scene.removeShape(line_b);
        scene.removeShape(line_c);
        scene.repaint();
        // the three cases for INSIDE
        // case 1: a lies strictly interior to the infinite circle.
        //         Split into three cases.
        if (b_angle == 60 && isAbove(c, a, c_angle) && (isAbove(b, a, b_angle) || Helpers.onLine(b, a, b_angle) && b.getX() >= a.getX())) {
            return infCircleEnum.INSIDE;
        } else if (c_angle == -60 && isBelow(b, a, b_angle) && (isBelow(c, a, c_angle) || Helpers.onLine(c, a, c_angle) && c.getX() >= a.getX())) {
            return infCircleEnum.INSIDE;
        } else if ((b_angle != 60 && c_angle != -60) && isAbove(b, a, b_angle) && isBelow(c, a, c_angle)) {
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

        // OUTSIDE: if a is not to the right of line bc, and is not inside.
        if (Helpers.leftOf(b, c, a)) {
            return infCircleEnum.OUTSIDE;
        }

        // BEFORE: if a lies to the right of bc, is not inside, and its projection onto bc is before b
        if (Helpers.rightOf(b, c, a) && before(b, c, a))
            return infCircleEnum.BEFORE;

        // AFTER: if a lies to the right of bc, is not inside, and its projection onto bc is after c
        if (Helpers.rightOf(b, c, a) && after(b, c, a))
            return infCircleEnum.AFTER;

        Logger.getGlobal().warning("PIE INF CIRCLE TEST FAILED!!!!!!");
        return infCircleEnum.INVALID;
    }
}
