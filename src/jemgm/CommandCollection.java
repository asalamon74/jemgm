package jemgm;

import java.util.*;
import java.io.*;

/**
 * CommandCollection.java
 *
 *
 * Created: Sat Mar  2 13:57:19 2002
 *
 * @author Salamon Andras
 * @version
 */

public class CommandCollection  {
    
    private Vector<Command> commands;
    private Game game;
    
    public CommandCollection(Game game) {
	this.game = game;
        commands = new Vector<Command>();
    }

    public void removeCommand(int index) {
        commands.removeElementAt(index);
    }

    public void removeCommand(Command c) {
	commands.removeElement(c);
    }

    public void addCommand(Command c) {
        if( (c.getType().flags & CommandType.COMMAND_TYPE_UNIQUE_SOURCE) > 0) {
            // we have the delete the old command
            int index = getCommandIndexById(c.getIntParam(0));
            if( index != -1 ) {
                removeCommand(index);
            }
        } else if( (c.getType().flags & CommandType.COMMAND_TYPE_UNIQUE) > 0 ) {
            int index = getCommandIndexByType(c.getType());
            if( index != -1 ) {
                removeCommand(index);
            }
        }
        commands.addElement(c);
    }

    public static CommandCollection readFromFile(Game game, int turnNum) {
        try {
	    BufferedReader fin = new BufferedReader(new FileReader(game.getDirectory()+"commands_"+turnNum+".dat"));
	    String line;
	    CommandCollection cc = new CommandCollection(game);
	    while( (line = fin.readLine()) != null ) {		
		if( line.trim().length() == 0 ) {
		    continue;
		}
		if( line.equals("EMGSTART") ) {
		    // here starts the file, ignore the next two lines
		    fin.readLine();
		    fin.readLine();
		} else if( line.equals("EMGEND") ) {
		    // end of file
		    break;
		} else {
		    StringTokenizer st = new StringTokenizer(line, " ");
		    int countTokens = st.countTokens();
		    String []params = new String[countTokens-1];
		    String command = st.nextToken();
		    int index=0;
		    while (st.hasMoreTokens()) {
			params[index++] = st.nextToken();
		    }
		    CommandType ct = CommandType.findCommandType(command);
		    cc.addCommand(new Command(game, ct, params));
		}
	    }
	    return cc;
        } catch( FileNotFoundException e ) {
            System.out.println("Exception : "+e);
            return null;
        } catch( IOException e ) {
            System.out.println("Exception : "+e);
            return null;
        }
    }

    public String toString() {
        String ret = "EMGSTART\n";
        ret += game.getGameId() + "\n";
        ret += game.getCode() + "\n";
        for( int i=0; i<commands.size(); ++i ) {
            ret += commands.elementAt(i).toString() + "\n";
        }
        ret += "EMGEND\n";
        return ret;
    }

    public int getCommandNum() {
        return commands.size();
    }

    public Command getCommand(int index) {
        return commands.elementAt(index);
    }

    private int getCommandIndex(Area a) {
        return getCommandIndexById(a.getId());
    }


    public Command getCommandByType(CommandType type) {
        int index = getCommandIndexByType(type);
        if( index > -1 ) {
            return commands.elementAt(index);
        }
        return null;
    }

    /**
     * Get the first command with type type.
     */
    public int getCommandIndexByType(CommandType type) {
        int index=0;
        while( index < commands.size() && 
               commands.elementAt(index).getType() != type ) {
            ++index;
        }
        if( index < commands.size() ) {
            return index;
        }
        return -1;
    }

    public int getCommandIndexById(int id) {
        int index=0;
        while( index < commands.size() && 
	       (((commands.elementAt(index).getType().flags & CommandType.COMMAND_TYPE_UNIQUE_SOURCE) == 0) ||
               commands.elementAt(index).getIntParam(0) != id )) {
            ++index;
        }
        if( index < commands.size() ) {
            return index;
        }
        return -1;
    }

    public Command getCommand(Area a) {
        int index = getCommandIndex(a);
        if( index > -1 ) {
            return commands.elementAt(index);
        }
        return null;
    }
    
} // CommandCollection
