package jemgm;

//import java.awt.Label;
//import java.awt.TextField;
//import java.awt.Button;
import java.awt.GridLayout;
//import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * VictoryConditions.java
 *
 *
 * Created: Fri Jul 19 18:10:12 2002
 *
 * @author Salamon Andras
 * @version
 */

public class VictoryConditions extends JFrame implements ActionListener {

    public static final int MIN_TURN = 10;
    public static final int MAX_TURN = 40;
    public static final int MIN_PERC = 900;
    public static final int MAX_PERC = 1500;

    
    public VictoryConditions(Game game,CommandCollection cc) {
	this.game = game;
        this.cc = cc;
        initComponents();
    }

    protected void initComponents() {
        setSize(300,140);
        setTitle("Victory Conditions");
        getContentPane().setLayout(new GridLayout(3,2,10,10));
        turnLabel = new JLabel("Turn length");
        getContentPane().add(turnLabel);
        turnTextField = new JTextField();
        getContentPane().add(turnTextField);
        percentLabel = new JLabel("Percentage increase");
        getContentPane().add(percentLabel);
        percentTextField = new JTextField();
        getContentPane().add(percentTextField);
        closeButton = new JButton("Close");
        getContentPane().add(new JLabel(""));
        getContentPane().add(closeButton);
        closeButton.addActionListener(this);

        // data
        Command c = cc.getCommandByType(CommandType.VC);
        if( c != null ) {
            int turn = c.getIntParam(0);
            int perc = c.getIntParam(1);
            turnTextField.setText(""+turn);
            percentTextField.setText(""+perc);
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    exitForm(evt);
                }
            });
    }

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        Object source = evt.getSource();
        if( source.equals(closeButton) ) {
            int turn = new Integer(turnTextField.getText()).intValue();
            int perc = new Integer(percentTextField.getText()).intValue();
            if( MIN_TURN > turn ||
                MAX_TURN < turn ||
                MIN_PERC > perc ||
                MAX_PERC < perc ) {
                //MessageBox.showMessage("Invalid Value");
                JOptionPane.showMessageDialog(this, "Invalid Value", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Command c = new Command(game, CommandType.VC, turn, perc);
            cc.addCommand(c);
            setVisible(false);
        }
    }

    private void exitForm(WindowEvent evt) {
        setVisible(false);
        // TODO: some kind of destroy, 
        //       or change the whole class to be a Singleton
    }
    
    protected JLabel turnLabel;
    protected JLabel percentLabel;
    protected JTextField turnTextField;
    protected JTextField percentTextField;
    protected JButton closeButton;

    protected CommandCollection cc;
    protected Game game;
} // VictoryConditions
