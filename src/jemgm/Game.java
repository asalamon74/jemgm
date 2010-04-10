package jemgm;

import java.util.*;
import java.io.*;

/**
 * Game.java
 *
 *
 * Created: Sat Feb 23 14:19:41 2002
 *
 * @author Salamon Andras
 * @version
 */
public class Game  {
    
    public Game() {
        mapcoll = new MapCollection(this);
        players = new Vector<Player>();
    }
    
    /** 
     * Loads a game from a .emg file.
     */
    public Game(String fileName) {
        this();
        try {
            BufferedReader fin = new BufferedReader(new FileReader(fileName));
            String line;
            while( (line = fin.readLine()) != null ) {
                System.out.println("line:"+line);
                if( line.trim().startsWith("//") ) {
                    // commented line, ignore
                } else if( line.startsWith("GameType = ") ) {
                    setGameType(line.substring("GameType = ".length()));
                } else if( line.startsWith("Game = ") ) {
                    setGameId(line.substring("Game = ".length()));
                } else if( line.startsWith("Player = ") ) {
                    setPlayer(getPlayer(new Integer(line.substring("Player = ".length())).intValue()));
                    System.out.println("player:"+getPlayer().getNum());
                } else if( line.startsWith("Code = ") ) {
                    setCode(line.substring("Code = ".length()));
                } else if( line.startsWith("Email") ) {
                    StringTokenizer st = new StringTokenizer(line, ",");
                    st.nextToken();
                    int num = Integer.parseInt(st.nextToken().trim());
                    String abbrev = st.nextToken().trim();
                    String name = st.nextToken().trim();
                    String email = st.nextToken().trim();
                    if( num == -1 ) {
                        //System.out.println("bot e-mail:"+email);
                        setBotEmail(email);
                        // add the neutral player
                        addPlayer(new Player("NTRL", "Neutral", 0));
                    } else {
                        Player p =  new Player(abbrev, name, num);
                        p.setEmail(email);
                        addPlayer(p);
                    }
                } else {
                    // the maps
                    StringTokenizer st = new StringTokenizer(line, " :");
                    int turn = Integer.parseInt(st.nextToken());
                    String mapFileName = st.nextToken();
                    Player mapPlayer = getPlayer(st.nextToken());
                    MapDescriptor aodmap = new MapDescriptor(turn, mapFileName, mapPlayer);
                    mapcoll.addMap(aodmap);
                }
                
            }
        } catch( FileNotFoundException e ) {
            System.out.println("FileNotFound:"+e);
        } catch( IOException e2 ) {
            System.out.println("IOException:"+e2);
        }
    }
    
    MapCollection mapcoll;
    
