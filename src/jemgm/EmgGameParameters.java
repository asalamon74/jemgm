package jemgm;

/**
 * Since GD* and AOD games are quite similar, it's is enough to 
 * store only the differences.
 */
public class EmgGameParameters {

    public static final int GAME_AOD = 1;
    public static final int GAME_GD = 2;

    private int gameType;
    
    public EmgGameParameters( int gameType ) {
	this.gameType = gameType;
    }

    public EmgGameParameters( String gameTypeStr ) {
	if( gameTypeStr.equalsIgnoreCase("AOD") ) {
	    this.gameType = GAME_AOD;
	} else if( gameTypeStr.equalsIgnoreCase("GD") ) {
	    this.gameType = GAME_GD;
	}
    }

//     public int getGameType() {
// 	return gameType;
//     }

    public int getMaxSpyNum() {
	int maxSpy = 0;
	switch( gameType ) {
	case GAME_AOD:
	    maxSpy = 12;
	    break;
	case GAME_GD:
	    maxSpy = 6;
	    break;
	}
	return maxSpy;
    }

    public String getAbbrev() {
	String abbrev = "";
	switch( gameType ) {
	case GAME_AOD:
	    abbrev = "AOD";
	    break;
	case GAME_GD:
	    abbrev = "GD";
	    break;
	}
	return abbrev;
    }

    public String getMapHexFileName() {
	String fileName=null;
	switch( gameType ) {
	case GAME_GD:
	    fileName="/res/gd_map_hex.dat";
	    break;
	}
	return fileName;
    }

    public String getMapAreaFileName() {
	String fileName=null;
	switch( gameType ) {
	case GAME_GD:
	    fileName="/res/gd_map_area.dat";
	    break;
	}
	return fileName;
    }

    public boolean hasStaticMap() {
	boolean b = true;
	if( gameType == GAME_AOD ) {
	    b = false;
	}
	return b;
    }

    public int getMapSizeX() {
	int xsize = 0;
	switch( gameType ) {
	case GAME_GD:
	    xsize = 70;
	    break;
	}
	return xsize;
    }

    public int getMapSizeY() {
	int ysize = 0;
	switch( gameType ) {
	case GAME_GD:
	    ysize = 31;
	    break;
	}
	return ysize;	
    }

    public boolean hasFogOfWar() {
	boolean b = false;
	if( gameType == GAME_AOD ) {
	    b = true;
	}
	return b;
    }

}
