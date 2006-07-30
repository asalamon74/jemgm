package jemgm;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Descriptor of a map, sent by the server or another players.
 *
 */
public class MapDescriptor  {
    
    public MapDescriptor(int turnNum, String fileName, Player p) {
        setTurnNum(turnNum);
        setFileName(fileName);
        setPlayer(p);
    }
    
    int turnNum;
    
    /**
     * Get the value of turnNum.
     * @return Value of turnNum.
     */
    public int getTurnNum() {
        return turnNum;
    }
    
    /**
     * Set the value of turnNum.
     * @param v  Value to assign to turnNum.
     */
    public void setTurnNum(int  v) {
        this.turnNum = v;
    }
    
    
    String fileName;
    
    /**
     * Get the value of fileName.
     * @return Value of fileName.
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Set the value of fileName.
     * @param v  Value to assign to fileName.
     */
    public void setFileName(String  v) {
        this.fileName = v;
    }
    
    
    Player player;
    
    /**
     * Get the value of player.
     * @return Value of player.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Set the value of player.
     * @param v  Value to assign to player.
     */
    public void setPlayer(Player  v) {
        this.player = v;
    }
    
    public String toString() {
        Player p = getPlayer();
        return getTurnNum() + " : " +getFileName() + " " + (p == null ? "" : ""+p.getName().charAt(0));
    }
            
} // MapDescriptor
