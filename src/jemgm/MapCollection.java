package jemgm;

import java.util.Vector;
import java.io.*;

/**
 * AODMapCollection.java
 *
 *
 * Created: Fri Feb 15 20:57:14 2002
 *
 * @author Salamon Andras
 * @version
 */
public class MapCollection  {

    private Vector<Map> maps; 
    private Game game;

    public MapCollection(Game game) {
        maps = new Vector<Map>();
        setLatestTurn(0);
	this.game = game;
    }

    public int getMapCount() {
        return maps.size();
    }

    public Map getMap(int index) {
        return maps.elementAt(index);
    }

    public void addMap(Map m) {
        maps.addElement(m);
        sort();
        if( m.getTurnNum() > getLatestTurn() ) {
            setLatestTurn(m.getTurnNum());
        }
    }

    public void deleteMap(int num) {
        maps.removeElementAt(num);
        if( maps.size() > 0 ) {
            setLatestTurn(maps.elementAt(0).getTurnNum());
        }
    }

    public Map getLatestMap(Player p, int maxTurnNum) {
        int latest = -1;
        int index = -1;
        Map m;
        for( int i=0; i<maps.size(); ++i ) {
            m = maps.elementAt(i);
            if( m.getPlayer().equals(p) && 
                m.getTurnNum() > latest &&
                m.getTurnNum() <= maxTurnNum) {
                latest = m.getTurnNum();
                index = i;
            }
        }
        if( index != -1 ) {
            return maps.elementAt(index);
        }
        return null;
    }

    public Map getLatestMap(Player p) {
        return getLatestMap(p, Integer.MAX_VALUE);
    }

    public Vector<Map> getLatestMaps(int maxTurnNum) {
        Vector<Map> latestMaps = new Vector<Map>();
        for( int i = 0; i<game.getPlayerNum(); ++i ) {
            Player p = game.getPlayer(i);
            Map aodmap = getLatestMap(p, maxTurnNum);
            if( aodmap != null ) {
                latestMaps.addElement(aodmap);
            }
        }
        return latestMaps;
    }

    public Vector getLatestMaps() {
        return getLatestMaps(Integer.MAX_VALUE);
    }

    public String toString() {
        String ret="";
        for( int i = 0; i<maps.size(); ++i ) {
            ret += maps.elementAt(i).toString()+"\n";
        }
        return ret;
    }


    private void sort() {
        int size = maps.size();
        Map lastMap = maps.elementAt(size-1);
        int lastTurn = lastMap.getTurnNum();
        int index = size-2;
        while( index >= 0 && lastTurn > maps.elementAt(index).getTurnNum()) {
            --index;
        }
        if( index < size -2 ) {
            Map swap = lastMap;
            maps.removeElementAt(size-1);
            maps.insertElementAt(swap, index+1);
        }
            
    }

    int latestTurn;
    
    /**
     * Get the value of latestTurn.
     * @return Value of latestTurn.
     */
    public int getLatestTurn() {
         return latestTurn; 
    }
    
    /**
     * Set the value of latestTurn.
     * @param v  Value to assign to latestTurn.
     */
    private void setLatestTurn(int  v) {
        this.latestTurn = v;
    }

    public AreaDataBase calculateAreaDatabase(int turnNum) {
	return calculateAreaDatabase(turnNum, false);
    }

    public AreaDataBase calculateAreaDatabase(int turnNum, boolean onlyMyMap) {
	System.out.println("mapCollNum:"+getMapCount());
	System.out.println("calculateAreaDatabase: "+turnNum);
        Player actPlayer = game.getPlayer();
	System.out.println("actPlayer:"+actPlayer.getNum());
        Map m = getLatestMap(actPlayer, turnNum);
	System.out.println("m:"+m);
        FileInputStream fis = null;
	AreaDataBase adb;
	EmgGameParameters gt = game.getGameType();
	if( gt.hasStaticMap() ) {
	    adb = new AreaDataBase(game, gt.getMapSizeX(), gt.getMapSizeY(), gt.getMapHexFileName(), gt.getMapAreaFileName());
	} else {
	    adb = new AreaDataBase(game);
	}

 	AreaInformation ai269 = adb.getAreaInformation(269);
        // my map
        boolean actual = true;
        int actturn = m.getTurnNum();
	m.processMap(game, adb, true, actual);
	System.out.println("my map processed");
	ai269 = adb.getAreaInformation(269);
        // other maps
	if( !onlyMyMap ) {
	    Vector<Map> v = getLatestMaps(turnNum);
	    for( int i=0; i<v.size(); ++i ) {
		m = v.elementAt(i);
		if( !m.getPlayer().equals(actPlayer) ) {
		    actual = ( actturn ==  m.getTurnNum() );
		    m.processMap(game, adb, false, actual);
		}
	    }        
	}
        return adb;
    }
    
} // AODMapCollection
