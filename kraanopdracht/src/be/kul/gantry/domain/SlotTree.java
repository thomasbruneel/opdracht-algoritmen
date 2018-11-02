package be.kul.gantry.domain;

import java.util.ArrayList;
import java.util.List;

public class SlotTree {

    private Node root;
    private List<Node> nodes;

    public SlotTree() {
        this.nodes = new ArrayList<>();
        this.root = null;
    }

    public void addNode(Slot slot){
        nodes.add(new Node(slot));
    }

    public void constructTree(){
        nodes.sort(new NodeComparator());
        root = constructTree(nodes, null);
        int treeMaxRange = root.berekenmaxRange();
    }

    private Node constructTree(List<Node> nodez, Node parent) {
        int half = nodez.size()/2;
        Node node = nodez.get(half);
        node.setParent(parent);
        List<Node> links = nodez.subList(0,half);
        List<Node> rechts = nodez.subList(half+1,nodez.size());
        if(!links.isEmpty()) node.setLinks(constructTree(links, node));
        if(!rechts.isEmpty()) node.setRechts(constructTree(rechts, node));
        return node;
    }


    //Getters & Setters

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
