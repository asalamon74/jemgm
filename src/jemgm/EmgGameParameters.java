package jemgm;

/**
 * Since GD* and AOD games are quite similar, it's is enough to
 * store only the differences.
 */
public class EmgGameParameters {
    
    public enum GameType { GAME_AOD, GAME_GD };
        
    private GameType gameType;
    
    public EmgGameParameters( GameType gameType ) {
        this.gameType = gameType;
    }
    
    public EmgGameParameters( String gameTypeStr ) {
        if( gameTypeStr.equalsIgnoreCase("AOD") ) {
            this.gameType = GameType.GAME_AOD;
        } else if( gameTypeStr.equalsIgnoreCase("GD") ) {
            this.gameType = GameType.GAME_GD;
        }
    }
    
    
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
        if( gameType == GameType.GAME_AOD ) {
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
    
    public boolean hasNeutralAndUnknown() {
        boolean b = false;
        if( gameType == GameType.GAME_AOD ) {
            b = true;
        }
        return b;
    }
    
    public boolean wordWrapY() {
        boolean wordWrap = false;
        switch( gameType ) {
            case GAME_AOD: 
                wordWrap = true;
                break;
            case GAME_GD:
                wordWrap = false;
                break;
        }
        return wordWrap;
    }
    
}
