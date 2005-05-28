package jemgm;

import java.awt.Color;

/**
 * Command.java
 *
 *
 * Created: Sat Mar  2 13:47:54 2002
 *
 * @author Salamon Andras
 * @version
 */
public class Command  {

    public Command(Game game, CommandType type) {
        this.type   = type;
        this.params = new String[0];
	this.game = game;
    }

    public Command(Game game, CommandType type, int[] params) {
        this.type   = type;
        this.params = new String[params.length];
        for( int i=0; i<params.length; ++i ) {
            this.params[i] = ""+params[i];
        }
	this.game = game;
    }

    public Command(Game game, CommandType type, int[] params, int length) {
        this.type   = type;
        this.params = new String[length];
        for( int i=0; i<length; ++i ) {
            this.params[i] = ""+params[i];
        }
	this.game = game;
    }

    public Command(Game game, CommandType type, String[] params) {
        this.type   = type;
        this.params = new String[params.length];
        for( int i=0; i<params.length; ++i ) {
            this.params[i] = params[i];
        }
	this.game = game;
    }

    public Command(Game game, CommandType type, int param) {
        this.type = type;
        this.params = new String[1];
        this.params[0] = ""+param;
	this.game = game;
    }

    public Command(Game game, CommandType type, int param1, int param2) {
        this.type = type;
        this.params = new String[2];
        this.params[0] = ""+param1;
        this.params[1] = ""+param2;
	this.game = game;
    }

    public Command(Game game, CommandType type, int param1, String param2) {
        this.type = type;
        this.params = new String[2];
        this.params[0] = ""+param1;
        this.params[1] = param2;
	this.game = game;
    }

    public String toString() {
        String ret = type.abbrev;
        for( int i=0; i<params.length && !params[i].equals("") && !params[i].equals("0"); ++i ) {
            if( type.equals(CommandType.SP) && params[i].length() > 4 ) {
                String param = params[i].substring(1);
                ret += " " + param;
            } else {
                ret += " "+params[i];
            }
        }
        return ret;
    }

    public String toHumanReadableString() {
        String ret = type.abbrev;
	String param="";
        for( int i=0; i<params.length && !params[i].equals("") && !params[i].equals("0"); ++i ) {
            if( type.equals(CommandType.SP) && params[i].length() > 4 ) {
                param = params[i].substring(1);
            } else if(type.equals(CommandType.DA) || 
		      type.equals(CommandType.DN) || 
		      type.equals(CommandType.DW) || 
		      type.equals(CommandType.CA) ||
		      (type.equals(CommandType.SA) && i == 2) ||
		      (type.equals(CommandType.SD) && i == 2)) {
		param = params[i] + " [ " +game.getPlayer(getIntParam(i)).getName() + " ]";
	    }else {
		param = params[i];
            }
	    ret += " " + param;
        }
        return ret;
    }

    public int getIntParam(int index) {
        return new Integer(params[index]).intValue();
    }

    public String getParam(int index) {
        return params[index];
    }

    public void removeParam(String param) {
	int index=-1;
	for( int i=0; i<params.length; ++i ) {
	    if( params[i].equals(param) ) {
		index = i;
	    }
	}
	if( index != -1 ) {
	    removeParamByIndex(index);
	}
    }

    public void removeParamByIndex(int index) {
	String []tmpParams = new String[params.length];
	System.arraycopy(params, 0, tmpParams, 0, params.length);
	params = new String[params.length-1];
	for( int i=0; i<index; ++i ) {
	    params[i] = tmpParams[i];
	}
	for( int i=index; i<params.length; ++i ) {
	    params[i] = tmpParams[i+1];
	}
    }

    public int getParamNum() {
        return params.length;
    }

    public CommandType getType() {
        return type;
    }

    public boolean equals(Object obj) {        
        if( !(obj instanceof Command) ) {
            return false;
        }
        Command objCommand = (Command)obj;
        if( type != objCommand.type || params.length != objCommand.params.length ) {
            return false;
        }
        for( int i=0; i<params.length; ++i ) {
            if( !params[i].equals( objCommand.params[i]) ) {
                return false;
            }
        }
        return true;
    }

    private CommandType   type;
    private String[] params; 
    private Game game;
    
} // Commands
