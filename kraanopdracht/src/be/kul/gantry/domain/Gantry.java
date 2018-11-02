package be.kul.gantry.domain;

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
}
