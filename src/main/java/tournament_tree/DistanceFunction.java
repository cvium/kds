package tournament_tree;

import utils.Primitive;

/**
 * Created by clausvium on 28/12/16.
 */
public class DistanceFunction<P extends Primitive> implements TournamentTreeWinner<P> {
    private Primitive p;

    public DistanceFunction(Primitive p) {
        this.p = p;
    }

    @Override
    public P findWinner(P first, P second) {
        double firstDistance = p.getDistance(first);
        double secondDistance = p.getDistance(second);
        return firstDistance < secondDistance ? first : second;
    }

    @Override
    public double computeValue(Primitive primitive) {
        return p.getDistance(primitive);
    }
}
