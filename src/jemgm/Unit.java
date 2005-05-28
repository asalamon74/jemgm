package jemgm;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.io.IOException;

/**
 * Unit.java
 *
 *
 * Created: Sun Jan  7 13:45:25 2001
 *
 * @author Andras Salamon
 * @version
 */
public class Unit  {

    public static final Unit NONE = new Unit(0, "None", " ", true, 0, null);    
    public static final Unit ARMY = new Unit(1, "Army", "A", true, 2, "army.png");    
    public static final Unit CORPS = new Unit(2, "Corps", "C", true, 1, "corps.png");    
    public static final Unit FLEET = new Unit(3, "Fleet", "F", false, 2, "fleet.png");    
    public static final Unit SQUANDRON = new Unit(4, "Squandron", "S", false, 1, "squandron.png");

    protected static Unit[] allUnits = new Unit[5];

    protected static final String imageHome="/images/";
    
    protected Image unitImage;

    protected static int imageWidth;
    protected static boolean imageError;

    protected String abbrev;

    public String getAbbrev() {
	return abbrev;
    }

    static {
        allUnits[0] = NONE;
        allUnits[1] = ARMY;
        allUnits[2] = CORPS;
        allUnits[3] = FLEET;
        allUnits[4] = SQUANDRON;        
    }

    private Unit(int id, String name, String abbrev, boolean landUnit, int strength, String imageResource){
        setId(id);
        setName(name);
	this.abbrev = abbrev;
        setLandUnit(landUnit);
        setStrength(strength);
        if( imageResource != null ) {
            URL url  = this.getClass().getResource( imageHome+imageResource );
            unitImage =  Toolkit.getDefaultToolkit().getImage( url );
            if( imageError ) {
                unitImage = null;
            }
        }
    }

    public static Unit getUnit(int num) {
        return allUnits[num];
    }   

    public static Unit getUnit(String abbrev) {
	for( int i = 0; i < allUnits.length; ++i ) {
	    if( allUnits[i].getAbbrev().equals(abbrev) ) {
		return allUnits[i];
	    }
	}
	return NONE;
    }

    static boolean showUnitImages;
    
    public static boolean getShowUnitImages() {
        return showUnitImages; 
    }
    
    public static void setShowUnitImages(boolean  v) {
        showUnitImages = v;
    }
    
    int id;
    
    /**
     * Get the value of id.
     * @return Value of id.
     */
    public int getId() {
        return id; 
    }
    
    /**
     * Set the value of id.
     * @param v  Value to assign to id.
     */
    protected void setId(int  v) {
        this.id = v;
    }


    String name;
    
    /**
     * Get the value of name.
     * @return Value of name.
     */
    public String getName() {
        return name; 
    }
    
    /**
     * Set the value of name.
     * @param v  Value to assign to name.
     */
    protected void setName(String  v) {
        this.name = v;
    }


    boolean landUnit;
    
    /**
     * Get the value of landUnit.
     * @return Value of landUnit.
     */
    public boolean getLandUnit() {
        return landUnit; 
    }
    
    /**
     * Set the value of landUnit.
     * @param v  Value to assign to landUnit.
     */
    protected void setLandUnit(boolean  v) {
        this.landUnit = v;
    }

    int strength;
    
    /**
     * Get the value of strength.
     * @return Value of strength.
     */
    public int getStrength() {
        return strength; 
    }
    
    /**
     * Set the value of strength.
     * @param v  Value to assign to strength.
     */
    protected void setStrength(int  v) {
        this.strength = v;
    }        

    /**
     * Draws the unit.
     */
    public void draw(Graphics g, int posx, int posy, int sizex, int sizey, Color mainColor, Color lineColor1, Color lineColor2) {
        if( showUnitImages ) {
            if( !lineColor2.equals(Color.black) ) {
                g.setColor(lineColor2);
                g.fillRect( posx-1, posy-1, sizex+2, 2*sizey/3+2);
            }
            g.setColor(mainColor);
            g.fillRect( posx, posy, sizex, 2*sizey/3 );          
            imageError = false;
            boolean s = g.drawImage(unitImage, posx, posy, sizex, 2*sizey/3, null);
            synchronized(this) {
                while( !s && !imageError ) {
                    try {
                        wait(100);
                    } catch( InterruptedException e ) {                        
                        // do something
                    }                
                    s = g.drawImage(unitImage, posx, posy, null); 
                }
            }
        } else {
            if( landUnit ) {
                // land
                g.setColor(mainColor);
                g.fillRect( posx, posy, sizex, sizey );          
                g.setColor(lineColor1);
                g.drawRect( posx, posy, sizex, sizey );
            } else {
                // naval
                g.setColor(mainColor);
                g.fillOval( posx, posy, sizex, sizey );
                g.setColor(lineColor1);
                g.drawOval( posx, posy, sizex, sizey );
            }
            g.drawLine( posx, posy, posx+sizex, posy+sizey );
            if( strength > 1 ) {
                // strength 2
                g.setColor(lineColor2);
                g.drawLine( posx, posy+sizey, posx+sizex, posy );
            }
        }            
    }
    
} // Unit
