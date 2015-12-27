package kds;

import ProGAL.geom2d.*;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import sortedList.SortedEvent;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by clausvium on 21/12/15.
 */
public class KDSPoint implements Comparable<KDSPoint>{
    double[] coeffsX;
    double[] coeffsY;
    int idx;
    boolean inEvent;
    ArrayList<Certificate> certificates;
    double x;
    double y;

    public ArrayList<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(ArrayList<Certificate> certificates) {
        this.certificates = certificates;
    }

    public void removeCertificates() {
        for (Certificate c : certificates) {
            //c.setFailureTime(Double.MAX_VALUE);
            c.setValid(false);
        }
        this.certificates.clear();
    }

    public boolean isInEvent() {
        return inEvent;
    }

    public void setInEvent(boolean inEvent) {
        this.inEvent = inEvent;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    @Override
    public int compareTo(KDSPoint other) {
        double xdiff = Math.abs(this.x - other.x);
        double ydiff = Math.abs(this.y - other.y);
        if (xdiff <= 1e-10) {
            if (ydiff <= 1e-10) {
                return 0;
            } else {
                return this.y < other.y ? -1 : 1;
            }
        } else {
            return this.x < other.x ? -1 : 1;
        }
    }

    public KDSPoint(double[] coeffsX, double[] coeffsY) {
        this.x = coeffsX[0];
        this.y = coeffsY[0];
        this.coeffsX = coeffsX;
        this.coeffsY = coeffsY;
        this.certificates = new ArrayList<>();
    }

    public double[] getCoeffsX() {
        return coeffsX;
    }

    public double[] getCoeffsY() {
        return coeffsY;
    }

    public void setCoeffsX(double[] coeffsX) {
        this.coeffsX = coeffsX;
    }

    public void setCoeffsY(double[] coeffsY) {
        this.coeffsY = coeffsY;
    }

    public Point getPoint(double t) {
        double new_x = 0;

        for (int i = 0; i < coeffsX.length; ++i) {
            new_x += coeffsX[i] * Math.pow(t, i);
        }

        double new_y = 0;

        for (int i = 0; i < coeffsY.length; ++i) {
            new_y += coeffsY[i] * Math.pow(t, i);
        }

        this.x = new_x;
        this.y = new_y;

        return new Point(new_x, new_y);
    }

    public void swap(KDSPoint other) {
        double[] old_coeffsX = other.coeffsX;
        double[] old_coeffsY = other.coeffsY;
        int old_idx = other.idx;
        boolean old_inEvent = other.inEvent;
        ArrayList<Certificate> old_certificates = new ArrayList<>(other.certificates);

        other.coeffsX = this.coeffsX;
        other.coeffsY = this.coeffsY;
        other.idx = this.idx;
        other.inEvent = this.inEvent;
        other.certificates = new ArrayList<>(this.certificates);

        this.coeffsX = old_coeffsX;
        this.coeffsY = old_coeffsY;
        this.idx = old_idx;
        this.inEvent = old_inEvent;
        this.certificates = new ArrayList<>(old_certificates);
    }

    public void draw(J2DScene scene, double t, Color c) {
        /*Circle circ = new Circle(getPoint(t), 0.01);
        scene.addShape(circ, c);*/
        getPoint(t).toScene(scene, 0.01, c);
        this.inEvent = false;
    }

    public void draw(J2DScene scene, double t) {
        Color color = isInEvent() ? java.awt.Color.RED : java.awt.Color.BLUE;
        Circle circ = new Circle(getPoint(t), 0.01);
        scene.addShape(circ, color);
        this.inEvent = false;
    }

    public void updatePosition(double t) {
        getPoint(t);
    }

    public static void toFile(ArrayList<KDSPoint> kps) throws IOException{
        ArrayList<String> lines = new ArrayList<>();
        for (KDSPoint kp : kps) {
            String init = "points.add(new KDSPoint(";
            String x = "new double[]{";
            String y = "new double[]{";
            for (int i = 0; i < kp.getCoeffsX().length; ++i) {
                x += kp.getCoeffsX()[i];
                if (i < kp.getCoeffsX().length-1) {
                    x += ",";
                } else {
                    x += "}";
                }
            }
            String mid = ",";
            for (int i = 0; i < kp.getCoeffsY().length; ++i) {
                y += kp.getCoeffsY()[i];
                if (i < kp.getCoeffsY().length-1) {
                    y += ",";
                } else {
                    y += "}";
                }
            }
            String end = "));";
            lines.add(init + x + mid + y + end);
        }
        Path file = Paths.get("coeffs.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    public static void toFileGoogle(ArrayList<KDSPoint> kps) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (KDSPoint kp : kps) {
            String a = "";
            for (int i = 0; i < kp.getCoeffsX().length; ++i) {
                a += kp.getCoeffsX()[i] + "*x^" + i;
                if (i < kp.getCoeffsX().length - 1) {
                    a += "+";
                } else {
                    a += kp.getCoeffsX()[i];
                }
            }
            lines.add(a);
        }
        Path file = Paths.get("coeffs_google.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }
}
