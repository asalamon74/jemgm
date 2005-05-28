package jemgm;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
//import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
//import java.awt.event.ItemEvent;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Diplomacy.java
 *
 *
 * Created: Mon Jul 15 17:14:18 2002
 *
 * @author Salamon Andras
 * @version
 */

public class Diplomacy extends JFrame implements ActionListener, ListSelectionListener {
    
    public Diplomacy(Game game, PlayersRelation pr, CommandCollection cc) {
        this.pr = pr;
        this.cc = cc;
        this.game = game;
        initComponents();
        initData();
    }
    
    private void initComponents() {
        setSize(400,570);
        setTitle("Diplomacy ["+game.getPlayer().getName()+"]");
        alliesLabel = new JLabel("Allies");
        neutralsLabel = new JLabel("Neutral");
        enemiesLabel = new JLabel("Enemies");
        alliesList = new JList(new DefaultListModel());;
        neutralsList = new JList(new DefaultListModel());;
        enemiesList = new JList(new DefaultListModel());;
        neutral1Button = new JButton("Neutral (A->N)");
        neutral2Button = new JButton("Neutral (W->N)");
        allyButton = new JButton("Ally");
        warButton = new JButton("War");
        messageLabel = new JLabel("Message");
        messageTextField = new JTextField();
        sendButton = new JButton("Message Send");
        gaAllButton = new JButton("GA All");
        caButton = new JButton("Call to Arms");
        deleteButton = new JButton("Delete");
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setLayout(gbl);
        
        // labels
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth=1;
        c.gridheight=1;
        c.weightx = 100;
        c.weighty = 0;
        gbl.setConstraints(alliesLabel, c);
        getContentPane().add(alliesLabel);
        
        c.gridwidth=2;
        gbl.setConstraints(neutralsLabel, c);
        getContentPane().add(neutralsLabel);
        
        c.gridwidth=1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(enemiesLabel, c);
        getContentPane().add(enemiesLabel);
        
        // listboxes
        c.gridwidth = 1;
        c.weighty = 100;
        c.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(alliesList, c);
        getContentPane().add(alliesList);
        
        c.gridwidth=2;
        gbl.setConstraints(neutralsList, c);
        getContentPane().add(neutralsList);
        
        c.gridwidth=1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(enemiesList, c);
        getContentPane().add(enemiesList);
        
        c.weighty = 0;
        c.gridwidth = 1;
        gbl.setConstraints(messageLabel, c);
        getContentPane().add(messageLabel);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 100;
        gbl.setConstraints(messageTextField, c);
        getContentPane().add(messageTextField);
        
        // commandsList
        commandsList = new JList(new DefaultListModel());
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridheight = 10;
        c.weighty=100;
        gbl.setConstraints(commandsList, c);
        getContentPane().add(commandsList);
        
        // buttons
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weighty = 0;
        
        gbl.setConstraints(neutral1Button, c);
        getContentPane().add(neutral1Button);
        
        gbl.setConstraints(neutral2Button, c);
        getContentPane().add(neutral2Button);
        
        gbl.setConstraints(allyButton, c);
        getContentPane().add(allyButton);
        
        gbl.setConstraints(warButton, c);
        getContentPane().add(warButton);
        
        gbl.setConstraints(sendButton, c);
        getContentPane().add(sendButton);
        
        gbl.setConstraints(gaAllButton, c);
        getContentPane().add(gaAllButton);
        
        gbl.setConstraints(caButton, c);
        getContentPane().add(caButton);
        
        gbl.setConstraints(deleteButton, c);
        getContentPane().add(deleteButton);
        
        neutral1Button.addActionListener(this);
        neutral2Button.addActionListener(this);
        warButton.addActionListener(this);
        allyButton.addActionListener(this);
        gaAllButton.addActionListener(this);
        caButton.addActionListener(this);
        deleteButton.addActionListener(this);
        sendButton.addActionListener(this);
        neutralsList.addListSelectionListener(this);
        enemiesList.addListSelectionListener(this);
        alliesList.addListSelectionListener(this);
        commandsList.addListSelectionListener(this);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });
    }
    
    
    public void initData() {
        // playerRelation
        int pl = game.getPlayer().getNum();
        for( int i=1; i<pr.getPlayerNum(); ++i ) {
            if( i == pl ) {
                continue;
            }
            PlayersRelation.RelationType rel = pr.getSimpleRelation(i,pl);
            String name = game.getPlayer(i).getName();
            switch( rel ) {
                case WAR: 
                     ((DefaultListModel)enemiesList.getModel()).addElement(name); 
                     break;
                case NEUTRAL: 
                    ((DefaultListModel)neutralsList.getModel()).addElement(name); 
                    break;
                case ALLY: 
                    ((DefaultListModel)alliesList.getModel()).addElement(name); 
                    break;
            }
        }
        // commands
        for( int i=0; i<cc.getCommandNum(); ++i ) {
            Command c = cc.getCommand(i);
            if( c.getType().equals( CommandType.DA ) ){
                String name = game.getPlayer(c.getIntParam(0)).getName();
                int index=0;
                while( index < neutralsList.getModel().getSize() &&
                        !neutralsList.getModel().getElementAt(index).equals(name) ) {
                    ++index;
                }
                if( index < neutralsList.getModel().getSize() ) {
                    neutralsList.setSelectedIndex(index);
                    movePlayer(neutralsList, alliesList, CommandType.DA, false);
                }
            } else if( c.getType().equals( CommandType.DW ) ){
                String name = game.getPlayer(c.getIntParam(0)).getName();
                int index=0;
                while( index < neutralsList.getModel().getSize() &&
                        !neutralsList.getModel().getElementAt(index).equals(name) ) {
                    ++index;
                }
                if( index < neutralsList.getModel().getSize() ) {
                    neutralsList.setSelectedIndex(index);
                }
                movePlayer(neutralsList, enemiesList, CommandType.DW, false);
            } else if( c.getType().equals( CommandType.DN ) ){
                //		System.out.println("intparam:"+c.getIntParam(0));
                String name = game.getPlayer(c.getIntParam(0)).getName();
                //		System.out.println("name: "+name);
                int index=0;
                while( index < alliesList.getModel().getSize() &&
                        !alliesList.getModel().getElementAt(index).equals(name) ) {
                    ++index;
                }
                if( index < alliesList.getModel().getSize() ) {
                    // ally -> neutral
                    alliesList.setSelectedIndex(index);
                    movePlayer(alliesList, neutralsList, CommandType.DN, false);
                } else {
                    index = 0;
                    while( index < enemiesList.getModel().getSize() &&
                            !enemiesList.getModel().getElementAt(index).equals(name) ) {
                        ++index;
                    }
                    if( index < enemiesList.getModel().getSize() ) {
                        // enemy -> neutral
                        enemiesList.setSelectedIndex(index);
                        movePlayer(enemiesList, neutralsList, CommandType.DN, false);
                    }
                }
            } else if( c.getType().equals( CommandType.ME ) ){
                String player = game.getPlayer(c.getIntParam(0)).getName();
                String message = c.getParam(1);
                messageHashtable.put(player, message);
                addCommand(c,player);
            } else if( c.getType().equals( CommandType.GA ) ){
                String gaPlayer = game.getPlayer(c.getIntParam(0)).getName();
                String param1 = c.getParam(1);
                if( param1.equalsIgnoreCase("all") ) {
                    addCommand(c, gaPlayer);
                }
            } else if( c.getType().equals( CommandType.CA ) ) {
                String allyPlayer = game.getPlayer(c.getIntParam(0)).getName();
                String enemyPlayer = game.getPlayer(c.getIntParam(1)).getName();
                //                addCommand(c, allyPlayer + ", " + enemyPlayer);
                addCommand(c);
            }
        }
    }
    
    private void exitForm(WindowEvent evt) {
        setVisible(false);
        // TODO: some kind of destroy,
        //       or change the whole class to be a Singleton
    }
    
    private boolean checkPlayer(JList list) {
        int index = list.getSelectedIndex();
        if( index != -1 ) {
            String item = (String)list.getModel().getElementAt(index);
            int plnum =  game.getPlayer(item).getNum();
            int i = 0;
            while( i < cc.getCommandNum() &&
                    ((cc.getCommand(i).getType() != CommandType.DA &&
                    cc.getCommand(i).getType() != CommandType.DN &&
                    cc.getCommand(i).getType() != CommandType.DW) ||
                    cc.getCommand(i).getIntParam(0) != plnum )) {
                ++i;
            }
            return i >= cc.getCommandNum();
        }
        return false;
    }
    
    private void movePlayer(JList fromList, JList toList, CommandType command, boolean putcc) {
        int index = fromList.getSelectedIndex();
        if( index != -1 ) {
            String item = (String)fromList.getModel().getElementAt(index);
            ((DefaultListModel)toList.getModel()).addElement(item);
            ((DefaultListModel)fromList.getModel()).removeElement(item);
            Command c = new Command(game, command, game.getPlayer(item).getNum());
            if( putcc ) {
                cc.addCommand(c);
            }
            addCommand(c);
        }
    }
    
    private boolean messageCreate(JList l) {
        if( l.getSelectedIndex() != -1 ) {
            String player = (String)l.getSelectedValue();
            int playerInt = game.getPlayer(player).getNum();
            String message = messageTextField.getText();
            Command c = new Command(game, CommandType.ME, playerInt, message);
            cc.addCommand(c);
            addCommand(c,player);
            messageHashtable.put(player, message);
            return true;
        }
        return false;
    }
    
    private int selectedPlayer() {
        if( neutralsList.getSelectedIndex() != -1 ) {
            return game.getPlayer((String)neutralsList.getSelectedValue()).getNum();
        }
        if( enemiesList.getSelectedIndex() != -1 ) {
            return game.getPlayer((String)enemiesList.getSelectedValue()).getNum();
        }
        if( alliesList.getSelectedIndex() != -1 ) {
            return game.getPlayer((String)alliesList.getSelectedValue()).getNum();
        }
        return 0;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        Object source = evt.getSource();
        JList l1=null,l2=null;
        CommandType command=null;
        if( source.equals(neutral1Button) ) {
            l1 = alliesList;
            l2 = neutralsList;
            command = CommandType.DN;
        } else if( source.equals(neutral2Button) ) {
            l1 = enemiesList;
            l2 = neutralsList;
            command = CommandType.DN;
        } else if( source.equals(allyButton) ) {
            l1 = neutralsList;
            l2 = alliesList;
            command = CommandType.DA;
        } else if( source.equals(warButton) ) {
            l1 = neutralsList;
            l2 = enemiesList;
            command = CommandType.DW;
        } else if( source.equals(sendButton) ) {
            messageCreate(alliesList);
            messageCreate(neutralsList);
            messageCreate(enemiesList);
        } else if( source.equals(gaAllButton) ) {
            int selPl = selectedPlayer();
            Command c = new Command(game, CommandType.GA, selPl, "all");
            addCommand(c, game.getPlayer(selPl).getName());
            cc.addCommand(c);
        } else if( source.equals(caButton) ) {
            if( !checkPlayer(alliesList) ) {
                JOptionPane.showMessageDialog(this, "You must choose an allied player", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PlayerSelect ps = new PlayerSelect(this, game, new CASelectablePlayers(game.getPlayer(),game.getPlayer(selectedPlayer())), "Call to Arms against");
            ps.setVisible(true);
            int selPl = selectedPlayer();
            Player selPl2 = ps.getSelectedPlayer();
            if( selPl2 != null ) {
                Command c = new Command(game, CommandType.CA, selPl, selPl2.getNum());
                addCommand(c, game.getPlayer(selPl).getName() + "," +selPl2.getName());
                cc.addCommand(c);
            }
        } else if( source.equals(deleteButton) ) {
            removeCommand();
        }
        if( command != null ) {
            if( checkPlayer(l1) ) {
                movePlayer(l1, l2, command, true);
            } else {
                //MessageBox.showMessage("Command already assigned to this player");
                JOptionPane.showMessageDialog(this, "Command already assigned to this player", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // TODO: eliminate this
    protected void addCommand(Command c, String plusInfo) {
        ((DefaultListModel)commandsList.getModel()).addElement(""+c+" ["+plusInfo+"]");
        diplomacyCommands.addElement(c);
    }
    
    protected void addCommand(Command c) {
        ((DefaultListModel)commandsList.getModel()).addElement(c.toHumanReadableString());
        diplomacyCommands.addElement(c);
    }
    
    protected void removeCommand() {
        int index = commandsList.getSelectedIndex();
        if( index == -1 ) {
            return;
        }
        ((DefaultListModel)commandsList.getModel()).remove(index);
        Command c = diplomacyCommands.elementAt(index);
        cc.removeCommand(c);
        diplomacyCommands.removeElementAt(index);
        // TODO: put it back to the original list
    }
    
    public void valueChanged(ListSelectionEvent e) {
        JList i = (JList)e.getSource();
        if( i.equals(commandsList) ) {
            return;
        }
        int index = e.getFirstIndex();
        if( !i.isSelectedIndex(index) ) {
            return;
        }
        String message;
        if( (message = messageHashtable.get(i.getModel().getElementAt(index))) == null ) {
            messageTextField.setText("");
        } else {
            messageTextField.setText(message);
        }
        // deselect other elements
        if( !neutralsList.equals(i) ) {
            neutralsList.clearSelection();
        }
        if( !enemiesList.equals(i) ) {
            enemiesList.clearSelection();
        }
        if( !alliesList.equals(i) ) {
            alliesList.clearSelection();
        }
    }
    
    class CASelectablePlayers implements PlayerSelectable {
        
        public CASelectablePlayers(Player caller, Player called) {
            this.caller = caller;
            this.called = called;
        }
        
        public boolean select(Player p) {
            if( p.equals(caller)  || p.equals(called) ) {
                return false;
            }
            return pr.getSimpleRelation(caller.getNum(), p.getNum()) == PlayersRelation.RelationType.WAR;
        }
        
        protected Player caller;
        protected Player called;
    }
    
    private JLabel alliesLabel;
    private JLabel neutralsLabel;
    private JLabel enemiesLabel;
    private JList alliesList;
    private JList neutralsList;
    private JList enemiesList;
    private JButton neutral1Button;
    private JButton neutral2Button;
    private JButton allyButton;
    private JButton warButton;
    private JLabel messageLabel;
    private JTextField messageTextField;
    private JButton sendButton;
    private JList commandsList;
    private JButton gaAllButton;
    private JButton caButton;
    private JButton deleteButton;
    
    private PlayersRelation pr;
    private CommandCollection cc;
    private Game game;
    private Hashtable<String, String> messageHashtable = new Hashtable<String, String>();
    private Vector<Command> diplomacyCommands = new Vector<Command>();
    
} // Diplomacy
