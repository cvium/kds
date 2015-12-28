/**
 * Created by clausvium on 21/12/15.
 */
package sortedList;

import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geom2d.*;
import kds.KDSPoint;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.DoubleToIntFunction;

class run {
    private static int N = 5000;
    private static int M = 3;
    private static int T = 10;
    private static double TIMESTEP = 0.1;

    public static void main(String[] args) throws Exception {
        J2DScene scene = J2DScene.createJ2DSceneInFrame();
        Random rand = new Random();
        ArrayList<KDSPoint> points = new ArrayList<>(N);
        /*
        Color[] c = {Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.GRAY,
                Color.ORANGE, Color.PINK, Color.YELLOW};

        for(int i = 0; i < N; ++i) {
            points.get(i).draw(scene, 0);
        }*/

        for (int i = 0; i < N; ++i) {
            double[] coeffsX = new double[M];
            double[] coeffsY = new double[M];

            for (int j = 0; j < M; ++j) {
                coeffsX[j] = rand.nextDouble();
                coeffsY[j] = 0;
            }
            points.add(new KDSPoint(coeffsX, coeffsY));
        }
        for(int i = 0; i < N; ++i) {
            points.get(i).draw(scene, 0);
        }
        KDSPoint.toFile(points);

        scene.centerCamera();
        scene.autoZoom();
        scene.repaint();
        SortedList sl = new SortedList(points);

        double t = 0.0;
        boolean event = false;
        while (t <= T) {
            System.out.println(t);
            scene.removeAllShapes();
            try {
                while (sl.eq.firstKey() <= t) {
                    ArrayList<SortedEvent> es = sl.eq.poll();
                    for (SortedEvent e : es) {
                        if (e.isValid()) {
                            event = true;
                            e.process(e.getFailureTime());
                            //System.out.println("EVENT at time " + e.getFailureTime());
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                // do nothing
            }
            if (!sl.audit(t)) {
                System.out.println("Auditing failed");
                //throw new Exception("Auditing failed");
            }
            event = false;
            for(int i = 0; i < N; ++i) {
                //sl.points.get(i).draw(scene, t, c[i]);
                sl.points.get(i).draw(scene, t);
            }
            scene.repaint();
            //try{Thread.sleep(100);} catch(InterruptedException e) {}
            t += TIMESTEP;
        }
        System.out.println("End of simulation.");
    }
}
