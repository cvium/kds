package tournament_tree;

import utils.Primitive;

/**
 * Created by clausvium on 27/12/16.
 */
public class TournamentNode<P extends Primitive> {

    private TournamentNode<P> parent;
    private TournamentNode<P> leftChild;
    private TournamentNode<P> rightChild;
    private TournamentEvent<P> event;
    private int key;
    private P object; // the winner object of the subtree rooted at this node
    private int weight;
    private boolean isNull = false; // special flag indicating that it's a null node

    public TournamentNode() {
        this.weight = 0;
    }

    public TournamentNode(boolean isNull) {
        this.weight = 0;
        this.isNull = isNull;
    }

    public TournamentNode(int key) {
        this.key = key;
        this.weight = 1;
    }

    public TournamentNode(int key, P object) {
        this.key = key;
        this.weight = 1;
        this.object = object;
    }

    public TournamentNode(int key, int weight, P object, TournamentNode<P> leftChild, TournamentNode<P> rightChild) {
        this.key = key;
        this.object = object;
        this.weight = weight;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public TournamentEvent<P> createEvent(double t) {
        event = new TournamentEvent<>(leftChild, rightChild);
        event.computeFailureTime(t);

        return event;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight == 0) weight = 1;  // make sure weight is always at least 1 when setting it outside the class
        this.weight = weight;
    }

    public TournamentNode<P> getLeftChild() {
        if (leftChild == null) return new TournamentNode<>(true);
        return leftChild;
    }

    public void setLeftChild(TournamentNode<P> leftChild) {
        assert !isNull;
        this.leftChild = leftChild;
        if (leftChild != null) leftChild.setParent(this);
    }

    public TournamentNode<P> getRightChild() {
        if (rightChild == null) return new TournamentNode<>(true);
        return rightChild;
    }

    public void setRightChild(TournamentNode<P> rightChild) {
        assert !isNull;
        this.rightChild = rightChild;
        if (rightChild != null) rightChild.setParent(this);
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public TournamentNode<P> getParent() {
        if (parent == null) return new TournamentNode<>(true);
        return parent;
    }

    public void setParent(TournamentNode<P> parent) {
        this.parent = parent;
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }

    public TournamentEvent<P> getEvent() {
        return event;
    }

    public void setEvent(TournamentEvent<P> event) {
        this.event = event;
    }

    public P getObject() {
        return object;
    }

    public void setObject(P object) {
        this.object = object;
    }

    /**
     * Returns true if the given node is the left child of this node
     *
     * @param node to check for leftness
     * @return true if input node is the left child
     */
    public boolean isLeftChild(TournamentNode<P> node) {
        return leftChild == node;
    }

    /**
     * Updates the weight of this node.
     */
    public void updateWeights() {
        int leftWeight = leftChild == null ? 0 : leftChild.getWeight();
        int rightWeight = rightChild == null ? 0 : rightChild.getWeight();
        this.weight = leftWeight + rightWeight;
    }

    /**
     * Updates the winner stored in this node
     */
    public void updateWinner() {
        // TODO
    }
}
