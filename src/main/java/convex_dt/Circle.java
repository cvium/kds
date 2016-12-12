package convex_dt;

import kds.KDSPoint;

import java.util.ArrayList;

import static convex_dt.Utils.leftOf;
import static convex_dt.Utils.rightOf;
import static java.util.Collections.sort;

/**
 * Created by cvium on 11-12-2016.
 */
public class Circle implements ConvexShape {
    @Override
    public circleEnum inCircle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
        ArrayList<KDSPoint> points = new ArrayList<>();
        points.add(a);
        points.add(b);
        points.add(c);
        sort(points);
        a = points.get(0);
        b = points.get(1);
        c = points.get(2);

        KDSPoint tmp;

        if (leftOf(a, c, b)) {
            tmp = c;
            c = b;
            b = tmp;
        }


        double adx = a.getX(0) - d.getX(0);
        double ady = a.getY(0) - d.getY(0);
        double bdx = b.getX(0) - d.getX(0);
        double bdy = b.getY(0) - d.getY(0);
        double cdx = c.getX(0) - d.getX(0);
        double cdy = c.getY(0) - d.getY(0);

        double abdet = adx * bdy - bdx * ady;
        double bcdet = bdx * cdy - cdx * bdy;
        double cadet = cdx * ady - adx * cdy;
        double alift = adx * adx + ady * ady;
        double blift = bdx * bdx + bdy * bdy;
        double clift = cdx * cdx + cdy * cdy;

        double det = alift * bcdet + blift * cadet + clift * abdet;
        if (Math.abs(det) < 1e-10) return circleEnum.ON;
        return det > 0 ? circleEnum.INSIDE : circleEnum.OUTSIDE;
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
