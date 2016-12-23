package dynotree;

/**
 * Created by clausvium on 23/12/16.
 */
public class Node<Key> {
    Key key;
    Node parent;
    Node leftChild;
    Node rightChild;

    public boolean isRoot() {
        return parent == null || (parent.getLeftChild() != this && parent.getRightChild() != this);
    }

    public Node(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }
}
