package jemgm;

import java.util.Hashtable;
/**
 * Player.java
 *
 *
 * Created: Tue Feb 12 22:35:05 2002
 *
 * @author Salamon Andras
 * @version
 */
public class Player  {
    

    public Player(String abbrev, String name, int num) {
        setAbbrev(abbrev);
        setName(name);
        setNum(num);
    }

    protected String name;
    
    /**
       * Get the value of name.
       * @return Value of name.
       */
    public String getName() {
        return name;
    }
    
    /**
     * Set the value of name.
     * @param newValue  Value to assign to name.
     */
    public void setName(String  newValue) {
        this.name = newValue;
    }


    protected String abbrev;
    
    /**
     * Get the value of abbrev.
     * @return Value of abbrev.
     */
    public String getAbbrev() {
            return abbrev;
    }
    
    /**
     * Set the value of abbrev.
     * @param newValue  Value to assign to abbrev.
     */
    public void setAbbrev(String  newValue) {
            this.abbrev = newValue;
    }
    

    protected int num;
    
    /** Holds value of property email. */
    private String email;
    
    /**
     * Get the value of num.
     * @return Value of num.
     */
    public int getNum() {
        return num;
    }
    
    /**
     * Set the value of num.
     * @param newValue  Value to assign to num.
     */
    public void setNum(int  newValue) {
        this.num = newValue;
    }        

    public boolean equals(Object p) {
        return (p instanceof Player &&
                ((Player)p).getNum() == getNum());
    }
    
    /** Getter for property email.
     * @return Value of property email.
     *
     */
    public String getEmail() {
        return this.email;
    }
    
    /** Setter for property email.
     * @param email New value of property email.
     *
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
} // Player
