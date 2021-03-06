package be.kul.gantry.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wim on 27/04/2015.
 */
public class Gantry {

    private final int id;
    private final int xMin,xMax;
    private final int startX,startY;
    private final double xSpeed,ySpeed;
    
    //extra attributen toevoegen
    private int xPosition;
    private int yPostion;
    private double time;
    private Item itemInCrane;
    private boolean isWorking;
    
    private ArrayList<CraneState> states;

    public Gantry(int id,
                  int xMin, int xMax,
                  int startX, int startY,
                  double xSpeed, double ySpeed) {
        this.id = id;
        this.xMin = xMin;
        this.xMax = xMax;
        this.startX = startX;
        this.startY = startY;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.xPosition = startX;
        this.yPostion = startY;
        this.isWorking = false;
        this.time = 0;
        this.states=new ArrayList<CraneState>();
        this.itemInCrane = null;
        states.add(new CraneState(startX,startY,time,itemInCrane));

    }

    public int getId() {
        return id;
    }

    public int getXMax() {
        return xMax;
    }

    public int getXMin() {
        return xMin;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
    }
    
    public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPostion() {
		return yPostion;
	}

	public void setyPostion(int yPostion) {
		this.yPostion = yPostion;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

    public int getxMin() {
        return xMin;
    }

    public int getxMax() {
        return xMax;
    }

    public double getxSpeed() {
        return xSpeed;
    }

    public double getySpeed() {
        return ySpeed;
    }

    public Item getItemInCrane() {
        return itemInCrane;
    }

    public void setItemInCrane(Item itemInCrane) {
        this.itemInCrane = itemInCrane;
    }
    

    public ArrayList<CraneState> getStates() {
		return states;
	}

	public void setStates(ArrayList<CraneState> states) {
		this.states = states;
	}

	public void start(double t){
        this.isWorking=true;
        states.add(new CraneState(xPosition,yPostion,t,itemInCrane));
        time = t;
    }

    public void start(){
	    this.isWorking=true;
	    states.add(new CraneState(xPosition,yPostion,states.get(states.size()-1).getT(),itemInCrane));
    }

    public void stop(){
	    this.isWorking=false;
    }

    public boolean isWorking(){
        return isWorking;
    }

	public boolean overlapsGantryArea(Gantry g) {
        return g.xMin < xMax && xMin < g.xMax;
    }

    public int[] getOverlapArea(Gantry g) {

        int maxmin = Math.max(xMin, g.xMin);
        int minmax = Math.min(xMax, g.xMax);

        if (minmax < maxmin)
            return null;
        else
            return new int[]{maxmin, minmax};
    }

    public boolean canReachSlot(Slot s) {
        return xMin <= s.getCenterX() && s.getCenterX() <= xMax;
    }

    //nieGoe
    public void move(Slot slot, double currentTime) {
        start(currentTime); //positie voor bewegen vastleggen

        double time_past = Math.max(Math.abs(xPosition-slot.getCenterX())/xSpeed,Math.abs(yPostion-slot.getCenterY())/ySpeed);
        states.add(new CraneState(slot.getCenterX(),slot.getCenterY(),currentTime+time_past,itemInCrane));
        time = currentTime+time_past;
    }

    public void pickup(Item item, double pickupPluceDuration) {
        itemInCrane = item;
        states.add(new CraneState(xPosition,yPostion,states.get(states.size()-1).getT()+pickupPluceDuration,itemInCrane));
        time += pickupPluceDuration;
    }

    public void drop(double pickupPlaceDuration) {
        itemInCrane = null;
        time += pickupPlaceDuration;
        states.add(new CraneState(xPosition,yPostion,time, itemInCrane));

    }

    public void moveTo(Slot slot) {
        double time_past = Math.max(Math.abs(xPosition-slot.getCenterX())/xSpeed,Math.abs(yPostion-slot.getCenterY())/ySpeed);
        states.add(new CraneState(slot.getCenterX(),slot.getCenterY(),states.get(states.size()-1).getT()+time_past,itemInCrane));
        time = time+time_past;
        xPosition = slot.getCenterX();
        yPostion = slot.getCenterY();
    }

    public List<CraneState> getStates(double time) {
        List<CraneState> futureStates = new ArrayList<>();
        int counter = states.size()-1;
        while(counter > 0 && states.get(counter).getT() > time) {
            futureStates.add(0, states.get(counter));
            counter--;
        }
        futureStates.add(0, states.get(counter));

        return futureStates;
    }

    public CraneState getLastCranestate() {
        return states.get(states.size()-1);
    }

    public void moveAway(int x) {
        double timeToTravel = Math.abs(xPosition-x)/xSpeed;
        time+=timeToTravel;
        states.add(new CraneState(x,yPostion,time,itemInCrane));
        xPosition = x;
    }

    public void stayIdle(double t) {
        time=t;
        states.add(new CraneState(xPosition,yPostion,time,itemInCrane));
    }
}
