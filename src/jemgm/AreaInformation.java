package jemgm;

import java.util.Vector;

/**
 * AreaInformation.java
 *
 * Stores the non-static information of an area (unit, unit owner,...)
 *
 * Created: Sun Jan  7 13:43:40 2001
 *
 * @author Andras Salamon
 * @version
 */
public class AreaInformation extends Area {
    
    
    protected int owner;
    
    /**
     * Get the value of owner.
     * @return Value of owner.
     */
    public int getOwner() {
        return owner;
    }
    
    /**
     * Set the value of owner.
     * @param newValue  Value to assign to owner.
     */
    public void setOwner(int  newValue) {
        this.owner = newValue;
    }
    
    private int prevOwner;
    
    protected int unitOwner;
    
    /**
     * Get the value of unitOwner.
     * @return Value of unitOwner.
     */
    public int getUnitOwner() {
        return unitOwner;
    }
    
    /**
     * Set the value of unitOwner.
     * @param newValue  Value to assign to unitOwner.
     */
    public void setUnitOwner(int  newValue) {
        this.unitOwner = newValue;
    }
    
    
    
    protected int unitType;
    
    /**
     * Get the value of unitType.
     * @return Value of unitType.
     */
    public int getUnitType() {
        return unitType;
    }
    
    /**
     * Set the value of unitType.
     * @param newValue  Value to assign to unitType.
     */
    public void setUnitType(int  newValue) {
        this.unitType = newValue;
    }
    
    
    public AreaInformation() {
        super();
    }
    
    public AreaInformation(Area a) {
        super();
        setId(a.getId());
        setAreaType(a.getAreaType());
        hexNum = a.hexNum;
        xCoords = new Vector<Integer>(a.xCoords);
        yCoords = new Vector<Integer>(a.yCoords);
        setSupplyPointNum(a.getSupplyPointNum());
        neighbours = new Vector<Integer>(a.neighbours);
        setOwner(-1);        
    }
    
    public AreaInformation(AreaInformation a) {
        setId(a.getId());
        setAreaType(a.getAreaType());
        hexNum = a.hexNum;
        xCoords = new Vector<Integer>(a.xCoords);
        yCoords = new Vector<Integer>(a.yCoords);
        setSupplyPointNum(a.getSupplyPointNum());
        neighbours = new Vector<Integer>(a.neighbours);
        setOwner(a.getOwner());
        setPrevOwner(a.getPrevOwner());
        setUnitOwner(a.getUnitOwner());
        setUnitType(a.getUnitType());
    }
    
    public String createWinAODFile(int unknownNumber) {
        int owner = getOwner() >= 0 ? getOwner() : unknownNumber;
        
        String ret = getId() + " " + owner + " " + getAreaType() + " " +
                getSupplyPointNum() + " ";
        for( int i=0; i<neighbours.size(); ++i ) {
            ret += neighbours.elementAt(i) + " ";
        }
        int j = neighbours.size();
        while( j < Area.AOD_MAX_NEIGHBOURS ) {
            ret += "0 ";
            ++j;
        }
        ret += getX(1) + " " + getX(2) + " " + getX(3) + " " +
                getY(1) + " " + getY(2) + " " + getY(3) + " " + getUnitOwner() + " "+
                getUnitType() + "\r\n";
        return ret;
    }

    public int getPrevOwner() {
        return prevOwner;
    }

    public void setPrevOwner(int prevOwner) {
        this.prevOwner = prevOwner;
    }
    
} // AreaInformation
