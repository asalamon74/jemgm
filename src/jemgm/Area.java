package jemgm;

import java.util.ArrayList;

/**
 * Area.java
 *
 * Stores the static information (place, type) of an area, which never
 * changes during the game.
 *
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
    public void setId(int  newValue) {
        this.id = newValue;}
    
    protected String name;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    protected boolean capital;
    
    public void setCapital(boolean isCapital) {
        this.capital = isCapital;
    }
    
    public boolean isCapital() {
        return capital;
    }
    
    protected int areaType;
    
    /**
     * Get the value of areaType.
     * @return Value of areaType.
     */
    public int getAreaType() {
        return areaType;
    }
    
    /**
     * Set the value of areaType.
     * @param newValue  Value to assign to areaType.
     */
    public void setAreaType(int  newValue) {
        this.areaType = newValue;
    }
    
    protected ArrayList<Integer> xCoords;
    protected ArrayList<Integer> yCoords;
    
    protected int hexNum;
    
    /**
     * Counting starts from 1, for historical reasons.
     */
    public int getX(int index) {
        if( index > hexNum ) {
            return 0;
        }
        return xCoords.get(index-1);
    }
    
    public int getY(int index) {
        if( index > hexNum ) {
            return 0;
        }
        return yCoords.get(index-1);
    }
    
    public int getSize() {
        return hexNum;
    }
    
    public void addHex(int xCoord, int yCoord) {
        if( xCoord != 0 && yCoord != 0 ) {
            xCoords.add( xCoord );
            yCoords.add( yCoord );
            ++hexNum;
        }
    }
    
    protected int supplyPointNum;
    
    /**
     * Get the value of supplyPointNum.
     * @return Value of supplyPointNum.
     */
    public int getSupplyPointNum() {
        return supplyPointNum;
    }
    
    /**
     * Set the value of supplyPointNum.
     * @param newValue  Value to assign to supplyPointNum.
     */
    public final void setSupplyPointNum(int  newValue) {
        this.supplyPointNum = newValue;
    }
    
    /**
     * Used when creating winAOD file.
     */
    public final static int AOD_MAX_NEIGHBOURS = 10;
    
    public ArrayList<Integer> neighbours;
    
    public Area() {
        neighbours = new ArrayList<Integer>(AOD_MAX_NEIGHBOURS);
        xCoords = new ArrayList<Integer>();
        yCoords = new ArrayList<Integer>();
        setSupplyPointNum(-1);
    }
    
    public void addNeighbour(int areaId) {
        if( areaId != 0 && !neighbours.contains(new Integer(areaId) ) ) {
            neighbours.add(new Integer(areaId) );
        }
    }
    
    public void addNeighbours(ArrayList areaIds) {
        for( int i=0; i<areaIds.size(); ++i ) {
            addNeighbour(((Integer)areaIds.get(i)).intValue());
        }
    }
    
    public boolean isNeighbour(Area area) {
        return area != null && neighbours.contains(new Integer(area.getId()) );
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Area area = new Area();
        area.setId(getId());
        area.setAreaType(getAreaType());
        area.hexNum = hexNum;
        area.xCoords = new ArrayList<Integer>(xCoords);
        area.yCoords = new ArrayList<Integer>(yCoords);
        area.setSupplyPointNum(getSupplyPointNum());
        area.neighbours = new ArrayList<Integer>(neighbours);
        return area;
    }
} // Area
