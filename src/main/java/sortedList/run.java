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
import java.lang.reflect.Array;
import java.util.*;

class run {
    private static int N = 500;
    private static int M = 5;
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
        /*
        points.add(new KDSPoint(new double[]{0.3904569761385204, 0.02698062109381527}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.1658290937912802, 0.8027016482417944}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.43716114650039883, 0.5547079004649283}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.9929681488989968, 0.1354213645339587}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.6700482058998174, 0.4796405986054495}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.49311222649462605, 0.6895203840234286}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.8794640365309483, 0.3575054364380311}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.8259547387806009, 0.5105496759433658}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.9158694718752232, 0.7472441411417013}, new double[]{0, 0}));
        points.add(new KDSPoint(new double[]{0.9819330206706257, 0.7471000852315168}, new double[]{0, 0}));

        Color[] c = {Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.darkGray,
                Color.ORANGE, Color.PINK, Color.YELLOW};

        for(int i = 0; i < N; ++i) {
            points.get(i).draw(scene, 0, c[i]);
        }*/

        /*
        points.add(new KDSPoint(new double[]{0.3904569761385204, 0.02698062109381527, 0.48964497038000576,
                0.7075512249796881, 0.7010952138904888}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.17678047033477418, 0.3919109435017235, 0.9302073481374996,
                0.6304472034698598, 0.6815572963336319}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.4256458871449901, 0.47327950117544715, 0.08148073711826309,
                0.8127529937983307, 0.3697212003652316}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.2975797846297298, 0.7420598513209492, 0.08170569261832572,
                0.9058111471292961, 0.2844109529943464}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.3025843272705221, 0.8474512950612914, 0.4478570204062481,
                0.056794581831290936, 0.505997635035555}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.3553933715001161, 0.24416854923976428, 0.9163552946863107,
                0.8667556856377399, 0.6024291299257032}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.6888883511198657, 0.32285109967969505, 0.1623488838406998,
                0.08464831747912127, 0.3096897969409068}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.6206635342602977, 0.5366296901434615, 0.09034548410518517,
                0.9030273267967269, 0.7353781403591911}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.5259735888845548, 0.699523549737408, 0.8039640994814269,
                0.8409951117911274, 0.33916494860394353}, new double[]{0, 0, 0, 0, 0}));
        points.add(new KDSPoint(new double[]{0.9087772057650372, 0.4311158087018987, 0.5448479624424398,
                0.7371832106732925, 0.28549155003542726}, new double[]{0, 0, 0, 0, 0}));
        */
        /*
        points.add(new KDSPoint(new double[]{0.5084869422792161,0.19800192479885215,0.45531448050522516,0.6868919667271562,0.993574466103454},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.9510228591488803,0.03841483529978151,0.19006801897766312,0.6585533932714205,0.3460709035712416},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.25782188127951255,0.10334321485581799,0.7860322898021634,0.5021049599206406,0.7912059082098827},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.7514373615074302,0.3587435774679024,0.2101030110419998,0.6359068655800417,0.18932358024005813},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.591902645017155,0.10738733837915682,0.6229280686152997,0.43280734831953327,0.36387180717568635},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.8346127908732816,0.7282367860373886,0.51229909257996,0.45763527190119524,0.004184124344166706},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.8424132697883455,0.024737611053175956,0.3742242229926528,0.41531343910169094,0.8364469552060154},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.9181919112217994,0.2341133532648475,0.7136245274149469,0.3444870290033041,0.9594894036166848},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.9437438686624938,0.6121554900159948,0.6736119178713141,0.7072884941928156,0.37170983274960956},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.8658071828592309,0.646190057220281,0.8413344170005207,0.7527785337648647,0.42272952933234764},new double[]{0.0,0.0,0.0,0.0,0.0}));

        *//*
        points.add(new KDSPoint(new double[]{0.4103753479430957,0.20397440729967298,0.206329275699086,0.753102096495774,0.46234011474204184},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.6166072679055391,0.8092095766960287,0.21157165002054756,0.22092587183386558,0.569188491996689},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.8117259953714798,0.1447546666764279,0.5257333623511364,0.059372297944845,0.8008006814521813},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.8172869512191797,0.7731619121170982,0.9982536150889694,0.399483297721809,0.03731637255620246},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.5049722371030404,0.6672971290383739,0.42054901783498977,0.4326216668627959,0.7020310840255034},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.48549758016432065,0.78626791153525,0.5736595722669466,0.1427656223756295,0.22779312919046868},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.037252411557737064,0.9893476731313464,0.9016753645513587,0.4968859776541439,0.17223443187258225},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.11064721059219618,0.43531992761004135,0.8180080235076149,0.635819312922344,0.5725188018766433},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.7315210031838416,0.19980141634885684,0.5027092269586045,0.10658101714273516,0.6820875590877933},new double[]{0.0,0.0,0.0,0.0,0.0}));
        points.add(new KDSPoint(new double[]{0.38436710512136774,0.29458353845362883,0.4648101141032609,0.18019326069952601,0.29566710016660536},new double[]{0.0,0.0,0.0,0.0,0.0}));

        Color[] c = {Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.GRAY,
                Color.ORANGE, Color.PINK, Color.YELLOW};

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
        KDSPoint.toFile(points);

        scene.centerCamera();
        scene.autoZoom();
        scene.repaint();
        SortedList sl = new SortedList(points);

        double t = 0.0;

        while (t <= T) {
            scene.removeAllShapes();
            try {
                ArrayList<SortedEvent> es = sl.eq.peek();
                while (sl.eq.firstKey() <= t) {
                    es = sl.eq.poll();
                    for (SortedEvent<KDSPoint> e : es) {
                        if (e.getCertificate().isValid()) {
                            e.process(t);
                            System.out.println("EVENT at time " + e.getCertificate().getFailureTime());
                        }
                    }
                }
                if (!sl.audit(t)) {
                    System.out.println("Auditing failed");
                    throw new Exception("Auditing failed");
                }
            } catch (NoSuchElementException e) {
                // do nothing
            }
            for(int i = 0; i < N; ++i) {
                //sl.points.get(i).draw(scene, t, c[i]);
                sl.points.get(i).draw(scene, t);
            }
            scene.repaint();
            //try{Thread.sleep(500);} catch(InterruptedException e) {}
            double nextFailTime;
            try {
                if (sl.eq.firstKey() - t < 0.01) {
                    nextFailTime = sl.eq.firstKey();
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
