package convex_dt.shapes;

import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;

/**
 * Created by cvium on 03-12-2016.
 */
public interface ConvexShape {
    void setScene(J2DScene scene);
    J2DScene getScene();
    /**
     * Predicate to test whether point d lies in the smallest circle containing a, b and c on its boundary.
     *
     * @param a
     * @param b
     * @param c
     * @param d query point
     * @return circleEnum
     */
    circleEnum inCircle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d);

    /**
     * Predicate test to determine where point a lies wrt. to the infinite circle with b and c on the boundary.
     *
     * @param a The query point
     * @param b First point on the boundary of the infinite circle
     * @param c The second point on the boundary of the infinite circle
     * @return inside, outside, before, after
     */
    infCircleEnum inInfCircle(KDSPoint a, KDSPoint b, KDSPoint c);
    enum infCircleEnum {
        INSIDE, OUTSIDE, BEFORE, AFTER, INVALID
    }
    enum circleEnum {
        INSIDE, OUTSIDE, ONBEFORE, ONAFTER, ON, INVALID
    }
}
