package jemgm;

/**
 * Turn.java
 *
 *
 * Created: Fri Feb 15 20:39:49 2002
 *
 * @author Salamon Andras
 * @version
 */

public class Turn  {
    
    public Turn(Game game, int turnNum) {
	this.game = game;
        this.turnNum = turnNum;
    }

    protected Game game;
    protected int turnNum;
    
    /**
     * Get the value of turnNum.
     * @return Value of turnNum.
     */
    public int getTurnNum() {
        return turnNum;
    }
    
    /**
     * Set the value of turnNum.
     * @param newValue  Value to assign to turnNum.
     */
    public void setTurnNum(int  newValue) {
        this.turnNum = newValue;
    }


    AreaDataBase areadb;
    
    /**
     * Get the value of areadb.
     * @return Value of areadb.
     */
    public AreaDataBase getAreadb() {
	if( areadb == null ) {
        System.out.println("pr:"+pr);
	    setAreadb(game.mapCollectionProcess(turnNum, pr));
	}
        return areadb;
    }
    
    /**
     * Set the value of areadb.
     * @param v  Value to assign to areadb.
     */
    public void setAreadb(AreaDataBase  v) {
        this.areadb = v;
    }
    
    CommandCollection cc;
    
    /**
     * Get the value of cc.
     * @return Value of cc.
     */
    public CommandCollection getCc() {
	if( cc == null ) {
	    cc = CommandCollection.readFromFile(game, turnNum);
	}
        return cc; 
    }
    
    /**
     * Set the value of cc.
     * @param v  Value to assign to cc.
     */
    public void setCc(CommandCollection  v) {
        this.cc = v;
    }

    PlayersRelation pr;
    
    public PlayersRelation getPr() {
         return pr; 
    }
    
    public void setPr(PlayersRelation  v) {
        this.pr = v;
    }


    /**
     * Validates the command collection.
     *
     * @return Array of error messages.
     */
    public String[] validate() {
        // TODO: implement
        //        return null;
        String []errors = {"Test error1", "Very big error"};
        return errors;
    }
    
} // Turn
