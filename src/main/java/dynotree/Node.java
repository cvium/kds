package dynotree;

/**
 * Created by clausvium on 23/12/16.
 */
public class Node<Key> {
    private Key key; // key kept in the node, must implement Comparable/Comparator
    private int size; // size of the tree rooted at this node
    private Node<Key> parent; // parent pointer
    private Node<Key> leftChild; // left child pointer
    private Node<Key> rightChild; // right child pointer

    public boolean isRoot() {
        return parent == null || (parent.getLeftChild() != this && parent.getRightChild() != this);
    }

    void update() {
        size = 1;
        if (leftChild != null) size += leftChild.getSize();
        if (rightChild != null) size += rightChild.getSize();
    }

    public void normalize() {

    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public Node<Key> getParent() {
        return parent;
    }

    public void setParent(Node<Key> parent) {
        this.parent = parent;
    }

    public Node<Key> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node<Key> leftChild) {
        this.leftChild = leftChild;
    }

    public Node<Key> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node<Key> rightChild) {
        this.rightChild = rightChild;
    }
}
