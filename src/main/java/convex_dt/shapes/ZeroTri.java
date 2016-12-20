package convex_dt.shapes;

import kds.KDSPoint;

/**
 * Created by cvium on 15-12-2016.
 */
public class ZeroTri implements ConvexShape {
    @Override
    public circleEnum inCircle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
        return null;
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
        return null;
    }
}
