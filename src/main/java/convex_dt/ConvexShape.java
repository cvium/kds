package convex_dt;

import kds.KDSPoint;

/**
 * Created by cvium on 03-12-2016.
 */
public interface ConvexShape {
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
        INSIDE, OUTSIDE, BEFORE, AFTER
    }
    enum circleEnum {
        INSIDE, OUTSIDE, ONBEFORE, ONAFTER, ON
    }
}
