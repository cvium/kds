package kds;

import ProGAL.geom2d.viewer.J2DScene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by clausvium on 28/12/15.
 */

public class Simulator<PointType extends KDSPoint, EventType extends Event<PointType>> {
    private static final Logger LOGGER = Logger.getLogger( Simulator.class.getName() );
    boolean visualize = true;
    KDS<PointType, EventType> kds;
    double timestep;
    double starttime;
    double endtime;
    Level loggerLevel;
    J2DScene scene;
    boolean paused;

    public J2DScene getScene() {
        return scene;
    }

    public void setScene(J2DScene scene) {
        this.scene = scene;
    }

    public Level getLoggerLevel() {
        return loggerLevel;
    }

    public void setLoggerLevel(Level loggerLevel) {
        this.loggerLevel = loggerLevel;
    }

    public Simulator(KDS<PointType, EventType> kds, double starttime, double timestep, double endtime,
                     Level loggerLevel) {
        this.kds = kds;
        this.starttime = starttime;
        this.timestep = timestep;
        this.endtime = endtime;
        this.loggerLevel = loggerLevel;
        LOGGER.setLevel(this.loggerLevel);
        Logger topLogger = java.util.logging.Logger.getLogger("");
        topLogger.getHandlers()[0].setLevel( this.loggerLevel );
        paused = false;
    }

    public double getTimestep() {
        return timestep;
    }

    public void setTimestep(double timestep) {
        this.timestep = timestep;
    }



    public boolean isVisualizing() {
        return visualize;
    }
    public void setVisualization(boolean visualize) {
        this.visualize = visualize;
    }
    public int run(boolean visualize, boolean auditEveryTimestep) throws IOException {
        LOGGER.log(Level.INFO, "Starting simulation");
        KDSKeyListener kdsKeyListener = new KDSKeyListener(this);
        if (visualize) {
            if (scene == null) scene = J2DScene.createJ2DSceneInFrame();
            scene.addKeyListener(kdsKeyListener);
        }
        /*
        Color[] c = {Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.GRAY,
                Color.ORANGE, Color.PINK, Color.YELLOW};

        for(int i = 0; i < N; ++i) {
            points.get(i).draw(scene, 0);
        }*/

        if (visualize) {
            for (int i = 0; i < kds.getPrimitives().size(); ++i) {
                kds.getPrimitives().get(i).draw(scene, 0);
            }
            scene.centerCamera();
            scene.autoZoom();
            scene.repaint();
        }

        double t = starttime;
        boolean event = false;
        int errors = 0;
        while (t <= endtime) {
            LOGGER.log(Level.FINER, "Time: {0}", t);
            if (paused) continue;
            ArrayList<EventType> es;
            try {
                while (kds.getEventQueue().firstKey() <= t) {
                    es = kds.getEventQueue().poll();
                    for (EventType e : es) {
                        if (e.isValid()) {
                            event = true;
                            e.process(e.getFailureTime());
                            LOGGER.log(Level.FINER, "EVENT at time t={0}", e.getFailureTime());
                        }
                    }
                }
            } catch (NullPointerException e) {
                // do nothing
            }
            if (auditEveryTimestep || event) {
                if (!kds.audit(t)){
                    LOGGER.log(Level.SEVERE, "Auditing failed");
                    ++errors;
                } else{
                    LOGGER.log(Level.FINE, "Auditing succeeded");
                }
            }
            event = false;
            if (visualize) {
                scene.removeAllShapes();
                for(int i = 0; i < kds.getPrimitives().size(); ++i) {
                    kds.getPrimitives().get(i).draw(scene, t);
                }
                scene.repaint();
                try {
                    TimeUnit.NANOSECONDS.sleep((long) (timestep * 1000000000));
                } catch (InterruptedException e) {}
            }
            t += timestep;
        }
        LOGGER.log(Level.INFO, "End of simulation, {0} errors", errors);
        //System.exit(0);
        return errors;
    }

    public void pause() {
        paused = !paused;
    }
    public void faster() {
        timestep += timestep;
        System.out.println(timestep);
    }
    public void slower() {
        if (timestep-timestep > 0) {
            timestep -= timestep;
        }
    }
}
