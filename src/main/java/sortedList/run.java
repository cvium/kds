/**
 * Created by clausvium on 21/12/15.
 */
package sortedlist;

import kds.KDSPoint;
import kds.Simulator;

import java.util.logging.Level;

class run {

    public static void main(String[] args) throws Exception {
        final int N = 50;
        final int M = 2;
        final int T = 10;
        final int NUMRUNS = 1;
        final double STARTTIME = 0.1;
        final double TIMESTEP = 0.1;
        final Level loggerLevel = Level.SEVERE;
        int failedRuns = 0;
        for (int i = 0; i < NUMRUNS; ++i) {
            SortedList kds = new SortedList(STARTTIME, N, M);
            Simulator<KDSPoint, SortedEvent> sim = new Simulator<>(kds, STARTTIME, TIMESTEP, T, loggerLevel);
            if (sim.run(true) != 0) {
                ++failedRuns;
            }
        }
        System.out.println("Failed runs: " + failedRuns);
        System.exit(0);
    }
}
