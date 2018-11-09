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
		gantries.get(index).setTime(T);
		
		//update tijd andere kraan
		if(index==0){
			gantries.get(1).setTime(T);
		}
		else{
			gantries.get(0).setTime(T);
		}
	}
	//exacte notatie voor wegschrijven naar csv file
	public String toString(){
		if(itemInCrane != null) return gID+";"+T+";"+x+";"+y+";"+itemInCrane.getId();
		return gID+";"+T+";"+x+";"+y+";"+"null";
	}

}
