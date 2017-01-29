package convex_dt.shapes;

import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;

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
        return null;
    }
}
