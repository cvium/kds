/**
 * Created by clausvium on 21/12/15.
 */
package sortedList;

import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geom2d.*;
import kds.Certificate;
import kds.KDSPoint;

import java.awt.*;
import java.util.*;

class run {
    private static int N = 20;
    private static int M = 2;
    private static int T = 10;
    private static double TIMESTEP = 0.01;

    public static void main(String[] args) throws Exception {
        J2DScene scene = J2DScene.createJ2DSceneInFrame();
        Random rand = new Random();
        ArrayList<KDSPoint> points = new ArrayList<>(N);
        /*points.add(new KDSPoint(new double[]{0.21178174548323625,0.35227221303691514}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.38776908727933024,0.36598790870872144}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.12881156841380526,0.9544637264652279}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.25701489343390926,0.5136243875865947}, new double[]{0, 0}));
        Color[] c = {Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.GREEN};

        for(int i = 0; i < N; ++i) {
            points.get(i).draw(scene, 0, c[i]);
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
        scene.centerCamera();
        scene.autoZoom();
        scene.repaint();
        SortedList sl = new SortedList(points);

        double t = 0.0;

        while (t <= T) {
            scene.removeAllShapes();
            try {
                while (sl.eq.queue.element().getCertificate().getFailureTime() <= t) {
                    SortedEvent event = (SortedEvent) sl.eq.queue.poll();
                    if (event.getCertificate().isValid()) {
                        event.process(t);
                        System.out.println("EVENT at time " + t);
                    }
                    if (!sl.audit(t)) {
                        throw new Exception("PLS");
                    }
                }
            } catch (NoSuchElementException e) {
                // do nothing
            }
            for(int i = 0; i < N; ++i) {
                sl.points.get(i).draw(scene, t);
            }
            scene.repaint();
            try{Thread.sleep(100);} catch(InterruptedException e) {}
            double nextFailTime;
            try {
                if (sl.eq.queue.element().getCertificate().getFailureTime() - t < 0.01 &&
                        sl.eq.queue.element().getCertificate().isValid()) {
                    nextFailTime = sl.eq.queue.element().getCertificate().getFailureTime();
                } else {
                    nextFailTime = t + TIMESTEP;
                }
            } catch (NoSuchElementException e) {
                nextFailTime = t + TIMESTEP;
            }
            t = nextFailTime;
        }
    }
}
