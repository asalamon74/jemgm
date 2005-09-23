package jemgm;

import java.awt.Color;

/**
 * Command types
 */
public class CommandType {

    // only one instance
    public static int COMMAND_TYPE_UNIQUE = 1;
    // only one command for a given area (counting all types)
    public static int COMMAND_TYPE_UNIQUE_SOURCE = 2;
    // only one command for a given area (counting all types)
    public static int COMMAND_TYPE_UNIQUE_DEST = 4;    


    public static final CommandType SA = new CommandType("SA", "Support Attack",     2, Color.blue, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);
    public static final CommandType SD = new CommandType("SD", "Support Defense",    2, Color.red, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);
    public static final CommandType MO = new CommandType("MO", "Move",               2, Color.black, CommandType.COMMAND_TYPE_UNIQUE_SOURCE | CommandType.COMMAND_TYPE_UNIQUE_DEST);
    public static final CommandType AC = new CommandType("AC", "Add Corp",           1, Color.white, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);
    public static final CommandType AA = new CommandType("AA", "Add Army",           1, Color.white, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);
    public static final CommandType AS = new CommandType("AS", "Add Squandron",      1, Color.white, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);
    public static final CommandType AF = new CommandType("AF", "Add Fleet",          1, Color.white, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);
    public static final CommandType UU = new CommandType("UU", "Upgrade Unit",       1, Color.yellow, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);

    public static final CommandType GA = new CommandType("GA", "Grant Access",       0);
    public static final CommandType DA = new CommandType("DA", "Declare Ally",       0);
    public static final CommandType DN = new CommandType("DN", "Declare Neutral",    0);
    public static final CommandType DW = new CommandType("DW", "Declate War",        0);
    public static final CommandType DU = new CommandType("DU", "Downgrade Unit",     1, Color.cyan, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);
    public static final CommandType RU = new CommandType("RU", "Remove Unit",        1, Color.cyan, CommandType.COMMAND_TYPE_UNIQUE_SOURCE);
    public static final CommandType ME = new CommandType("ME", "Message",            2);
    public static final CommandType VC = new CommandType("VC", "Victory Condition",  2,null, CommandType.COMMAND_TYPE_UNIQUE);
    public static final CommandType CO = new CommandType("CO", "Convoy",           -15, Color.green, CommandType.COMMAND_TYPE_UNIQUE_SOURCE | CommandType.COMMAND_TYPE_UNIQUE_DEST);
    public static final CommandType CA = new CommandType("CA", "Call to Arms",       2);

    // we need to initialize here, because the main program uses this info for creating buttons
    public static CommandType SP = new CommandType("SP", "Spy", 0, Color.red);

	
    public static CommandType[] commandTypes = {
	SA, SD, MO, AC, AA, AS, AF, UU, SP, GA, DA, DN, DW, DU, RU, ME, VC, CO, CA
    };


    /** 
     * Initializes the game-dependent commandtypes.
     *
     * Spy command has different parameters in GD* and AOD games.
     */
    public static void initGameDependentCommandTypes(EmgGameParameters emgParam) {
        //System.out.println("spynum: "+commandTypes[8].paramNum);
        SP = new CommandType("SP", "Spy", -emgParam.getMaxSpyNum(), Color.red, CommandType.COMMAND_TYPE_UNIQUE);
        commandTypes[8] = SP;
        //System.out.println("spynum: "+commandTypes[8].paramNum);
    }

    // TODO: list the others


    String abbrev;
    String name;
    /**
     * Number of parameters.
     * If negative, then only a maximum number is known (-15 means: max 15)
     * For CO there is no maximum, but 15 should be enough.
     */
    int paramNum;
    Color  color;
    int flags;

    public CommandType(String abbrev, String name,  int paramNum) {
	this( abbrev, name, paramNum, null );
    }


    public CommandType(String abbrev, String name,  int paramNum, Color color) {
	this( abbrev, name, paramNum, color, 0 );
    }

    public CommandType(String abbrev, String name,  int paramNum, Color color, int flags) {
	this.abbrev = abbrev;
	this.name = name;
	this.paramNum = paramNum;
	this.color = color;
	this.flags = flags;
    }

    public boolean equals(Object obj) {
	if( !(obj instanceof CommandType) ) {
	    return false;
	}
	return abbrev.equals(((CommandType)obj).abbrev);
    }

    public static CommandType findCommandType(String abbrev) {
	int index = 0;
	while( index < commandTypes.length && !commandTypes[index].abbrev.equals(abbrev) ) {
	    ++index;
	}
	if( index < commandTypes.length ) {
	    return commandTypes[index];
	}
	return null;
    }
}
