package be.kul.gantry.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    //alle slots binnen bepaald interval zoeken
    public ArrayList<Slot> findOverlapping(int xMin, int xMax,int z) {
    	ArrayList<Slot> sloten= new ArrayList<>();
    	findOverlappingInterval(xMin,xMax,z,sloten,root);

    	ArrayList<Slot> sloten2= new ArrayList<>();
    	if(sloten.size()!=0){
    		for(Slot s:sloten){
    			sloten2.addAll(findOverlapping(s.getXMin(),s.getXMax(),s.getZ()));
    		}
    	}

    	for (Slot s : sloten2){
    	    if(!sloten.contains(s)) sloten.add(s);
        }

    	sloten.sort(new SlotHeightComparator());

        return sloten;
    }
    
	private void findOverlappingInterval(int xMin, int xMax, int z, List<Slot> sloten, Node n) {
		//verder boom doorlopen als xMin kleiner is dan de max waarde van huidige node
		if(xMin<n.getMaxwaarde()){
			
			if(n.getLinks()!=null){
				findOverlappingInterval(xMin, xMax, z, sloten, n.getLinks());
			}
		
			// als de xMax kleiner zou zijn dan dan dan de low waarde van de huidige node dan moeten we rechter knoop niet meer doorlopen anders wel
			if(xMax>=n.getSlot().getXMin()){
				if(n.getRechts()!=null){
					findOverlappingInterval(xMin, xMax , z, sloten, n.getRechts());
					
				}
			}
		}
		//slot toevoegen als het een item bevat en groter is dan de meegegeven z-waarde en er is overlap
		if((n.getSlot().getItem()!=null)&&(n.getSlot().getZ()>z)&&(!((xMin>=n.getSlot().getXMax())||(xMax<=n.getSlot().getXMin())))){
			sloten.add(n.getSlot());
		}
		
	}

    public Slot getEmptySlot() throws RuntimeException {

        for(int z=0; z<4; z++){
            for (Node n : nodes){
                if (n.getSlot().getZ()==z && n.getSlot().getItem()==null) return n.getSlot();
            }
        }

        throw new RuntimeException("Rij vol");
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
