package jemgm;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.GridLayout;

public class Colors extends JFrame implements ActionListener {
    
    protected Colors(Game game) {
	this.game = game;
	initComponents();
    }

    public static Colors getInstance(Game game) {
	if( __instance == null ) {
	    __instance = new Colors(game);
	}
	return __instance;
    }

    private void initComponents() {
        setSize(400,570);
	playerColorsPanel = new JPanel();
	int playerNum = game.getPlayerNum();
	playerColorsPanel.setLayout(new GridLayout(playerNum,3));
	playerLabels = new JLabel[playerNum];
	playerColorLabels = new JLabel[playerNum];
	playerButtons = new JButton[playerNum];	
	for( int i=0; i<playerNum; ++i ) {
	    playerLabels[i] = new JLabel(game.getPlayer(i).getName());
	    playerLabels[i].setFont(new Font("Helvetica", Font.BOLD, 10));
	    //	    playerLabels[i].setBackground(playerColors[i % playerColors.length]);
	    //	    playerLabels[i].setOpaque(true);
	    playerColorsPanel.add(playerLabels[i]);

	    playerColorLabels[i] = new JLabel();
	    playerColorLabels[i].setBackground(playerColors[i % playerColors.length]);
	    playerColorLabels[i].setOpaque(true);
	    playerColorsPanel.add(playerColorLabels[i]);
	    
	    playerButtons[i] = new JButton("Change");
	    playerButtons[i].setBackground(playerColors[i % playerColors.length]);
	    //	    playerButtons[i].setOpaque(true);
	    //playerButtons[i].setFont(new Font("Helvetica", Font.BOLD, 10));
	    // playerButtons[i].setForeground(playerColors[i % playerColors.length]);
	    playerButtons[i].addActionListener(this);
	    playerColorsPanel.add(playerButtons[i]);
	}
	getContentPane().add(playerColorsPanel);
    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
	int i=0;
	while( !source.equals(playerButtons[i]) ) {
	    ++i;
	}
	Color newColor = JColorChooser.showDialog(this, "Choose the color for player "+game.getPlayer(i).getName(), playerColors[i % playerColors.length]);
	System.out.println("newColor:"+newColor);
	if( newColor != null ) {
	    playerColors[i % playerColors.length] = newColor;
	    playerButtons[i].setForeground(newColor);
	}
    }

    public Color getPlayerColor(int playerNum) {
	return playerColors[playerNum % playerColors.length];
    }

    private JPanel playerColorsPanel;
    private JLabel []playerLabels;
    private JLabel []playerColorLabels;
    private JButton []playerButtons;
    
    private Color playerColors[] = {
        Color.white, Color.red, Color.pink, Color.orange, Color.yellow,Color.magenta,
    new Color(150,150, 255), new Color(255, 100, 100), new Color(100,255,100), Color.green, new Color(240,240,100), new Color(70,70, 170), new Color(170,70,70) };

    protected Game game;

    protected static Colors __instance;
}
