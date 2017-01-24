package tournament_tree;

import utils.Primitive;

/**
 * Created by clausvium on 28/12/16.
 */
public class DistanceFunction<P extends Primitive> implements TournamentTreeWinner<P> {
    private P p;

    public DistanceFunction(P p) {
        this.p = p;
    }

    public P getP() {
        return p;
    }

    public void setP(P p) {
        this.p = p;
    }

    @Override
    public P findWinner(P first, P second) {
        double firstDistance = p.getDistance(first);
        double secondDistance = p.getDistance(second);
        if (Math.abs(firstDistance - secondDistance) < 1e-10) System.out.println("FUCK");
        return firstDistance <= secondDistance ? first : second;
    }

    @Override
    public double computeValue(double t, Primitive primitive) {
        p.updatePosition(t);
        return p.getDistance(primitive);
    }
}
