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

public class VisualPlayersRelation extends JFrame {
    
    public VisualPlayersRelation(PlayersRelation pr) {
        this.pr = pr;
        initComponents();
    }
    
    private void initComponents() {
        int plnum = pr.getPlayerNum();
        setSize(50*plnum, 30*plnum);
        getContentPane().setLayout(new GridLayout(plnum,plnum,2,2));
        for( int i=0; i<plnum; ++i ) {
            for( int j=0; j<plnum; ++j ) {
                String label = pr.getSimpleRelation(i,j).abbrev;
                String tooltip = pr.getSimpleRelation(i,j).abbrev;
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
                JLabel lab = new JLabel(label);
                lab.setToolTipText(tooltip);
                
                PlayersRelation.RelationType rel1 = pr.getRelation(i,j);
                PlayersRelation.RelationType rel2 = pr.getRelation(j,i);
                
                if( !pr.getSimpleRelation(i,j).equals( rel1 ) ||
                    !pr.getSimpleRelation(i,j).equals( rel2 ) ) {
                    lab.setFont(new Font("Helvetica", Font.BOLD, 14));
                    lab.setToolTipText(pr.getAllianceHeadline(i,j));
                }
                if( i != 0 && j != 0 ) {
                    lab.setForeground(pr.getSimpleRelation(i,j).color);
                }
                getContentPane().add(lab);
            }
        }
        setTitle("Relations");
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });
    }
    
    private void exitForm(WindowEvent evt) {
        setVisible(false);
        // TODO: some kind of destroy,
        //       or change the whole class to be a Singleton
    }
    
    private PlayersRelation pr;
    
} // VisualPlayersRelation
