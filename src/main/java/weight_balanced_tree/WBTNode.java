package weight_balanced_tree;

import kds.KDS;

/**
 * Created by clausvium on 26/12/16.
 */
public class WBTNode<Key extends Comparable<Key>> {
    private int weight;
    private boolean isNull = false; // special value for making null nodes to avoid making explicit null checks
    private WBTNode<Key> parent;
    private WBTNode<Key> leftChild;
    private WBTNode<Key> rightChild;
    private Key key;

    public WBTNode(Key key) {
        this.key = key;
    }

    public WBTNode(boolean isNull) {
        this.isNull = isNull;
        this.weight = 0;
        this.key = null;
    }

    public WBTNode(Key key, int weight) {
        this.key = key;
        this.weight = weight;
    }

    public WBTNode(Key key, WBTNode<Key> leftChild, WBTNode<Key> rightChild) {
        this.key = key;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
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
        this.weight = weight;
    }

    public WBTNode<Key> getLeftChild() {
        if (isNull) return new WBTNode<>(true);
        return leftChild;
    }

    public void setLeftChild(WBTNode<Key> leftChild) {
        assert !isNull;
        this.leftChild = leftChild;
        leftChild.setParent(this);
    }

    public WBTNode<Key> getRightChild() {
        if (isNull) return new WBTNode<>(true);
        return rightChild;
    }

    public void setRightChild(WBTNode<Key> rightChild) {
        assert !isNull;
        this.rightChild = rightChild;
        rightChild.setParent(this);
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public WBTNode<Key> getParent() {
        if (isNull) return new WBTNode<>(true);
        return parent;
    }

    public void setParent(WBTNode<Key> parent) {
        this.parent = parent;
    }
}
