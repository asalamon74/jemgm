package jemgm;

import java.awt.Color;

/**
 * PlayersRelation.java
 *
 *
 * Created: Wed Feb 13 10:07:43 2002
 *
 * @author Salamon Andras
 * @version
 */
public class PlayersRelation  {
    
    public static final int WAR     = 1;
    public static final int NEUTRAL = 2;
    public static final int ALLY    = 3;
    public static final int NOREL   = 4;
    
    public static final int OFFERED_NEUTRAL = 10 + NEUTRAL;
    public static final int OFFERED_ALLY    = 10 + ALLY;
    public static final int OFFERED_WAR     = 10 + WAR;
    
    public static final String []abbr = {"?", "W", "N", "A", "-"};
    public static final String []name = {"??", "War", "Neutral", "Ally", "-"};
    public static final Color []color = {Color.black, Color.red, Color.black, new Color(40, 200,40), Color.black};
    
    private int[][] relations;
    protected Game game;
    
    public PlayersRelation(Game game) {
        this.game = game;
        relations = new int[this.game.getPlayerNum()][this.game.getPlayerNum()];
    }
    
    public void setRelation(int player1, int player2, int rel) {
        relations[player1][player2] = rel;
        //        relations[player2][player1] = rel;
    }
    
    public int getRelation(int player1, int player2) {
        return relations[player1][player2];
    }
    
    public int getSimpleRelation(int player1, int player2) {
        int rel1 = getRelation(player1, player2);
        int rel2 = getRelation(player2, player1);
        if( rel1 == OFFERED_WAR ||
                rel2 == OFFERED_WAR ||
                rel1 == WAR ||
                rel2 == WAR ) {
            return WAR;
        } else if( (rel1 == ALLY && rel2 == ALLY)
        || (rel1 == OFFERED_ALLY && rel2 == OFFERED_ALLY) ) {
            return ALLY;
        } else if( rel1 == NOREL || rel2 == NOREL ) {
            return NOREL;
        } else {
            return NEUTRAL;
        }
    }
    
    public String getAllianceHeadline(int player1, int player2) {
        String headline=null;
        String name1 = game.getPlayer(player1).getName();
        String name2 = game.getPlayer(player2).getName();
        int rel1 = getRelation(player1,player2);
        int rel2 = getRelation(player2,player1);
        int rel = getSimpleRelation(player1,player2);
        if( rel1 == OFFERED_WAR ) {
            if( rel2 == OFFERED_WAR ) {
                headline = name1 + " and " + name2 + "declared war on each other";
            } else if( rel2 == OFFERED_ALLY ) {
                headline = name1+ " declared war on "+name2+" who was trying to ally";
            } else {
                headline = name1+ " declared war on "+name2;
            }
        } else if( rel1 == OFFERED_NEUTRAL ) {
            if( rel == WAR ) {
                headline = name1+ " offered to end the war, but "+name2+" refused";
            } else if( rel2 == OFFERED_NEUTRAL ) {
                headline = name1 + " and "+name2+ "become neutral to each other";
            } else {
                headline = name1 + " broke alliance with "+name2;
            }
        } else if( rel1 == OFFERED_ALLY ) {
            if( rel == ALLY ) {
                headline = name1 + " and " +name2+ " have formed an alliance";
            } else {
                headline = name1+ " offered " +name2+ " an alliance, but was refused";
            }
        } else {
            if( rel2 == OFFERED_WAR ) {
                headline = name2+ " declared war on "+name1;
            } else if( rel2 == OFFERED_NEUTRAL ) {
                if( rel == WAR ) {
                    headline = name2+ " offered to end the war, but "+name1+" refused";
                } else {
                    headline = name2 + " broke alliance with "+name1;
                }
            } else if( rel2 == OFFERED_ALLY ) {
                headline = name2+ " offered " +name1+ " an alliance, but was refused";
            }
        }
        return headline;
    }
    
    public String getAllianceHeadlines() {
        String headlines="";
        String headline;
        for( int i=1; i<relations.length; ++i ) {
            for( int j=i+1; j<relations.length; ++j ) {
                headline = getAllianceHeadline(i,j);
                if( headline != null ) {
                    headlines += headline + "\n";
                }
            }
        }
        return headlines;
    }
    
    public int getPlayerNum() {
        return relations.length;
    }
    
} // PlayersRelation
