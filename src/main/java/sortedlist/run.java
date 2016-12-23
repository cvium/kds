/**
 * Created by clausvium on 21/12/15.
 */
package sortedlist;

import kds.KDSPoint;
import kds.Simulator;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

class run {

    public static void main(String[] args) throws Exception {
        final int numPoints = 5000;
        final int degree = 2;
        final int endtime = 10;
        final int NUMRUNS = 10;
        final double STARTTIME = 0.1;
        final double TIMESTEP = 0.1;
        final Level loggerLevel = Level.FINE;
        int failedRuns = 0;
        for (int i = 0; i < NUMRUNS; ++i) {
            ArrayList<KDSPoint> points = new ArrayList<>();
            Random rand = new Random();
            for (int j = 0; j < numPoints; ++j) {
                double[] coeffsX = new double[degree];
                double[] coeffsY = new double[degree];

                for (int k = 0; k < degree; ++k) {
                    coeffsX[k] = -10 + (10 + 10) * rand.nextDouble();//
                    //System.out.println(coeffsX[j]);
                    coeffsY[k] = 0;
                }
                points.add(new KDSPoint(coeffsX, coeffsY));
            }
            SortedList<KDSPoint> kds = new SortedList<>(STARTTIME, points);
            Simulator<KDSPoint, SortedEvent<KDSPoint>> sim = new Simulator<>(kds, STARTTIME, TIMESTEP, endtime, loggerLevel);
            if (sim.run(false, true) != 0) {
                ++failedRuns;
            }
        }
        System.out.println("Failed runs: " + failedRuns);
        System.exit(0);
    }
}
