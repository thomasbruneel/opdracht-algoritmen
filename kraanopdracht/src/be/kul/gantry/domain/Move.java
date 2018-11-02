package be.kul.gantry.domain;

public class Move {
	private int gID;
	private double T;
	private int x;
	private int y;
	private Item itemInCrane;
	
	
	public Move(Gantry g,int x,int y,double extraTime){
		this.gID=g.getId();
		this.x=x;
		this.y=y;
		this.itemInCrane = g.getItemInCrane();
		//tijd berekenen
		this.T=g.getTime()+extraTime+Math.max(((Math.abs(g.getxPosition()-x))/g.getXSpeed()),((Math.abs(g.getyPostion()-y))/g.getYSpeed()));
		
		//nieuwe waarden aan kraan toekennen
		g.setxPosition(x);
		g.setyPostion(y);
		g.setTime(T);
	}
	//exacte notatie voor wegschrijven naar csv file
	public String toString(){
		if(itemInCrane != null) return gID+";"+T+";"+x+";"+y+";"+itemInCrane.getId();
		return gID+";"+T+";"+x+";"+y+";"+"null";
	}

}
