package jemgm;

import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * VisualPlayersRelation
 *
 * Visually shows the relation between the players.
 */

public class VisualPlayersRelation extends JPanel {
    
    public VisualPlayersRelation() {
    }
    
    private void updateComponents() {
        int plnum = pr.getPlayerNum();
        boolean init = (labels == null);
        if( init ) {
            labels = new JLabel[plnum][plnum];
            setLayout(new GridLayout(plnum,plnum,2,2));
        }
        String label;
        String tooltip;
        for( int i=0; i<plnum; ++i ) {
            for( int j=0; j<plnum; ++j ) {
                label = pr.getSimpleRelation(i,j).abbrev;
                tooltip = pr.getSimpleRelation(i,j).abbrev;
                if( i == 0 && j == 0 ) {
                    label = "";
                } else if( i == 0 ) {
                    //label = ""+pr.game.getPlayer(j).getName().charAt(0);
                    label = pr.game.getPlayer(j).getAbbrev();
                    tooltip = "Player: "+pr.game.getPlayer(j).getName();
                } else if( j == 0 ) {
                    // label = ""+pr.game.getPlayer(i).getName().charAt(0);
                    label = pr.game.getPlayer(i).getAbbrev();
                    tooltip = "Player: "+pr.game.getPlayer(i).getName();
                } else if( i == j ) {
                    label = "-";
                }
                if( init ) {
                    labels[i][j] = new JLabel(label);
                } else {
                    labels[i][j].setText(label);
                }
                labels[i][j].setToolTipText(tooltip);
                
                PlayersRelation.RelationType rel1 = pr.getRelation(i,j);
                PlayersRelation.RelationType rel2 = pr.getRelation(j,i);
                
                if( !pr.getSimpleRelation(i,j).equals( rel1 ) ||
                    !pr.getSimpleRelation(i,j).equals( rel2 ) ) {
                    labels[i][j].setFont(new Font("Helvetica", Font.BOLD, 14));
                    labels[i][j].setToolTipText(pr.getAllianceHeadline(i,j));
                }
                if( i != 0 && j != 0 ) {
                    labels[i][j].setForeground(pr.getSimpleRelation(i,j).color);
                }
                if( init ) {
                    add(labels[i][j]);
                }
            }
        }
    }
    
    private PlayersRelation pr;

    public PlayersRelation getPr() {
        return pr;
    }

    public void setPr(PlayersRelation pr) {
        this.pr = pr;
        updateComponents();
    }
    
    private JLabel [][]labels;
    
} // VisualPlayersRelation
