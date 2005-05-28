package jemgm;

import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;

/**
 * PlayerSelect.java
 *
 *
 * Created: Tue Jul 23 08:21:02 2002
 *
 * @author Salamon Andras
 * @version
 */

public class PlayerSelect extends JDialog implements ActionListener {

    public PlayerSelect(JFrame parent, Game game,  PlayerSelectable sel, String title) {
        super(parent, true);
        setTitle(title);
	this.game = game;
        this.sel = sel;
        initComponents();
    }

    protected void initComponents() {
        setSize(220,80);
        playerLabel = new JLabel("Player");
        playerChoice = new JComboBox();
	playerChoice.setEditable(false);
        fillPlayerChoice();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        getContentPane().setLayout(new GridLayout(2,2,1,1));
        getContentPane().add(playerLabel);
        getContentPane().add(playerChoice);
        getContentPane().add(okButton);
        getContentPane().add(cancelButton);

        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
		    playerChoice.setSelectedIndex(-1);
		    setVisible(false);
                }
            });
    }    

    /**
     * Fills the playerChoice with the name of the players.
     *
     */
    private void fillPlayerChoice() {
        int max = game.getPlayerNum();
//         if( max == 0 ) {
//             // not yet initialized
//             max = Player.getMaxPlayerNum();
//         }
        for( int i = 1; i<max; ++i ) {            
            if( sel.select(game.getPlayer(i)) ) {
                playerChoice.addItem(game.getPlayer(i).getName());
            }
        }
    }

    public Player getSelectedPlayer() {
	if( playerChoice.getSelectedIndex() == -1 ) {
	    return null;
	}
        return game.getPlayer((String)playerChoice.getSelectedItem());
    }

    public  void actionPerformed(java.awt.event.ActionEvent evt) {
        Object source = evt.getSource();
        if( source.equals(okButton) ) {
            setVisible(false);
        } else if( source.equals(cancelButton) ) {
	    playerChoice.setSelectedIndex(-1);
	    setVisible(false);
	}
    }

    protected JLabel playerLabel;
    protected JComboBox playerChoice;
    protected JButton okButton;
    protected JButton cancelButton;
    protected PlayerSelectable sel;
    protected Game game;
} // PlayerSelect
