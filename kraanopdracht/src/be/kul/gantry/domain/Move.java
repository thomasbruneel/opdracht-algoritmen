package be.kul.gantry.domain;

public class Move {
	private int gID;
	private double T;
	private int x;
	private int y;
	private int itemInCraneID;
	
	
	public Move(Gantry g,double tijd,int xPositie,int yPositie,int itemInCraneID){
		this.gID=g.getId();
		this.x=xPositie;
		this.y=yPositie;
		this.itemInCraneID=itemInCraneID;
		
		//tijd berekenen
		
		
	}
	//exacte notatie voor wegschrijven naar csv file
	public String toString(){
		return gID+";"+T+";"+x+";"+y+";"+itemInCraneID;
		
	}

}
