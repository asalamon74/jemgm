package jemgm;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * AODMap.java
 *
 *
 * Created: Fri Feb 15 20:49:47 2002
 *
 * @author Salamon Andras
 * @version
 */

public class Map  {
    
    public enum ParserState {NOT_STARTED,
    EMAILS,
    HEADER,
    GAME_SETUP,
    AREA,
    RELATIONS,
    HEADLINES,
    ALLIANCE_HEADLINES,
    COMMAND_REPORTS,
    POSSIBLE_COMMANDS};
    
    public Map(int turnNum, String fileName, Player p) {
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
    
    /**
     * Processes a file (report file format).
     */
    public boolean processMapReportFormat(Game game, AreaDataBase adb, PlayersRelation pr, boolean headerProcess, boolean actual) {
        String line;
        
        System.out.println("processMapReport");        
        ParserState state = ParserState.NOT_STARTED;
        if( !headerProcess ) {
            state = ParserState.AREA;
        }
        Vector<String> headLines  = new Vector<String>();
        int plNum=0;
        int relId=0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(game.getDirectory()+getFileName()));
            while( (line = reader.readLine()) != null ) {
                if( line.matches(".*Current Status.*") ) {
                    state = ParserState.AREA;
                    continue;
                } else if( line.matches(".*RELATIONS.*")) {
                    state = ParserState.RELATIONS;
                    continue;
                } else if( line.matches(".*ALLIANCE HEADLINES.*")) {
                    state = ParserState.ALLIANCE_HEADLINES;
                    continue;
                } else if( line.matches(".*HEADLINES.*")) {
                    state = ParserState.HEADLINES;
                    continue;
                } else if( line.matches(".*GLOBAL DIPLOMACY.*GAME.*")) {
                    state = ParserState.HEADER;
                } else if( line.matches(".*RESULTS OF COMMANDS REPORTS.*")) {
                    state = ParserState.COMMAND_REPORTS;
                } else if( line.matches(".*POSSIBLE COMMANDS.*")) {
                    state = ParserState.POSSIBLE_COMMANDS;
                } else if( line.matches(".*E-MAIL GAMES:.*")) {
                    plNum = 0;
                    state = ParserState.EMAILS;
                } else if( line.matches(".*GAME SETUP REPORT:.*")) {
                    plNum=0;
                    state = ParserState.GAME_SETUP;
                }
                if( line.matches("\\=*")) {
                    // ===== line, ignore
                    continue;
                }
                switch( state ) {
                    case EMAILS:
                        Matcher botMatcher = Pattern.compile(".*MAIL GAMES:\\s*(.*)\\s*\\|").matcher(line);
                        if( botMatcher.matches() ) {
                            //			    System.out.printf("Bot email: [%s]\n", botMatcher.group(1).trim());
                            game.setBotEmail(botMatcher.group(1).trim());
                        }
                        Matcher playerEmailMatcher = Pattern.compile("\\|\\s([A-Z\\-]{1,4})\\s+([\\s\\w\\(\\)]*)\\s{5,}(.*)\\s*\\|").matcher(line);
                        if( headerProcess && playerEmailMatcher.matches() ) {
                            String abbrev = playerEmailMatcher.group(1).trim();
                            String realName = playerEmailMatcher.group(2).trim();
                            String eMail = playerEmailMatcher.group(3).trim();
                            //			    System.out.printf("Email of player [%s] real name [%s] is [%s]\n",abbrev, realName, eMail);
                            if( game.getPlayer(++plNum) == null ) {
                                Player p = new Player(abbrev, abbrev, plNum);
                                p.setEmail(eMail);
                                game.addPlayer(p);
                            }
                        }
                        break;
                    case HEADER:
                        Matcher gameMatcher = Pattern.compile(".*GAME \\: (.*)").matcher(line);
                        Matcher countryTurnMatcher = Pattern.compile("COUNTRY :\\s*(\\w*)\\s*TURN[\\s:]*(.*)").matcher(line);
//                        Matcher numberLandSeaAreasMatcher = Pattern.compile(".*Number Land Areas\\s*:\\s*(\\d*)\\s*Nunber Sea Areas\\s*:\\s*(\\d*).*").matcher(line);
                        if( gameMatcher.matches()) {
                            //                            System.out.printf("Game code: [%s]\n", gameMatcher.group(1));
                            game.setCode(gameMatcher.group(1).trim());
                        }
                        if( countryTurnMatcher.matches()) {
                            //			    System.out.printf("Country: [%s]\n", countryTurnMatcher.group(1));
                            game.setPlayer(game.getPlayer(countryTurnMatcher.group(1)));
                            System.out.printf("Turn: [%s]\n", countryTurnMatcher.group(2));
                        }
//                        if( numberLandSeaAreasMatcher.matches() ) {
//                            System.out.printf("Land Areas: [%s]\n", numberLandSeaAreasMatcher.group(1));
//                            System.out.printf("Sea Areas: [%s]\n", numberLandSeaAreasMatcher.group(2));
//                        }
                        break;
                    case GAME_SETUP:
                        if( line.matches(".*1st.*") ) {
                            StringTokenizer st = new StringTokenizer(line, "|");
                            Player p = game.getPlayer(++plNum);
                            p.setName(st.nextToken());
                        }
                        break;
                    case AREA:
                        if( !line.matches(".*\\d.*") ) {
                            // no number in the line, we can skip it.
                            continue;
                        }
                        line = line.trim();
                        StringTokenizer tokenizer = new StringTokenizer(line, "|");
                        // concatenate the broken lines if necessery
                        while( tokenizer.countTokens() < 10 || !line.matches(".*\\|")) {
                            line += reader.readLine();
                            tokenizer = new StringTokenizer(line, "|");
                        }
                        String id = tokenizer.nextToken().trim();
                        String areaName = tokenizer.nextToken().trim();
                        String supplyNum = tokenizer.nextToken().trim();
                        String currentAreaOwner = tokenizer.nextToken().trim();
                        String currentUnitType = tokenizer.nextToken().trim();
                        String currentUnitOwner = tokenizer.nextToken().trim();
                        String prevAreaOwner = tokenizer.nextToken().trim();
                        String prevUnitType = tokenizer.nextToken().trim();
                        String prevUnitOwner = tokenizer.nextToken().trim();
                        String lastCommand = tokenizer.nextToken().trim();
// 			System.out.printf("Area [%20s] (%3s) Supply: %2s Prev: [%s] [%s] [%s] Current: [%s] [%s] [%s] Command: [%s]\n",
// 					  areaName, id, supplyNum, prevAreaOwner, prevUnitType, prevUnitOwner, currentAreaOwner, currentUnitType, currentUnitOwner, lastCommand);
                        AreaInformation ai = new AreaInformation();
                        ai.setId(Integer.parseInt(id));
                        if( currentAreaOwner.equals("****") ) {
                            currentAreaOwner = game.getPlayer().getAbbrev();
                        }
                        ai.setOwner(game.getPlayer(currentAreaOwner).getNum());
                        if( currentUnitOwner.equals("****") ) {
                            currentUnitOwner = game.getPlayer().getAbbrev();
                        }
                        if( currentUnitType.length() > 0 ) {
                            ai.setUnitType(Unit.getUnit(currentUnitType).getId());
                            ai.setUnitOwner(game.getPlayer(currentUnitOwner).getNum());
                        }
                        if( actual ) {
                            adb.mergeAreaInformation(ai);
                        } else {
                            adb.mergeArea((Area)ai);
                        }
                        
                        break;
                    case RELATIONS:
                        if( line.matches("\\=*")) {
                            // ===== line, ignore
                            continue;
                        }
                        String relStr;
                        // no delimiter character, we assume fix width
                        if( line.substring(8).matches("[\\sWAN\\-]*")) {
                            ++relId;
                            int i=8;
                            int relId2=0;
                            while( i < line.length() ) {
                                ++relId2;                                
                                System.out.printf("Relation [%d, %d]: [%s]\n",relId, relId2, line.substring(i, i+5));                                
                                relStr = line.substring(i,i+1);
                                if( relStr.equals("W") ) {
                                    pr.setRelation(relId, relId2, PlayersRelation.RelationType.WAR);
                                    pr.setRelation(relId2, relId, PlayersRelation.RelationType.WAR);                                    
                                } else if( relStr.equals("A")) {
                                    pr.setRelation(relId, relId2, PlayersRelation.RelationType.ALLY);
                                    pr.setRelation(relId2, relId, PlayersRelation.RelationType.ALLY);
                                } else if( relStr.equals("-")) {
                                    pr.setRelation(relId, relId2, PlayersRelation.RelationType.NOREL);
                                    pr.setRelation(relId2, relId, PlayersRelation.RelationType.NOREL);
                                } else {
                                    pr.setRelation(relId, relId2, PlayersRelation.RelationType.NEUTRAL);
                                    pr.setRelation(relId2, relId, PlayersRelation.RelationType.NEUTRAL);
                                }
                                i += 5;
                            }
                        }
                        break;
                    case HEADLINES:
                        if( line.matches("\\=*")) {
                            // ===== line, ignore
                            continue;
                        }
                        if( line.trim().length() > 0 ) {
                            headLines.add(line.trim());
                        }
                        break;
                    case ALLIANCE_HEADLINES:
                        Pattern offeredRefused = Pattern.compile("(.*)offered(.*)an alliance, but was refused");
                        Pattern allied = Pattern.compile("(.*)and(.*)have formed an alliance");
                        Pattern brokeAlliance = Pattern.compile("(.*)broke alliance with(.*)");
                        Pattern declaredWarOn = Pattern.compile("(.*)declared war on(.*)");
                        Pattern offeredEndOfWar = Pattern.compile("(.*)offered to end the war, but(.*)refused");
                        
                        Matcher offeredRefusedMatcher = offeredRefused.matcher(line);
                        Matcher alliedMatcher = allied.matcher(line);
                        Matcher brokeAllianceMatcher = brokeAlliance.matcher(line);
                        Matcher declaredWarOnMatcher = declaredWarOn.matcher(line);
                        Matcher offeredEndOfWarMatcher = offeredEndOfWar.matcher(line);
                        
                        if( offeredRefusedMatcher.matches()) {
                            System.out.printf("[%s] a [%s]\n",offeredRefusedMatcher.group(1), offeredRefusedMatcher.group(2));
                        } else if( alliedMatcher.matches()) {
                            System.out.printf("[%s] A [%s]\n",alliedMatcher.group(1), alliedMatcher.group(2));
                        } else if( brokeAllianceMatcher.matches() ) {
                            System.out.printf("[%s] n [%s]\n",brokeAllianceMatcher.group(1), brokeAllianceMatcher.group(2));
                        } else if( declaredWarOnMatcher.matches() ) {
                            System.out.printf("[%s] w [%s]\n",declaredWarOnMatcher.group(1), declaredWarOnMatcher.group(2));
                        } else if( offeredEndOfWarMatcher.matches()) {
                            System.out.printf("[%s] wr [%s]\n",offeredEndOfWarMatcher.group(1), offeredEndOfWarMatcher.group(2));
                        } else {
                            System.out.println("Unknown alliance line:"+line);
                        }
                        break;
                }
            }
            System.out.printf("Number of headlines: %d\n", headLines.size());
        } catch( FileNotFoundException e) {
            System.out.println("File not found: "+e);
            return false;
        } catch( IOException e2) {
            System.out.println("File read error: "+e2);
            return false;
        }
        System.out.println("state:"+state);
        return state != ParserState.NOT_STARTED;
    }
    
    /**
     * Processes a file (date file format).
     */
    public boolean processMapDataFormat(Game game, AreaDataBase adb, PlayersRelation pr, boolean headerProcess, boolean actual) {
        try {
            System.out.println("headerProcess:"+headerProcess);
            BufferedReader fin = new BufferedReader(new FileReader(game.getDirectory()+getFileName()));
            String line;
            int lineNum = 1;
            StringTokenizer st;
            int xsize = 0;
            int ysize = 0;
            while( (line = fin.readLine()) != null ) {
                //				System.out.println("line:"+line);
                if( line.trim().length() == 0 ) {
                    continue;
                }
                if( lineNum < 6 && headerProcess ) {
                    while( lineNum == 1 && !line.matches("(GD|AOD)\\d{1,4}") ) {
                        // first line shows the game id
                        // we skip the previous lines (e.g. e-mail lines)
                        line = fin.readLine();
                    }
                    //		    System.out.println("lineNum:"+lineNum);
                    switch( lineNum ) {
                        case 1:
                            game.setGameId(line);
                            ++lineNum;
                            break;
                        case 2:
                            st = new StringTokenizer(line, " ,");
                            game.setPlayer(game.getPlayer(Integer.parseInt(st.nextToken())));
                            game.setCode(st.nextToken());
                            ++lineNum;
                            if( game.getGameType().hasStaticMap() ) {
                                ++lineNum;
                            }
                            break;
                        case 3:
                            st = new StringTokenizer(line, " ");
                            xsize = Integer.parseInt(st.nextToken());
                            ysize = Integer.parseInt(st.nextToken());
                            game.setShiftX( Integer.parseInt(st.nextToken()) );
                            game.setShiftY( Integer.parseInt(st.nextToken()) );
                            ++lineNum;
                            break;
                        case 4:
                            st = new StringTokenizer(line, ",");
                            st.nextToken(); // ignore Number of Players label
                            //			game.setPlayerNum( Integer.parseInt(st.nextToken().trim()) );
//                            pr = new PlayersRelation(game);
                            ++lineNum;
                            break;
                        case 5:
                            st = new StringTokenizer(line, " ,");
                            String abbrev = st.nextToken();
                            String name = st.nextToken();
                            String relationString = st.nextToken();
                            if( !abbrev.equalsIgnoreCase("Unkwn") ) {
                                if( pr != null ) {
                                    Player p = game.getPlayer(abbrev);
                                    for( int i=0; i<relationString.length() && i<game.getPlayerNum(); ++i ) {
                                        String c = ""+relationString.charAt(i);
                                        if( "W".equals(c) ) {
                                            pr.setRelation(p.getNum(), i, PlayersRelation.RelationType.WAR);
                                        } else if( "w".equals(c) ) {
                                            pr.setRelation(p.getNum(), i, PlayersRelation.RelationType.OFFERED_WAR);
                                        } else if( "N".equals(c) ) {
                                            pr.setRelation(p.getNum(), i, PlayersRelation.RelationType.NEUTRAL);
                                        } else if( "n".equals(c) ) {
                                            pr.setRelation(p.getNum(), i, PlayersRelation.RelationType.OFFERED_NEUTRAL);
                                        } else if( "A".equals(c) ) {
                                            pr.setRelation(p.getNum(), i, PlayersRelation.RelationType.ALLY);
                                        } else if( "a".equals(c) ) {
                                            pr.setRelation(p.getNum(), i, PlayersRelation.RelationType.OFFERED_ALLY);
                                        } else if( "-".equals(c) ) {
                                            pr.setRelation(p.getNum(), i, PlayersRelation.RelationType.NOREL);
                                        }
                                    }
                                }
                            } else {
                                ++lineNum;
                                fin.readLine();
                            }
                    }
                    continue;
                } else if( lineNum < 6 && !headerProcess) {
                    // skip the header
                    while( !line.startsWith("Number of Areas" ) ) {
                        if( (line = fin.readLine()) == null ) {
                            // end of file before number of areas line
                            // not data file format
                            return false;
                        }
                    }
                    line = fin.readLine();
                    lineNum = 6;
                }
                // after the header lines
                if( headerProcess && adb.getXSize() == 0 ) {
                    System.out.printf("hp [%d,%d]\n", xsize, ysize);
                    adb.init( xsize, ysize );
                }
                if( line.trim().matches("Subject: EMG: \\w* ColorMap Data File") ) {
                    // colormap data file
                    // we can skip the lines
                    break;
                }
                st = new StringTokenizer(line, " ");
                AreaInformation ai = new AreaInformation();
                ai.setId( Integer.parseInt( st.nextToken() ) );
                int owner = Integer.parseInt( st.nextToken() );
                //		System.out.println("owner:"+owner);
                if( owner != game.getPlayerNum() ) {
                    ai.setOwner(owner);
                } else {
                    ai.setOwner(-1);
                }
                //		System.out.println("aiowner:"+ai.getOwner());
                if( !game.getGameType().hasStaticMap() ) {
                    ai.setAreaType( Integer.parseInt( st.nextToken() ) );
                    ai.setSupplyPointNum( Integer.parseInt( st.nextToken() ) );
                    for( int i=0; i<10; ++i ) {
                        ai.addNeighbour( Integer.parseInt( st.nextToken() ) );
                    }
                    int x1 = Integer.parseInt( st.nextToken());
                    int x2 = Integer.parseInt( st.nextToken());
                    int x3 = Integer.parseInt( st.nextToken());
                    int y1 = Integer.parseInt( st.nextToken());
                    int y2 = Integer.parseInt( st.nextToken());
                    int y3 = Integer.parseInt( st.nextToken());
                    ai.addHex(x1, y1);
                    ai.addHex(x2, y2);
                    ai.addHex(x3, y3);
                }
                ai.setUnitOwner( Integer.parseInt( st.nextToken() ) );
                ai.setUnitType(Integer.parseInt( st.nextToken()));
                if( actual ) {
                    //		    System.out.println("xsize:"+adb.getXSize());
                    //		    System.out.println("ysize:"+adb.getYSize());
                    //		    System.out.println("mergeai");
                    adb.mergeAreaInformation(ai);
                } else {
                    //		    System.out.println("ma xsize:"+adb.getXSize());
                    //		    System.out.println("ma ysize:"+adb.getYSize());
                    //		    System.out.println("mergea");
                    adb.mergeArea((Area)ai);
                }
            }
        } catch( FileNotFoundException e ) {
            System.out.println("FileNotFound:"+e);
            return false;
        } catch( IOException e2 ) {
            System.out.println("IOException:"+e2);
            return false;
        }
        return true;
    }
    
    public boolean processMap(Game game, AreaDataBase adb, PlayersRelation pr, boolean headerProcess, boolean actual) {
        if( !processMapReportFormat(game, adb, pr, headerProcess, actual) ) {
            return processMapDataFormat(game, adb, pr, headerProcess, actual);
        } else {
            return true;
        }
    }
    
    
} // AODMap
