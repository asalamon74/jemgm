package jemgm;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * MapCollection
 *
 * Collection of map files. Used to store the different data and report files 
 * sent by EMG server, or other players.
 *
 */
public class MapCollection  {
    
    private ArrayList<MapDescriptor> maps;
    private Game game;
    
    public MapCollection(Game game) {
        maps = new ArrayList<MapDescriptor>();
        setLatestTurn(0);
        this.game = game;
    }
    
    public int getMapCount() {
        return maps.size();
    }
    
    public MapDescriptor getMap(int index) {
        return maps.get(index);
    }
    
    public void addMap(MapDescriptor m) {
        maps.add(m);
        if( m.getTurnNum() > getLatestTurn() ) {
            setLatestTurn(m.getTurnNum());
        }
    }
    
    public void deleteMap(int num) {
        maps.remove(num);
        if( maps.size() > 0 ) {
            //setLatestTurn(maps.get(0).getTurnNum());
            calculateLatestTurn();
        }
    }
    
    public MapDescriptor getLatestMap(Player p, int maxTurnNum) {
        int latest = -1;
        int index = -1;
        MapDescriptor m;
        for( int i=0; i<maps.size(); ++i ) {
            m = maps.get(i);
            if( m.getPlayer().equals(p) &&
                    m.getTurnNum() > latest &&
                    m.getTurnNum() <= maxTurnNum) {
                latest = m.getTurnNum();
                index = i;
            }
        }
        if( index != -1 ) {
            return maps.get(index);
        }
        return null;
    }
    
    public MapDescriptor getLatestMap(Player p) {
        return getLatestMap(p, Integer.MAX_VALUE);
    }
    
    public ArrayList<MapDescriptor> getLatestMaps(int maxTurnNum) {
        ArrayList<MapDescriptor> latestMaps = new ArrayList<MapDescriptor>();
        for( int i = 0; i<game.getPlayerNum(); ++i ) {
            Player p = game.getPlayer(i);
            MapDescriptor aodmap = getLatestMap(p, maxTurnNum);
            if( aodmap != null ) {
                latestMaps.add(aodmap);
            }
        }
        return latestMaps;
    }
    
    public ArrayList getLatestMaps() {
        return getLatestMaps(Integer.MAX_VALUE);
    }
    
    public String toString() {
        String ret="";
        for( int i = 0; i<maps.size(); ++i ) {
            ret += maps.get(i).toString()+"\n";
        }
        return ret;
    }
    
    
    private void calculateLatestTurn() {
        int newLatestTurn = Integer.MIN_VALUE;
        int actLatestTurn;
        Iterator<MapDescriptor> iter = maps.iterator();
        while (iter.hasNext()) {
            if( ( actLatestTurn = iter.next().getTurnNum() ) > newLatestTurn ) {
                newLatestTurn = actLatestTurn;
            }
        }
        setLatestTurn( newLatestTurn );
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
    
    public AreaDataBase calculateAreaDatabase(int turnNum, PlayersRelation pr) {
        return calculateAreaDatabase(turnNum, false, pr);
    }
    
    public AreaDataBase calculateAreaDatabase(int turnNum, boolean onlyMyMap, PlayersRelation pr) {
        //System.out.println("mapCollNum:"+getMapCount());
        //System.out.println("calculateAreaDatabase: "+turnNum);
        Player actPlayer = game.getPlayer();
        //System.out.println("actPlayer:"+actPlayer.getNum());
        MapDescriptor m = getLatestMap(actPlayer, turnNum);
        System.out.println("m:"+m);
        FileInputStream fis = null;
        AreaDataBase adb;
        EmgGameParameters gt = game.getGameType();
        if( gt.hasStaticMap() ) {
            adb = new AreaDataBase(game, gt.getMapSizeX(), gt.getMapSizeY(), gt.getMapHexFileName(), gt.getMapAreaFileName());
        } else {
            adb = new AreaDataBase(game);
        }
        
        //AreaInformation ai269 = adb.getAreaInformation(269);
        // my map
        boolean actual = true;
        int actturn = m.getTurnNum();
        MapProcessor.processMap(m, game, adb, pr, true, actual);
        //System.out.println("my map processed");
        //ai269 = adb.getAreaInformation(269);
        // other maps
        if( !onlyMyMap ) {
            ArrayList<MapDescriptor> v = getLatestMaps(turnNum);
            for( int i=0; i<v.size(); ++i ) {
                m = v.get(i);
                if( !m.getPlayer().equals(actPlayer) ) {
                    actual = ( actturn ==  m.getTurnNum() );
                    MapProcessor.processMap( m, game, adb, pr, false, actual);
                }
            }
        }
        return adb;
    }
    
} // AODMapCollection
