package convex_dt.shapes;

import kds.KDSPoint;

/**
 * Created by cvium on 03-12-2016.
 */
public class Pie implements ConvexShape {
    public Pie() {
    }

    @Override
    public circleEnum inCircle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
        return null;
    }

    @Override
    public infCircleEnum inInfCircle(KDSPoint a, KDSPoint b, KDSPoint c) {
        return null;
    }
}