    /**
     * Get the value of mapcoll.
     * @return Value of mapcoll.
     */
    public MapCollection getMapcoll() {
        return mapcoll;
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
    
    Vector<Player> players;
    
    public void addPlayer(Player p) {
        players.add(p);
    }
    
    public int getPlayerNum() {
        return players.size();
    }
    
    public Player getPlayer(int num) {
        int index=0;
        while (index < getPlayerNum() && players.elementAt(index).getNum() != num ) {
            ++index;
        }
        
        if( index < getPlayerNum() ) {
            return players.elementAt(index);
        }
        return null;
    }
    
    /**
     * Finds the player by name or abbrev.
     */
    public Player getPlayer(String name) {
        int index=0;
        while (index < getPlayerNum() &&
                !players.elementAt(index).getName().regionMatches(true, 0, name, 0, 1) ) {
            ++index;
        }
        
        if( index < getPlayerNum() ) {
            return players.elementAt(index);
        }
        return null;
    }
    
    
    String code;
    
    /**
     * Get the value of code.
     * @return Value of code.
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Set the value of code.
     * @param v  Value to assign to code.
     */
    public void setCode(String  v) {
        this.code = v;
    }
    
    EmgGameParameters gameType;
    
    public EmgGameParameters getGameType() {
        return gameType;
    }
    
    public void setGameType( String gameTypeStr ) {
        setGameType( new EmgGameParameters(gameTypeStr) );
    }
    
    public void setGameType( EmgGameParameters gameType ) {
        this.gameType = gameType;
        CommandType.initGameDependentCommandTypes(gameType);
    }
    
    String gameId;
    
    /**
     * Get the value of gameId.
     * @return Value of gameId.
     */
    public String getGameId() {
        return gameId;
    }
    
    /**
     * Set the value of gameId.
     * @param v  Value to assign to gameId.
     */
    public void setGameId(String  v) {
        this.gameId = v;
    }

    @Override
    public String toString() {
        // header
        String ret =
                "GameType = "+getGameType().getAbbrev()+"\n"+
                "Game = "+getGameId()+"\n"+
                "Code = "+getCode()+"\n";
        // player emails
        Player p;
        for( int i=1; i<getPlayerNum(); ++i ) {
            p = getPlayer(i);
            if( p.getEmail() != null && !p.getEmail().equals("") ) {
                ret += "Email, "+ p.getNum() + ", "+p.getAbbrev()+", "+p.getName()+", "+p.getEmail()+"\n";
            }
        }
        // bot email address
        ret += "Email, -1, Bot, Bot, "+getBotEmail()+"\n";
        ret += "Player = "+getPlayer().getNum()+"\n";
        
        // map collection
        ret += mapcoll.toString();
        return ret;
    }
    
    
    String directory;
    
    /**
     * Get the value of directory.
     * @return Value of directory.
     */
    public String getDirectory() {
        return directory;
    }
    
    /**
     * Set the value of directory.
     * @param v  Value to assign to directory.
     */
    public void setDirectory(String  v) {
        this.directory = v;
    }
    
    
    String file;
    
    /**
     * Get the value of file.
     * @return Value of file.
     */
    public String getFile() {
        return file;
    }
    
    /**
     * Set the value of file.
     * @param v  Value to assign to file.
     */
    public void setFile(String  v) {
        this.file = v;
    }
    
    private Hashtable<Integer,Turn> turns = new Hashtable<Integer,Turn>();
    
    public Turn getTurn(int turnNumber) {
        System.out.println("getTurn: "+turnNumber);
        Turn t = (Turn)turns.get(new Integer(turnNumber));
// 	if ( t == null ) {
// 	    t = new Turn(this, turnNumber);
// 	    setTurn(turnNumber, t);
// 	}
        return t;
    }
    
    public void setTurn(int turnNumber, Turn turn) {
        System.out.println("setTurn: "+turnNumber);
        turns.put(new Integer(turnNumber), turn);
    }
    
    public void removeTurnsAfter(int turnNumber) {
        Integer key;
        for (Enumeration e = turns.keys(); e.hasMoreElements() ;) {
            key = (Integer)e.nextElement();
            if( key.intValue() >= turnNumber ) {
                turns.remove(key);
            }
        }
        
    }
    
    int shiftX;
    
    /**
     * Get the value of shiftX.
     * @return Value of shiftX.
     */
    public int getShiftX() {
        return shiftX;
    }
    
    /**
     * Set the value of shiftX.
     * @param v  Value to assign to shiftX.
     */
    public void setShiftX(int  v) {
        this.shiftX = v;
    }
    
    
    int shiftY;
    
    /** Holds value of property botEmail. */
    private String botEmail;
    
    /**
     * Get the value of shiftY.
     * @return Value of shiftY.
     */
    public int getShiftY() {
        return shiftY;
    }
    
    /**
     * Set the value of shiftY.
     * @param v  Value to assign to shiftY.
     */
    public void setShiftY(int  v) {
        this.shiftY = v;
    }
    
    /** Getter for property botEmail.
     * @return Value of property botEmail.
     *
     */
    public String getBotEmail() {
        return this.botEmail;
    }
    
    /** Setter for property botEmail.
     * @param botEmail New value of property botEmail.
     *
     */
    public void setBotEmail(String botEmail) {
        this.botEmail = botEmail;
    }
    
    public AreaDataBase mapCollectionProcess(int turnNum, PlayersRelation pr) {
        return mapcoll.calculateAreaDatabase(turnNum, pr);
    }
} // AODGame
