package jemgm;

import java.util.*;
import java.io.*;

/**
 * AreaDataBase.java
 *
 *
 * Created: Sun Jan  7 14:29:53 2001
 *
 * @author Andras Salamon
 * @version
 */
public class AreaDataBase  {
        
    protected Hashtable<Integer, AreaInformation> areas;     
    protected int[][]   areaNums;
    protected Game   game;

    public AreaDataBase(Game game)
    {
	this.game = game;
	areas = new Hashtable<Integer, AreaInformation>();
	if( xSize != 0 && ySize != 0 ) {
	    // we already know the size
	    areaNums = new int[xSize][ySize];
	}
    }

    public AreaDataBase(Game game, int xSize, int ySize, String mapHexFileName, String mapAreaFileName)
    {
	String line;
	BufferedReader fin;
	StringTokenizer st;

	this.game = game;
	areas = new Hashtable<Integer, AreaInformation>();	
	if( this.xSize == 0 || this.ySize == 0 ) {	    
	    this.xSize = xSize;
	    this.ySize = ySize;
	}
	areaNums = new int[this.xSize][this.ySize];

	try {
	    fin = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream( mapHexFileName ) ));
	    while( (line = fin.readLine()) != null ) {
		st = new StringTokenizer( line, "," );
		int xPos = Integer.parseInt(st.nextToken());
		int yPos = Integer.parseInt(st.nextToken());		
		int areaId = Integer.parseInt(st.nextToken());
		// ignore the other fields
		AreaInformation ai = getAreaInformation(areaId);
		if( ai == null ) {
		    ai = new AreaInformation();
		    //		    System.out.println("new ai: "+ai.xCoords.length);
		    ai.setId(areaId);
		    addHex(ai, xPos, yPos);
		    putAreaInformation(ai);
		} else {
		    addHex(ai, xPos, yPos);
		}
	    }
	} catch( FileNotFoundException e ) {
            System.out.println("FileNotFound:"+e);
	} catch( IOException e2 ) {
            System.out.println("IOException:"+e2);
	}

	try {
	    fin = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream( mapAreaFileName ) ));
	    int index=0;
	    while( (line = fin.readLine()) != null ) {
		++index;
		st = new StringTokenizer( line, " " );
		if( st.countTokens() == 17 ) {
		    // area lines
		    AreaInformation ai = getAreaInformation(index);
		    ai.setAreaType( Integer.parseInt(st.nextToken()) );
		    ai.setSupplyPointNum( Integer.parseInt(st.nextToken()) );
		    for( int i=0; i<14; ++i ) {
			ai.addNeighbour( Integer.parseInt( st.nextToken() ) );
		    }
		    ai.setName( st.nextToken() );
		} else {
		    // capital lines
		    AreaInformation ai = getAreaInformation(Integer.parseInt(st.nextToken()));
		    ai.setCapital(true);
		    // skip the name
		}
	    }
	} catch( FileNotFoundException e ) {
            System.out.println("FileNotFound:"+e);
	} catch( IOException e2 ) {
            System.out.println("IOException:"+e2);
	}
    }


    public AreaInformation getAreaInformation(int id) {
        return areas.get(new Integer(id));
    }    

    public void putAreaInformation(AreaInformation ai) {
	areas.put(new Integer(ai.getId()), new AreaInformation(ai));
	for( int i=0; i < ai.getSize(); ++i) {	    
	    areaNums[ai.getX(i+1)-1][ai.getY(i+1)-1] = ai.getId();
	}
    }

    public void addHex(AreaInformation ai, int x, int y) {
	ai.addHex(x,y);
	areaNums[x-1][y-1] = ai.getId();
    }

    public void mergeArea(Area a) {
        AreaInformation ai;
        if( (ai = getAreaInformation(a.getId())) == null ) {
            // new area
	    //	    System.out.println("new area");
	    Area aa = null;
	    try {
		aa = (Area)a.clone();
	    } catch (CloneNotSupportedException e) {
		System.out.println("Clone error:"+e);
	    }
	    ai = new AreaInformation(aa);
	    //	    System.out.println(ai.createWinAODFile());
            putAreaInformation(ai);
	} else {
            // existing area
            if( a.getAreaType() != Area.AREA_TYPE_UNKNOWN && ai.getAreaType() != a.getAreaType() ) {
		System.out.println("Invalid areType: "+ai.getId());
            }
            if( a.getSupplyPointNum() != -1 && ai.getSupplyPointNum() != a.getSupplyPointNum() ) {
		System.out.println("Invalid supplyPointNum: "+ai.getId());
            }
	    for(int i=0; i< ai.getSize(); ++i ) {
		if( (a.getX(i+1) != 0 && ai.getX(i+1) != a.getX(i+1)) ||
		    (a.getY(i+1) != 0 && ai.getY(i+1) != a.getY(i+1))) {
		    System.out.println("Invalid area position: "+ai.getId()+" "+ai.getX(i+1)+" "+a.getX(i+1));
		}
	    }
            ai.addNeighbours(a.neighbours);
	}        
    }

    public void mergeAreaInformation(AreaInformation a) {
	AreaInformation ai;
        if( (ai = getAreaInformation(a.getId())) == null ) {
            // new area
	    //	    System.out.println("new");
            putAreaInformation(a);
	} else {
            // existing area
	    //	    System.out.println("existing");
	    //            System.out.println("a.getId()"+a.getId());
	    //            System.out.println("ai.getOwner bma:"+ai.getOwner());
	    mergeArea(a);
	    //ai = getAreaInformation(a.getId());
	    //	    System.out.println("ai.getOwner ama:"+ai.getOwner());
            if( ai.getOwner() != game.getPlayerNum() 
                && a.getOwner() != game.getPlayerNum()
		&& ai.getUnitOwner() != 0 
                && a.getUnitOwner() != 0 
		&& ai.getUnitOwner() != a.getUnitOwner() ) {
		System.out.println("Invalid unitOwner: "+ai.getId());
	        System.out.println("ai.getOwner:"+ai.getOwner());
	        System.out.println("a.getOwner:"+a.getOwner());
                System.out.println("o1:"+ai.getUnitOwner()+" o2:"+a.getUnitOwner());
	    } else if( ai.getUnitOwner() == 0 ) {
		ai.setUnitOwner(a.getUnitOwner());
	    }
            if( ai.getOwner() != game.getPlayerNum() 
                && a.getOwner() != game.getPlayerNum()
		&& ai.getUnitType() != 0 
                && a.getUnitType() != 0
                && ai.getUnitType() != a.getUnitType() ) {
		System.out.println("Invalid unitType: "+ai.getId());
	    } else if( ai.getUnitType() == 0 ) {
		ai.setUnitType(a.getUnitType());
	    }
            if( game.getGameType().hasFogOfWar() && ai.getOwner() != -1 && a.getOwner() != -1
		&& ai.getOwner() != a.getOwner() ) {
		System.out.println("Invalid owner (fogofwar): "+ai.getId());
	        System.out.println("ai.getOwner:"+ai.getOwner());
	        System.out.println("a.getOwner:"+a.getOwner());
	    } else if( !game.getGameType().hasFogOfWar() && ai.getOwner() != 0 && a.getOwner() != 0
		       && ai.getOwner() != a.getOwner() ) {
		System.out.println("Invalid owner (no fogofwar): "+ai.getId());
	        System.out.println("ai.getOwner:"+ai.getOwner());
	        System.out.println("a.getOwner:"+a.getOwner());
	    } else if( game.getGameType().hasFogOfWar() && ai.getOwner() == -1 ) {
		ai.setOwner(a.getOwner());
	    } else if( !game.getGameType().hasFogOfWar() && ai.getOwner() == 0 ) {
		ai.setOwner(a.getOwner());
	    }

        }
    }

    public String createWinAODFile() {
        int size = areas.size();
	Integer keys[] = new Integer[size];
        Enumeration enumer = areas.keys();
	for( int i=0; i<size; ++i ) {
	    keys[i] = (Integer)enumer.nextElement();
	}
	for( int i=0; i<size-1; ++i ) {
	    for( int j=i+1; j<size; ++j ) {
		AreaInformation ai1 = areas.get(keys[i]);
		AreaInformation ai2 = areas.get(keys[j]);
		int actPlayerId = game.getPlayer().getNum();
		if( (ai2.getOwner() ==  actPlayerId && ai1.getOwner() != actPlayerId ) ||
		    ( ai2.getOwner() == actPlayerId && ai1.getOwner() == actPlayerId &&
		      ai2.getId() < ai1.getId() ) ||
		    ( ai2.getOwner() != actPlayerId && ai1.getOwner() != actPlayerId &&
		      ai2.getId() < ai1.getId() ) ) {
		    Integer swap = keys[i];
		    keys[i] = keys[j];
		    keys[j] = swap;
		}		    
	    }
	}
	String ret="Number of Areas =, ";
	ret +=size+"\r\n";
	for( int i=0; i<size; ++i ){
	    ret += areas.get(keys[i]).createWinAODFile(game.getPlayerNum());
	}
	return ret;
    }


    static int xSize;
    
    /**
     * Get the value of xSize.
     * @return Value of xSize.
     */
    public static int getXSize() {
         return xSize; 
    }
    
    /**
     * Set the value of xSize.
     * @param v  Value to assign to xSize.
     */
    public static void setXSize(int  v) {
        xSize = v;
    }
    

    static int ySize;
    
    /**
     * Get the value of ySize.
     * @return Value of ySize.
     */
    public static int getYSize() {
         return ySize; 
    }
    
    /**
     * Set the value of ySize.
     * @param v  Value to assign to ySize.
     */
    public static void setYSize(int  v) {
        ySize = v;
    }

    public void init (int x, int y) {
        setXSize(x);
        setYSize(y);
        System.out.println("x:"+x+" y:"+y);
        areaNums = new int[x][y];
    }

    public int getId(int x, int y) {
	if( x > 0 && y > 0 && x<=getXSize() && y<=getYSize() ) {
	    return areaNums[x-1][y-1];
	}
	return 0;
    }

    /** 
     * Finds an area which belongs to the player.
     */
    public AreaInformation getAreaInformationByOwner(Player p) {
        int pid = p.getNum();
        Enumeration enumer = areas.elements();
        AreaInformation ai = null;
	while( enumer.hasMoreElements() ) {
            ai = (AreaInformation)enumer.nextElement();
            if( ai.getOwner() == pid ) {
                return ai;
            }
	}
        return null;
    }

    public int getSupplyPointNum(Player p) {
        int pid = p.getNum();
        Enumeration enumer = areas.elements();
        AreaInformation ai = null;
        int supplyPoints = 0;
	while( enumer.hasMoreElements() ) {
            ai = (AreaInformation)enumer.nextElement();
            if( ai.getOwner() == pid ) {
                supplyPoints += ai.getSupplyPointNum();
            }
	}
        return supplyPoints;
    }

    public int getLandAreasNum(Player p) {
        int pid = p.getNum();
        Enumeration enumer = areas.elements();
        int landAreasNum = 0;
        AreaInformation ai = null;
	while( enumer.hasMoreElements() ) {
            ai = (AreaInformation)enumer.nextElement();
	    if( ai.getOwner() == pid &&
		(ai.getAreaType() == Area.AREA_TYPE_LAND ||
		 ai.getAreaType() == Area.AREA_TYPE_COSTAL) ) {
		++landAreasNum;
	    }
	}
        return landAreasNum;
    }

    public int getSeaAreasNum(Player p) {
        int pid = p.getNum();
        Enumeration enumer = areas.elements();
        int seaAreasNum = 0;
        AreaInformation ai = null;
	while( enumer.hasMoreElements() ) {
            ai = (AreaInformation)enumer.nextElement();
	    if( ai.getUnitOwner() == pid &&
		ai.getAreaType() == Area.AREA_TYPE_SEA ) {
		++seaAreasNum;
	    }
	}
        return seaAreasNum;
    }

    public int getArmyStrength(Player p) {
        int pid = p.getNum();
        Enumeration enumer = areas.elements();
        AreaInformation ai = null;
        int armyStrength = 0;
	while( enumer.hasMoreElements() ) {
            ai = (AreaInformation)enumer.nextElement();
            if( ai.getUnitOwner() == pid ) {
                armyStrength += Unit.getUnit(ai.getUnitType()).getStrength();
            }
	}
        return armyStrength;
    }

    public int getVictoryPoints(Player p) {
	int land = getLandAreasNum(p);
	int sea = getSeaAreasNum(p);
	int supply = getSupplyPointNum(p);
	return land+sea+supply;
    }


} // AreaDataBase
