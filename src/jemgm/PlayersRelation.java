package jemgm;

import java.awt.Color;

/**
 * PlayersRelation
 *
 * Stores the relations between the players.
 * There are two levels of relation. The simple is WAR, ALLY, NEUTRAL,
 * the more complex contains the last action (e.g. offered ALLY but refused).
 *
 */
public class PlayersRelation  {
    
    public enum RelationType {
        WAR ("W", "War", Color.red), 
        NEUTRAL ("N", "Neutral", Color.black), 
        ALLY ("A", "Ally", new Color(40, 200,40)), 
        NOREL ("-", "-", Color.black), 
        OFFERED_NEUTRAL, 
        OFFERED_ALLY, 
        OFFERED_WAR;                                           
        
        RelationType() {            
        }
        
        RelationType(String abbrev, String name, Color color) {
            this.abbrev = abbrev;
            this.name = name;
            this.color = color;
        }
    
        String abbrev;
        String name;
        Color color;
    };
    
//    public static final String []abbr = {"?", "W", "N", "A", "-"};
//    public static final String []name = {"??", "War", "Neutral", "Ally", "-"};
//    public static final Color []color = {Color.black, Color.red, Color.black, new Color(40, 200,40), Color.black};
    
    private RelationType[][] relations;
    protected Game game;
    
    public PlayersRelation(Game game) {
        this.game = game;
        relations = new RelationType[this.game.getPlayerNum()][this.game.getPlayerNum()];
    }
    
    public void setRelation(int player1, int player2, RelationType rel) {
        relations[player1][player2] = rel;
        //        relations[player2][player1] = rel;
    }
    
    public RelationType getRelation(int player1, int player2) {
        return relations[player1][player2];
    }
    
    public RelationType getSimpleRelation(int player1, int player2) {
        RelationType rel1 = getRelation(player1, player2);
        RelationType rel2 = getRelation(player2, player1);
        if( rel1 == RelationType.OFFERED_WAR ||
                rel2 == RelationType.OFFERED_WAR ||
                rel1 == RelationType.WAR ||
                rel2 == RelationType.WAR ) {
            return RelationType.WAR;
        } else if( (rel1 == RelationType.ALLY && rel2 == RelationType.ALLY)
        || (rel1 == RelationType.OFFERED_ALLY && rel2 == RelationType.OFFERED_ALLY) ) {
            return RelationType.ALLY;
        } else if( rel1 == RelationType.NOREL || rel2 == RelationType.NOREL ) {
            return RelationType.NOREL;
        } else {
            return RelationType.NEUTRAL;
        }
    }
    
    public String getAllianceHeadline(int player1, int player2) {
        String headline=null;
        String name1 = game.getPlayer(player1).getName();
        String name2 = game.getPlayer(player2).getName();
        RelationType rel1 = getRelation(player1,player2);
        RelationType rel2 = getRelation(player2,player1);
        RelationType rel = getSimpleRelation(player1,player2);
        if( rel1 == RelationType.OFFERED_WAR ) {
            if( rel2 == RelationType.OFFERED_WAR ) {
                headline = name1 + " and " + name2 + "declared war on each other";
            } else if( rel2 == RelationType.OFFERED_ALLY ) {
                headline = name1+ " declared war on "+name2+" who was trying to ally";
            } else {
                headline = name1+ " declared war on "+name2;
            }
        } else if( rel1 == RelationType.OFFERED_NEUTRAL ) {
            if( rel == RelationType.WAR ) {
                headline = name1+ " offered to end the war, but "+name2+" refused";
            } else if( rel2 == RelationType.OFFERED_NEUTRAL ) {
                headline = name1 + " and "+name2+ "become neutral to each other";
            } else {
                headline = name1 + " broke alliance with "+name2;
            }
        } else if( rel1 == RelationType.OFFERED_ALLY ) {
            if( rel == RelationType.ALLY ) {
                headline = name1 + " and " +name2+ " have formed an alliance";
            } else {
                headline = name1+ " offered " +name2+ " an alliance, but was refused";
            }
        } else {
            if( rel2 == RelationType.OFFERED_WAR ) {
                headline = name2+ " declared war on "+name1;
            } else if( rel2 == RelationType.OFFERED_NEUTRAL ) {
                if( rel == RelationType.WAR ) {
                    headline = name2+ " offered to end the war, but "+name1+" refused";
                } else {
                    headline = name2 + " broke alliance with "+name1;
                }
            } else if( rel2 == RelationType.OFFERED_ALLY ) {
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
