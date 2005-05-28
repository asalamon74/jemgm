package jemgm;

import java.util.Vector;

/**
 * Area.java
 *
 * Stores the static information (place, type) of an area.
 * This is known at the beginning in GD*, but has to be explored
 * in AOD.
 *
 * Created: Sun Jan  7 13:30:50 2001
 *
 * @author Andras Salamon
 * @version
 */
public class Area implements Cloneable {

    public static final int AREA_TYPE_UNKNOWN = 0;
    public static final int AREA_TYPE_LAND    = 1;
    public static final int AREA_TYPE_COSTAL  = 2;
    public static final int AREA_TYPE_SEA     = 3;

    protected int id;
    
    /**
     * Get the value of id.
     * @return Value of id.
     */
    public int getId() {
	return id;
    }
    
    /**
       * Set the value of id.
       * @param newValue  Value to assign to id.
       */
    public void setId(int  newValue) 
    {this.id = newValue;}

    protected String name;

    public void setName(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    protected boolean isCapital;

    public void setCapital(boolean isCapital) {
	this.isCapital = isCapital;
    }

    public boolean isCapital() {
	return isCapital;
    }

    protected int areaType;
    
    /**
       * Get the value of areaType.
       * @return Value of areaType.
       */
    public int getAreaType() 
    {
	return areaType;
	}
    
    /**
       * Set the value of areaType.
       * @param newValue  Value to assign to areaType.
       */
    public void setAreaType(int  newValue) 
    {
	this.areaType = newValue; 
    }

    protected Vector<Integer> xCoords;
    protected Vector<Integer> yCoords;

    protected int hexNum;

    /**
     * Counting starts from 1, for historical reasons.
     */
    public int getX(int index) {
	if( index > hexNum ) {
	    return 0;
	}
	return xCoords.elementAt(index-1);
    }

    public int getY(int index) {
	if( index > hexNum ) {
	    return 0;
	}
	return yCoords.elementAt(index-1);
    }

    public int getSize() {
	return hexNum;
    }

    public void addHex(int xc, int yc) {
	if( xc != 0 && yc != 0 ) {
	    xCoords.add( xc );
	    yCoords.add( yc );
	    ++hexNum;
	}
    }

    protected int supplyPointNum;
    
    /**
       * Get the value of supplyPointNum.
       * @return Value of supplyPointNum.
       */
    public int getSupplyPointNum() 
    {
	return supplyPointNum;
	}
    
    /**
     * Set the value of supplyPointNum.
     * @param newValue  Value to assign to supplyPointNum.
     */
    public void setSupplyPointNum(int  newValue) {
	this.supplyPointNum = newValue;
    }
    
    /** 
     * Used when creating winAOD file.
     */
    public static int AOD_MAX_NEIGHBOURS = 10;

    public Vector<Integer> neighbours;

    public Area()
    {
        neighbours = new Vector<Integer>(AOD_MAX_NEIGHBOURS);
	xCoords = new Vector<Integer>();
	yCoords = new Vector<Integer>();
	setSupplyPointNum(-1);
    }

    public void addNeighbour(int areaId) {
        if( areaId != 0 && !neighbours.contains(new Integer(areaId) ) ) {
	    neighbours.addElement(new Integer(areaId) );
	}
    }    

    public void addNeighbours(Vector areaIds) {
        for( int i=0; i<areaIds.size(); ++i ) {
	    addNeighbour(((Integer)areaIds.elementAt(i)).intValue());
	}
    }    

    public boolean isNeighbour(Area area) {
        return area != null && neighbours.contains(new Integer(area.getId()) );
    }
    public Object clone() throws CloneNotSupportedException {
	Area a = new Area();
	a.setId(getId());
	a.setAreaType(getAreaType());
	a.hexNum = hexNum;
	a.xCoords = new Vector<Integer>(xCoords);
	a.yCoords = new Vector<Integer>(yCoords);
	a.setSupplyPointNum(getSupplyPointNum());
	a.neighbours = new Vector<Integer>(neighbours);
	return a;
    }
} // Area
