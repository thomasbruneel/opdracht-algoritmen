package be.kul.gantry.domain;

public class CraneState {
	
	private int x;
	private int y;
	private double t;
	private Item item;

	public CraneState(int x, int y, double t, Item item) {
		this.x = x;
		this.y = y;
		this.t = t;
		this.item = item;
	}
	
	public CraneState(CraneState cs){
		this.x=cs.x;
		this.y=cs.y;
		this.t=cs.t;
		this.item=cs.item;
		
	}

	//getters en setters
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
	public double getT() {
		return t;
	}
	public void setT(double t) {
		this.t = t;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item itemId) {
		this.item = itemId;
	}
	
	
	
	

}
