package convex_dt.shapes;

import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;

import static utils.Helpers.leftOf;
import static utils.Helpers.rightOf;
import static java.util.Collections.sort;

/**
 * Created by cvium on 11-12-2016.
 */
public class CircleShape implements ConvexShape {
    private J2DScene scene;

    @Override
    public J2DScene getScene() {
        return scene;
    }

    @Override
    public void setScene(J2DScene scene) {
        this.scene = scene;
    }

    public CircleShape() {}

    public CircleShape(J2DScene scene) {
        this.scene = scene;
    }

    @Override
    public circleEnum inCircle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
        double ad_x = a.getX(0) - d.getX(0);
        double ad_y = a.getY(0) - d.getY(0);

        double bd_x = b.getX(0) - d.getX(0);
        double bd_y = b.getY(0) - d.getY(0);

        double cd_x = c.getX(0) - d.getX(0);
        double cd_y = c.getY(0) - d.getY(0);

        double ad = ad_x * ad_x + ad_y * ad_y;
        double bd = bd_x * bd_x + bd_y * bd_y;
        double cd = cd_x * cd_x + cd_y * cd_y;

        double row1 = ad_x * (bd_y * cd - bd * cd_y);
        double row2 = ad_y * (bd_x * cd - bd * cd_x);
        double row3 = ad   * (bd_x * cd_y - bd_y * cd_x);

        double det = - (row1 - row2 + row3);

        if (det > 1e-10) return circleEnum.INSIDE;
        else if (det < 1e-10) return circleEnum.OUTSIDE;
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
//        if (b.compareTo(c) < 0) {
//            KDSPoint tmp = b;
//            b = c;
//            c = tmp;
//        }
        if (rightOf(b, c, a)) return infCircleEnum.INSIDE;
        else if (leftOf(b, c, a)) return infCircleEnum.OUTSIDE;
        else {
            System.out.println("THIS REALLY SHOULDNT HAPPEN!!!!");
            return infCircleEnum.AFTER;
        }
    }
}
