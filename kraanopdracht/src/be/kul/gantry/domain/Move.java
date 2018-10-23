package be.kul.gantry.domain;

public class Move {
	private int gID;
	private double T;
	private int x;
	private int y;
	private int itemInCraneID;
	
	
	public Move(Gantry g,int x,int y,int itemInCraneID){
		this.gID=g.getId();
		this.x=x;
		this.y=y;
		this.itemInCraneID=itemInCraneID;
		
		//tijd berekenen
		this.T=g.getTime()+((Math.abs(g.getxPosition()-x))/g.getXSpeed())+((Math.abs(g.getyPostion()-y))/g.getYSpeed());
		
		//nieuwe waarden aan kraan toekennen
		g.setxPosition(x);
		g.setyPostion(y);
		g.setTime(T);
	}
	//exacte notatie voor wegschrijven naar csv file
	public String toString(){
		return gID+";"+T+";"+x+";"+y+";"+itemInCraneID;
		
	}

}
