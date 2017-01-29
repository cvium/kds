package convex_dt.shapes;

import ProGAL.geom2d.Line;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;
import utils.Helpers;

import java.awt.*;
import java.util.logging.Logger;

import static utils.Helpers.*;

/**
 * Created by cvium on 03-12-2016.
 */
public class Pie implements ConvexShape {
    private J2DScene scene;

    @Override
    public J2DScene getScene() {
        return scene;
    }

    @Override
    public void setScene(J2DScene scene) {
        this.scene = scene;
    }

    public Pie() {
    }

    public Pie(J2DScene scene) {
        this.scene = scene;
    }

    @Override
    public circleEnum inCircle(KDSPoint a, KDSPoint b, KDSPoint c, KDSPoint d) {
        return null;
    }

    @Override
    public infCircleEnum inInfCircle(KDSPoint a, KDSPoint b, KDSPoint c) {
        // if standing in the corner and facing the triangle's interior, point b is always on the right edge
        // and point c on the left edge, since the infinite circle must touch b->c from the right

        double b_angle, c_angle;
        Line line_b, line_c;
        KDSPoint b_point, c_point;

        // the first two cases pertain to when at least one of b and c is on the vertical line
        if ((b.getX() > c.getX() && !isAbove(b, c, -30.0))
                || (b.getX() == c.getX() && b.getY() >= c.getY())) {
            b_angle = 90;
            c_angle = -30;
            line_b = new Line(b.getPoint(), new Point(b.getX(), b.getY()-5));
            line_c = new Line(c.getPoint(), new Point(c.getX()-5, getYCoordinate(c, c.getX()-5, -30)));
            b_point = new KDSPoint(new double[]{b.getX()}, new double[]{b.getY() + 5});
            c_point = new KDSPoint(new double[]{c.getX()-5}, new double[]{getYCoordinate(c, c.getX()-5, c_angle)});
        } else if ((c.getX() > b.getX() && !isBelow(c, b, 30.0))
                || (c.getY() <= b.getY())) {
            b_angle = 30;
            c_angle = 90;
            b_point = new KDSPoint(new double[]{b.getX()-5}, new double[]{getYCoordinate(b, b.getX()-5, b_angle)});
            c_point = new KDSPoint(new double[]{c.getX()}, new double[]{c.getY() - 5});
            line_c = new Line(c.getPoint(), new Point(c.getX(), c.getY()-5));
            line_b = new Line(b.getPoint(), new Point(b.getX()-5, getYCoordinate(b, b.getX()-5, 30)));
        } else {
            // neither of them can be on the vertical line. b is always on -30 and c on 30 then.
            b_angle = -30;
            c_angle = 30;
            b_point = new KDSPoint(new double[]{b.getX()+5}, new double[]{getYCoordinate(b, b.getX()+5, b_angle)});
            c_point = new KDSPoint(new double[]{c.getX()+5}, new double[]{getYCoordinate(c, c.getX()+5, c_angle)});
            line_b = new Line(b.getPoint(), new Point(b.getX()-5, getYCoordinate(b, b.getX()-5, -30)));
            line_c = new Line(c.getPoint(), new Point(c.getX()-5, getYCoordinate(c, c.getX()-5, 30)));
        }

        scene.addShape(line_b, Color.RED);
        scene.addShape(line_c, Color.RED);
        scene.repaint();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {}
        scene.removeShape(line_b);
        scene.removeShape(line_c);
        scene.repaint();
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

        Logger.getGlobal().warning("ZERO TRI INF CIRCLE TEST FAILED!!!!!!");
        return infCircleEnum.INVALID;
    }
}
