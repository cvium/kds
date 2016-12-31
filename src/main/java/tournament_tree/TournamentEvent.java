package tournament_tree;

import utils.Primitive;

/**
 * Created by clausvium on 22/12/16.
 */
public class TournamentEvent<P extends Primitive> extends kds.Event<P>{

    private double failureTime;
    private TournamentNode<P> first;
    private TournamentNode<P> second;

    public TournamentEvent(TournamentNode<P> first, TournamentNode<P> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void process(double t) {
        System.out.println("PROCESS");
    }

    @Override
    public void computeFailureTime(double t) {

    }

}
