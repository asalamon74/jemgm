package jemgm;

import java.awt.Frame;
//import java.awt.List;
//import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
//import java.awt.Panel;
import java.awt.event.ActionListener;
//import java.awt.Dialog;
import javax.swing.*;

/**
 * ReportManager.java
 *
 *
 * Created: Fri Feb 22 19:50:50 2002
 *
 * @author Salamon Andras
 * @version
 */

public class ReportManager extends JDialog implements  ActionListener {
    
    public ReportManager(Frame f, Game game) {
        super(f, true);
        setGame(game);
        initComponents();
    }

    private void initComponents() {
        setSize(400,300);
        reportsList = new JList(new DefaultListModel());
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Edit");
        closeButton = new JButton("Close");

        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        editButton.addActionListener(this);
        closeButton.addActionListener(this);
        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(closeButton);
        getContentPane().add(reportsList, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        setTitle("Reports");
        updateList();
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
    
    public void updateList() {
        ((DefaultListModel)reportsList.getModel()).removeAllElements();
        for( int mi=0; mi<getGame().getMapcoll().getMapCount(); ++mi ) {
            ((DefaultListModel)reportsList.getModel()).addElement(getGame().getMapcoll().getMap(mi).toString());
        }
    }

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        Object source = evt.getSource();
	DefaultListModel listModel = ((DefaultListModel)reportsList.getModel());
        if( source.equals(closeButton) ) {
            setVisible(false);
        } else if( source.equals(deleteButton) ) {
            int index = reportsList.getSelectedIndex();
            if( index != -1 ) {
                getGame().getMapcoll().deleteMap(index);
                listModel.remove(index);
		int listItemNum = listModel.getSize();
                reportsList.setSelectedIndex( index < listItemNum ? index : listItemNum-1 );
            }
        } else if( source.equals(addButton) ) {
            ReportEditor repEditor = new ReportEditor(new JFrame(), getGame(), ReportEditor.ADD);
            repEditor.setVisible(true);
            if( repEditor.getStatus() == repEditor.OK ) {
                updateList();
            }
        } else if( source.equals(editButton) ) {
            ReportEditor repEditor = new ReportEditor(new JFrame(), getGame(), ReportEditor.EDIT);
            int index = reportsList.getSelectedIndex();
            if( index != -1 ) {
                MapDescriptor map =  getGame().getMapcoll().getMap(index);
                repEditor.setFields(map.getTurnNum(), map.getFileName(), map.getPlayer());
                repEditor.setVisible(true);
                if( repEditor.getStatus() == repEditor.OK ) {
                    getGame().getMapcoll().deleteMap(index);
                    listModel.remove(index);
                    updateList();
                }
            }
        }
    }    
    private JList     reportsList;
    private JPanel    buttonsPanel;
    private JButton   addButton;
    private JButton   deleteButton;
    private JButton   editButton;    
    private JButton   closeButton;

} // ReportManager
