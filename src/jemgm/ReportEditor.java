package jemgm;

import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * ReportEditor.java
 *
 *
 * Created: Sun Feb 24 15:10:53 2002
 *
 * @author Salamon Andras
 * @version
 */
public class ReportEditor extends JDialog
  implements ActionListener {

    public static final int ADD  = 1;
    public static final int EDIT = 2;

    public static final int OK     = 1;
    public static final int CANCEL = 2;
    
    public ReportEditor(JFrame f, Game game, int mode) {
        super(f, true);
        setGame(game);
        setMode(mode);
        initComponents();
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
            playerChoice.addItem(game.getPlayer(i).getName());
        }
    }

    private void initComponents() {
        setSize(400,150);
        turnNumLabel = new JLabel("Turn Number");
        fileLabel = new JLabel("File");
        playerLabel = new JLabel("Player");
        turnNumTextField = new JTextField();
        fileTextField = new JTextField();
        playerChoice = new JComboBox();
	playerChoice.setEditable(false);
        fillPlayerChoice();
        browseButton = new JButton("Browse");
        browseButton.addActionListener(this);
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        getContentPane().setLayout(gbl);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth=1;
        c.gridheight=1;
        gbl.setConstraints(turnNumLabel, c);
        getContentPane().add(turnNumLabel);

        c.gridwidth=GridBagConstraints.REMAINDER;
        c.weightx = 2.0;
        gbl.setConstraints(turnNumTextField, c);
        getContentPane().add(turnNumTextField);

        c.weightx = 0.0;
        c.gridwidth=1;
        gbl.setConstraints(fileLabel, c);
        getContentPane().add(fileLabel);

        c.gridwidth=1;
        c.weightx = 2.0;
        gbl.setConstraints(fileTextField, c);
        getContentPane().add(fileTextField);
        c.weightx = 0.0;

        c.gridwidth=GridBagConstraints.REMAINDER;
        gbl.setConstraints(browseButton, c);
        getContentPane().add(browseButton);

        c.gridwidth=1;
        gbl.setConstraints(playerLabel, c);
        getContentPane().add(playerLabel);

        c.gridwidth=GridBagConstraints.REMAINDER;
        gbl.setConstraints(playerChoice, c);
        getContentPane().add(playerChoice);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        String addString = "";
        if( getMode() == ADD ) {
            addString = "Add";
        } else {
            addString = "Modify";
        }
        addButton = new JButton(addString);
        addButton.addActionListener(this);
        buttonPanel.add(addButton);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        c.gridwidth=GridBagConstraints.REMAINDER;
        gbl.setConstraints(buttonPanel, c);
        getContentPane().add(buttonPanel);

        setTitle("Report Edit");
    }

    public  void actionPerformed(java.awt.event.ActionEvent evt) {
        Object source = evt.getSource();
        if( source.equals(cancelButton) ) {
            setStatus(CANCEL);
            setVisible(false);
        } else if( source.equals(addButton) ) {
            Player p = game.getPlayer(playerChoice.getSelectedIndex()+1);
            int turnNum = new Integer(turnNumTextField.getText()).intValue();
            Map newMap = new Map(turnNum, fileTextField.getText(), p);
            game.getMapcoll().addMap(newMap);
            setStatus(OK);
            setVisible(false);
        } else if( source.equals(browseButton) ) {
	    System.out.println("load report from dir: "+getGame().getDirectory());
	    JFileChooser fc = new JFileChooser(getGame().getDirectory());
	    fc.setDialogTitle("Load Report");

	    //fc.setFileFilter(filter);
	    //	    fc.setDirectory(getGame().getDirectory());
	    int retVal = fc.showOpenDialog(this);
	    if( retVal != JFileChooser.APPROVE_OPTION ) {
		return;
	    }
	    
	    //            fc.setDirectory(getGame().getDirectory());
	    //            fc.setVisible(true);
	    //            String fileName = fc.getDirectory() + fc.getFile();
	    String fileName = fc.getSelectedFile().getPath();
            if( fileName.indexOf(getGame().getDirectory()) != -1 ) {
                fileTextField.setText(fileName.substring(getGame().getDirectory().length()));
            } else {
                System.out.println("Wrong directory");
		JOptionPane.showMessageDialog(this, "Wrong directory", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void setFields(int turnNum, String fileName, Player p) {
        turnNumTextField.setText(""+turnNum);
        fileTextField.setText(fileName);
        playerChoice.setSelectedIndex(p.getNum()-1);
    }

    Game game;
    
    /**
     * Get the value of game.
     * @return Value of game.
     */
    public Game getGame() {
         return game; 
    }
    
    /**
     * Set the value of game.
     * @param v  Value to assign to game.
     */
    public void setGame(Game  v) {
        this.game = v;
    }
    
    int mode;
    
    /**
     * Get the value of mode.
     * @return Value of mode.
     */
    public int getMode() {
         return mode; 
    }
    
    /**
     * Set the value of mode.
     * @param v  Value to assign to mode.
     */
    public void setMode(int  v) {
        this.mode = v;
    }


    int status;
    
    /**
     * Get the value of status.
     * @return Value of status.
     */
    public int getStatus() {
         return status; 
    }
    
    /**
     * Set the value of status.
     * @param v  Value to assign to status.
     */
    public void setStatus(int  v) {
        this.status = v;
    }
        
    private JLabel turnNumLabel;
    private JTextField turnNumTextField;
    private JLabel fileLabel;
    private JTextField fileTextField;
    private JButton browseButton;
    private JLabel playerLabel;
    private JComboBox playerChoice;
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JButton addButton;
    
} // ReportEditor
