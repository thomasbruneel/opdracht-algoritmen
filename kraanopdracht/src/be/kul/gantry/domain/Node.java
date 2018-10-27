package be.kul.gantry.domain;

public class Node {

    private Slot slot;
    private Node links, rechts, parent;
    private int maxwaarde;

    public Node(Slot s) {
        this.slot = s;
        this.links = null;
        this.rechts = null;
        this.parent = null;
        this.maxwaarde = Integer.MIN_VALUE;
    }


    //Getters & Setters

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Node getLinks() {
        return links;
    }

    public void setLinks(Node links) {
        this.links = links;
    }

    public Node getRechts() {
        return rechts;
    }

    public void setRechts(Node rechts) {
        this.rechts = rechts;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getMaxwaarde() {
        return maxwaarde;
    }

    public void setMaxwaarde(int maxwaarde) {
        this.maxwaarde = maxwaarde;
    }
}
