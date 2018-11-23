package be.kul.gantry.domain;

public class CraneState {
	
	private int x;
	private int y;
	private int t;
	private int itemId;
	
	

	public CraneState(int x, int y, int t, int itemId) {
		this.x = x;
		this.y = y;
		this.t = t;
		this.itemId = itemId;
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
	public int getT() {
		return t;
	}
	public void setT(int t) {
		this.t = t;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	
	
	

}
