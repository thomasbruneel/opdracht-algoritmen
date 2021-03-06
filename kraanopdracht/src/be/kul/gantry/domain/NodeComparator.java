package be.kul.gantry.domain;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        return o1.getSlot().compareTo(o2.getSlot());
    }
}
