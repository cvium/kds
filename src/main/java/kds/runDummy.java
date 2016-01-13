/**
 * Created by clausvium on 21/12/15.
 */
package kds;

import java.util.logging.Level;

class runDummy {

    public static void main(String[] args) throws Exception {
        final int N = 50;
        final int M = 3;
        final int T = 10;
        final int NUMRUNS = 1;
        final double STARTTIME = 0.0;
        final double TIMESTEP = 0.0001;
        final Level loggerLevel = Level.FINE;
        int failedRuns = 0;
        for (int i = 0; i < NUMRUNS; ++i) {
            DummyKDS kds = new DummyKDS(STARTTIME, N, M);
            Simulator<BoundedKDSPoint, Event<BoundedKDSPoint>> sim = new Simulator<>(kds, STARTTIME, TIMESTEP, T, loggerLevel);
            if (sim.run(true, false) != 0) {
                ++failedRuns;
            }
        }
        System.out.println("Failed runs: " + failedRuns);
        System.exit(0);
    }
}
