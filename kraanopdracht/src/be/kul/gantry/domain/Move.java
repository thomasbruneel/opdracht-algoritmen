package be.kul.gantry.domain;

import java.util.List;

public class Move {
	private int gID;
	private double T;
	private int x;
	private int y;
	private Item itemInCrane;
	
	
	public Move(List<Gantry> gantries,int index,int x,int y,double extraTime){
		this.gID=gantries.get(index).getId();
		this.x=x;
		this.y=y;
		this.itemInCrane = gantries.get(index).getItemInCrane();
		//tijd berekenen
		this.T=gantries.get(index).getTime()+extraTime+Math.max(((Math.abs(gantries.get(index).getxPosition()-x))/gantries.get(index).getXSpeed()),((Math.abs(gantries.get(index).getyPostion()-y))/gantries.get(index).getYSpeed()));
		
		//update waarden huidige kraan
		gantries.get(index).setxPosition(x);
		gantries.get(index).setyPostion(y);

		//-----foreachlus?
		gantries.get(index).setTime(T);
		
		//update tijd andere kraan
		if(index==0){
			gantries.get(1).setTime(T);
		}
		else{
			gantries.get(0).setTime(T);
		}
		//-----
	}
	//exacte notatie voor wegschrijven naar csv file
	public String toString(){
		if(itemInCrane != null) return gID+";"+T+";"+x+";"+y+";"+itemInCrane.getId();
		return gID+";"+T+";"+x+";"+y+";"+"null";
	}

	public int getgID() {
		return gID;
	}

	public void setgID(int gID) {
		this.gID = gID;
	}

	public double getT() {
		return T;
	}

	public void setT(double t) {
		T = t;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Item getItemInCrane() {
		return itemInCrane;
	}

	public void setItemInCrane(Item itemInCrane) {
		this.itemInCrane = itemInCrane;
	}
}
