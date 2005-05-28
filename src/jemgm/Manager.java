package jemgm;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * AODManager.java
 *
 *
 * Created: Sun Feb 17 13:43:06 2002
 *
 * @author Salamon Andras
 */
public class Manager extends JFrame implements ActionListener, ItemListener {
    
    private Manager() {
        initComponents();
	loadProperties();
    }

    public static Manager getInstance() {
        if( __instance == null ) {
            __instance = new Manager();
        }
        return __instance;
    }

    private void initComponents() {
        setSize(700,400);
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newGameMenuItem = new JMenuItem("New Game");
        openGameMenuItem = new JMenuItem("Open Game");
        saveGameMenuItem = new JMenuItem("Save Game");
        exitMenuItem = new JMenuItem("Exit");

        fileMenu.add(newGameMenuItem);
        fileMenu.add(openGameMenuItem);
        fileMenu.add(saveGameMenuItem);
        fileMenu.addSeparator();
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        viewMenu = new JMenu("View");
        relationsMenuItem = new JMenuItem("Relations");
	allianceMenuItem = new JMenuItem("Alliance Headlines");
	chartsMenuItem = new JMenuItem("Charts");
        viewMenu.add(relationsMenuItem);
	viewMenu.add(allianceMenuItem);
	viewMenu.add(chartsMenuItem);
        menuBar.add(viewMenu);

        commandsMenu = new JMenu("Commands");
        diplomacyMenuItem = new JMenuItem("Diplomacy");
        victoryConditionsMenuItem = new JMenuItem("Victory Conditions");
        viewMenuItem = new JMenuItem("View");
        validateMenuItem = new JMenuItem("Validate");
        sendMenuItem = new JMenuItem("Send via E-mail");
        commandsMenu.add(diplomacyMenuItem);
        commandsMenu.add(victoryConditionsMenuItem);
        commandsMenu.addSeparator();
	commandsMenu.add(viewMenuItem);
        commandsMenu.add(validateMenuItem);
        commandsMenu.add(sendMenuItem);
        menuBar.add(commandsMenu);

        optionsMenu = new JMenu("Options");
        unitImagesMenuItem = new JCheckBoxMenuItem("Unit Images", true);
        Unit.setShowUnitImages(unitImagesMenuItem.getState());
	colorsMenuItem = new JMenuItem("Colors");
        emailMenuItem = new JMenuItem("Emails");
        optionsMenu.add(unitImagesMenuItem);
	optionsMenu.add(colorsMenuItem);
        optionsMenu.addSeparator();        
        optionsMenu.add(emailMenuItem);
        menuBar.add(optionsMenu);

        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    exitForm(evt);
                }
            });

	popupMenu = new JPopupMenu();
	cancelMenuItem = new JMenuItem("Cancel Command");
	cancelMenuItem.addActionListener(this);
	popupMenu.add(cancelMenuItem);
	
	

        newGameMenuItem.addActionListener(this);
        openGameMenuItem.addActionListener(this);
        saveGameMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);
        relationsMenuItem.addActionListener(this);
        allianceMenuItem.addActionListener(this);
        chartsMenuItem.addActionListener(this);
        diplomacyMenuItem.addActionListener(this);
        victoryConditionsMenuItem.addActionListener(this);
        viewMenuItem.addActionListener(this);
        validateMenuItem.addActionListener(this);
        sendMenuItem.addActionListener(this);
        unitImagesMenuItem.addItemListener(this);
	colorsMenuItem.addActionListener(this);
        emailMenuItem.addActionListener(this);

        setJMenuBar(menuBar);

        commandButtonPanel = new JPanel();
        FlowLayout commandButtonLayout = new FlowLayout(FlowLayout.LEFT,1,1);
        commandButtonPanel.setLayout(commandButtonLayout);
	commandButtonGroup = new ButtonGroup();

	CommandType []cTypes = CommandType.commandTypes;
	commandButtons = new JToggleButton[cTypes.length];
	for( int i=0; i<cTypes.length; ++i ) {
	    commandButtons[i] = new JToggleButton(cTypes[i].abbrev);
	    commandButtons[i].setToolTipText(cTypes[i].name);
	    if( cTypes[i].color == null ) {
		commandButtons[i].setVisible(false);
	    }
	    commandButtons[i].addActionListener(this);
	    commandButtonGroup.add(commandButtons[i]);
	    commandButtonPanel.add(commandButtons[i]);
	}
        commandPanel = new JPanel();
        reportsButton = new JButton("Reports");
        openButton = new JButton("Open");
        saveButton = new JButton("Save");
        commentButton = new JToggleButton("Comment");

        navigatorPanel = new JPanel();
        navigatorPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        firstButton = new JButton("|<");
        prevButton  = new JButton("<");
        nextButton  = new JButton(">");
        lastButton  = new JButton(">|");

        strengthLabel = new JLabel("");

        reportsButton.addActionListener(this);
        openButton.addActionListener(this);
        saveButton.addActionListener(this);
        commentButton.addActionListener(this);
        firstButton.addActionListener(this);
        prevButton.addActionListener(this);
        nextButton.addActionListener(this);
        lastButton.addActionListener(this);

        FlowLayout commandLayout = new FlowLayout(FlowLayout.LEFT);
        commandPanel.setLayout(commandLayout);
        commandPanel.add(reportsButton);
        commandPanel.add(openButton);
        commandPanel.add(saveButton);
        commandPanel.add(commentButton);

        navigatorPanel.add(firstButton);
        navigatorPanel.add(prevButton);
        navigatorPanel.add(nextButton);
        navigatorPanel.add(lastButton);
        commandPanel.add(navigatorPanel);
        //commandPanel.add(commandButtonPanel);
        commandPanel.add(strengthLabel);
        
        northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(2,1));
        northPanel.add(commandPanel);
        northPanel.add(commandButtonPanel);
        map = new HexMap(this);
        map.setBackground(HexMap.unknownColor);        
	map.addMouseListener(new PopupListener());
	commentTextArea = new JTextArea(40,40);
	//commentTextArea = new JTextArea();
	commentTextArea.setVisible(false);

	split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, map, commentTextArea);
	split.setResizeWeight(1);
        
        getContentPane().add(northPanel, BorderLayout.NORTH);
	//        getContentPane().add(map, BorderLayout.CENTER);
        getContentPane().add(split, BorderLayout.CENTER);

        statusPanel = new JPanel();
        statusLabel = new JLabel(":");
        statusLabel.setBackground(Color.white);
        commandLabel = new JLabel(">");
        commandLabel.setBackground(Color.yellow);
	commandLabel.setOpaque(true);
        statusPanel.setLayout(new GridLayout(2,1));
        statusPanel.add(statusLabel);
        statusPanel.add(commandLabel);

        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        setTitle(windowTitle);
        setGameLoaded(false);
    }

    public void itemStateChanged(ItemEvent evt) {
        Object source = evt.getSource();
        if( source.equals(unitImagesMenuItem) ) {
            Unit.setShowUnitImages(unitImagesMenuItem.getState());
            map.needRepaint = true;
            map.repaint();
            validate();
        }
    }

    class PopupListener extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
	    maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
	    maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		popupX = e.getX();
		popupY = e.getY();
		// enable/disable cancel
		Dimension d = map.getAreaPlace(popupX, popupY);
		AreaInformation ai = map.getAdb().getAreaInformation(map.getAdb().getId(d.width,d.height));
		boolean cancelEnabled = false;
		if( ai != null ) {
		    System.out.println("ai.getId:"+ai.getId());
		    int index = map.getCc().getCommandIndexById(ai.getId());
		    if( index != -1 ) {
			cancelEnabled = true;
		    }
		} else {
		    int w = map.shift(d.width,  map.getAdb().getXSize(), getGame().getShiftX());
		    int h = map.shift(d.height, map.getAdb().getYSize(), getGame().getShiftY());
		    int param=10000+w*100+h;
		    Command c = map.getCc().getCommandByType(CommandType.SP);
		    if( c != null ) {
			int i=0;			
			while( i<c.getParamNum() && c.getIntParam(i) != param ) {
			    ++i;
			}
			if( i < c.getParamNum() ) {
			    System.out.println("cancel special spy");
			    cancelEnabled = true;
			}
		    }
		}
		cancelMenuItem.setEnabled(cancelEnabled);
		popupMenu.show(e.getComponent(),
			   e.getX(), e.getY());
	    }
	}
    }


    public  void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if( source.equals(newGameMenuItem) ) {
            System.out.println("new Game");
            newGame();
        } else if( source.equals(exitMenuItem) ) {
            System.out.println("exit");
            exitForm(null);
        } else if( source.equals(openGameMenuItem) || source.equals(openButton) ) {
            openGame();
        } else if( source.equals(saveGameMenuItem) || source.equals(saveButton) ) {
            saveGame();
        } else if( source.equals(commentButton) ) {
            if( commentTextArea.isVisible() ) {
                commentTextArea.setVisible(false);
		splitDividerLocation = split.getDividerLocation();
		//                getContentPane().remove(commentTextArea);
            } else {
                commentTextArea.setVisible(true);
		split.setDividerLocation(splitDividerLocation);
		//                getContentPane().add(commentTextArea, BorderLayout.EAST);
            }
            validate();
        } else if( source.equals(relationsMenuItem) ) {
            VisualPlayersRelation vpr = new VisualPlayersRelation(getTurn(getActTurnNumber()).getPr());
            vpr.setVisible(true);
        } else if( source.equals(allianceMenuItem) ) {
	    String allianceHeadlines =getTurn(getActTurnNumber()).getPr().getAllianceHeadlines();
	    JOptionPane.showMessageDialog(this, allianceHeadlines, "Alliance Headlines", JOptionPane.INFORMATION_MESSAGE);            

        } else if( source.equals(chartsMenuItem) ) {
	    System.out.println("charts");
	    Charts.getInstance(this).setVisible(true);
        } else if( source.equals(diplomacyMenuItem) ) {
            Diplomacy dip = new Diplomacy(getGame(), getTurn(getActTurnNumber()).getPr(), map.getCc());
            dip.setVisible(true);
        } else if( source.equals(victoryConditionsMenuItem) ) {
            VictoryConditions vc = new VictoryConditions(game, map.getCc());
            vc.setVisible(true);
        } else if( source.equals(sendMenuItem) ) {
	    try {
		//		String fromAddress = "asalamon@hu.inter.net";
		String fromAddress = game.getPlayer().getEmail();
		if( fromAddress == null || fromAddress.equals("") ) {
		    JOptionPane.showMessageDialog(this, "Please fill out you e-mail address", "Email warning", JOptionPane.WARNING_MESSAGE);
		} else {
		    Properties props = System.getProperties();
		    Session session = Session.getDefaultInstance(props, null);
		    Message msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress(fromAddress));
		    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getGame().getBotEmail(), false));
		    if( bccSelf ) {
			msg.setRecipients(Message.RecipientType.BCC,
					  InternetAddress.parse(fromAddress, false));
		    }

		    msg.setSubject("EMGTURN");
		    msg.setText(map.getCc().toString());
		    msg.setSentDate(new Date());
		    Transport.send(msg);
		    JOptionPane.showMessageDialog(this, "E-mail sent to "+getGame().getBotEmail()+" address", "E-mail", JOptionPane.INFORMATION_MESSAGE);
		}
	    } catch ( MessagingException me ) {
		me.printStackTrace();
		System.out.println("me:"+me);
		JOptionPane.showMessageDialog(this, "Cannot send mail: "+me, "Mail Error", JOptionPane.ERROR_MESSAGE);
	    }
	} else if( source.equals(viewMenuItem) ) {
	    // TODO: change it to something nicer
	    JOptionPane.showMessageDialog(this, ""+map.getCc(), "Commands", JOptionPane.INFORMATION_MESSAGE);            
        } else if( source.equals(emailMenuItem) ) {
            System.out.println("emailMenuItem selected");
            Emails emails = new Emails(this);
            emails.setVisible(true);
        } else if( source.equals(colorsMenuItem) ) {
            System.out.println("colors menuitem selected");
            Colors colors = Colors.getInstance(game);
            colors.setVisible(true);
        } else if( source.equals(validateMenuItem) ) {
            Turn t = (Turn)getGame().getTurn(getActTurnNumber());
            String[] errors = t.validate();
            if( errors == null ) {
                System.out.println("No Error");
            } else {
                System.out.println("Error");
                String errorMessage="";
                for( int i=0; i<errors.length; ++i ) {
                    errorMessage += errors[i] + "\n";
                }
                JOptionPane.showMessageDialog(this, errorMessage, "Validation Errors", JOptionPane.ERROR_MESSAGE);
            }
        } else if( source.equals(reportsButton) ) {
            System.out.println("reports Button");
            ReportManager rm = new ReportManager(this, getGame());
            rm.setVisible(true);
            System.out.println("latestturn:"+getGame().getMapcoll().getLatestTurn());
            // TODO: increase 1
            getGame().removeTurnsAfter(1);
            setActTurnNumber(getGame().getMapcoll().getLatestTurn());
        } else if( source.equals(firstButton) ) {
            setActTurnNumber(1);
        } else if( source.equals(prevButton) ) {
            setActTurnNumber(getActTurnNumber() == 1 ? 1 : getActTurnNumber() -1);
        } else if( source.equals(nextButton) ) {
            setActTurnNumber(getActTurnNumber() == getGame().getMapcoll().getLatestTurn() ? getGame().getMapcoll().getLatestTurn() : getActTurnNumber() +1);
        } else if( source.equals(lastButton) ) {
            setActTurnNumber(getGame().getMapcoll().getLatestTurn());
	} else if( source.equals(cancelMenuItem) ) {
            Dimension d = map.getAreaPlace(popupX, popupY);
            AreaInformation ai = map.getAdb().getAreaInformation(map.getAdb().getId(d.width,d.height));
	    if( ai != null ) {
		System.out.println("ai.getId:"+ai.getId());
		int index = map.getCc().getCommandIndexById(ai.getId());
		if( index != -1 ) {
		    Command c = map.getCc().getCommand(index);
		    if( c.getType() != CommandType.SP ) {
			map.getCc().removeCommand(index);
		    } else {
			c.removeParam(""+ai.getId());
			System.out.println("new c:"+c);
			map.getCc().addCommand(c);
			System.out.println("new cc:"+map.getCc());
			actCommand = null;
			commandButtonGroup.setSelected(commandButtonGroup.getSelection(), false);
		    }
		    map.needRepaint = true;
		    map.repaint();
		}
	    } else {
		// cancel special spy
		int w = map.shift(d.width,  map.getAdb().getXSize(), getGame().getShiftX());
		int h = map.shift(d.height, map.getAdb().getYSize(), getGame().getShiftY());
		int param=10000+w*100+h;
		Command c = map.getCc().getCommandByType(CommandType.SP);
		if( c != null ) {
		    int i=0;			
		    while( i<c.getParamNum() && c.getIntParam(i) != param ) {
			++i;
		    }
		    if( i < c.getParamNum() ) {
			// CANCEL
		    }
		}

	    }
	} else {
	    for( int i=0; i<commandButtons.length; ++i ) {
		if( source.equals(commandButtons[i]) ) {
		    commandType = CommandType.commandTypes[i];
		    if( commandType.equals( CommandType.SP ) ) {
			actCommand = map.getCc().getCommandByType(CommandType.SP);
			initSPCommand(actCommand);
		    } else {
			actCommand = new Command(game,commandType);
			setParamIndex(0);
		    }
		}
	    }
	    if( source instanceof JMenuItem ) {
		String text = ((JMenuItem)source).getText();
		if( text.endsWith(".emg") ) {		    
		    File file = new File(text);
		    int index = text.lastIndexOf(File.separator);
		    File dir = new File(text.substring(0, index));
		    openGame(dir, file);
		}
	    }
	}
        updateCommandLabel();
    }

    protected void initSPCommand(Command c) {
        System.out.println("initSP c:"+c);
        if( c != null ) {
            // copy info from the existing command
            System.out.println("c.getPN:"+c.getParamNum());
            for( int i=0; i<c.getParamNum(); ++i ) {
                commandParam[i] = c.getIntParam(i);
            }
            setParamIndex(c.getParamNum());
        }
    }


    private void exitForm(WindowEvent evt) {
        System.exit(0);
    }

    public static void main(String args[]) {
	//        getInstance().setSkin("lib/aquathemepack.zip");        
	//        getInstance().setSkin("lib/toxicthemepack.zip");        
	getInstance().setSkin("lib/themepack.zip");        
        getInstance().setVisible(true);
        //getInstance().setSkin("lib/themepack.zip");        
    }
    
    public void setSkin(String theme) {
        try {
            SkinLookAndFeel.setSkin(SkinLookAndFeel.loadThemePack(theme));
            SkinLookAndFeel.enable();
            SwingUtilities.updateComponentTreeUI(this);
        } catch( Exception e ) {
            e.printStackTrace();
            System.out.println("Exception: "+e);
        }
    }
    
    public void saveProperties() {
	String dirName = System.getProperty("user.home") + File.separator + ".aodm/";
	File dir = new File(dirName);
	if( !dir.exists() ) {
	    dir.mkdir();
	}
        try {
            BufferedWriter propsOut = new BufferedWriter(new FileWriter(dirName + "properties"));
            propsOut.write("unitImages: "+unitImagesMenuItem.getState()+"\n");
            propsOut.write("bccSelf: "+bccSelf+"\n");
            for (Enumeration e = recents.elements() ; e.hasMoreElements() ;) {
                propsOut.write("recent:"+e.nextElement()+"\n");
            }
            propsOut.flush();
            propsOut.close();
        } catch( IOException e ) {
            System.err.println("Properties file writing problem");
        }
	    
    }

    public void loadProperties() {
	String dirName = System.getProperty("user.home") + File.separator + ".aodm/";
	int recentPos = 4;
        try {
            BufferedReader propsIn = new BufferedReader(new FileReader(dirName + "properties"));
	    while( propsIn.ready() ) {
		String line = propsIn.readLine();
		if( line.startsWith("unitImages:") ) {
		    // not too nice
		    if( line.indexOf("false") > -1 ) {
			unitImagesMenuItem.setState(false);
		    } else {
			unitImagesMenuItem.setState(true);
		    }
		    Unit.setShowUnitImages(unitImagesMenuItem.getState());
		} else if( line.startsWith("recent:") ) {
                    recents.add(line.substring("recent:".length()));
		    JMenuItem recentItem = new JMenuItem(line.substring("recent:".length()));
                    fileMenu.add(recentItem, recentPos++);
		    recentItem.addActionListener(this);
                } else if( line.startsWith("bccSelf:") ) {
		    if( line.indexOf("false") > -1 ) {
			bccSelf = false;
		    } else {
			bccSelf = true;
		    }		    
		}
	    }
            propsIn.close();
        } catch( IOException e ) {
            System.err.println("Properties file reading problem");
        }
	    
    }

    public void newGame() {
        game = new Game();
	// todo: let the user choose
	game.setGameType("AOD");
        JFileChooser fd = new JFileChooser();
        fd.setDialogTitle("New EMG Game");
        fd.setFileFilter(filter);
        int retVal = fd.showOpenDialog(this);
        if( retVal != JFileChooser.APPROVE_OPTION ) {
	    // user cancelled
            return;
        }
	//        System.out.println("d:"+fd.getCurrentDirectory());
	//        System.out.println("f:"+fd.getSelectedFile());
	//        System.out.println("d2:"+fd.getCurrentDirectory().getPath()+File.separator);

        game.setDirectory(fd.getCurrentDirectory().getPath()+File.separator);
        game.setFile(fd.getSelectedFile().getName());
	game.setBotEmail(defaultBotMailAddress);

        ReportEditor repEditor = new ReportEditor(this, getGame(), ReportEditor.ADD);
        repEditor.setVisible(true);
        if( repEditor.getStatus() == repEditor.OK ) {
            Player p = game.getMapcoll().getMap(0).getPlayer();
            int actPlayerNum = p.getNum();
            game.setPlayer(p);
            setGameLoaded(true);
            setActTurnNumber(1);
            recents.add(0, fd.getSelectedFile().getPath()); 
        }
    }

    public void openGame() {
        System.out.println("open Game:");
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load AOD Game");

        fc.setFileFilter(filter);
        int retVal = fc.showOpenDialog(this);
        if( retVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }
        
        System.out.println("d:"+fc.getCurrentDirectory());
        System.out.println("f:"+fc.getSelectedFile());
        String fileName = fc.getSelectedFile().getPath();
	openGame(fc.getCurrentDirectory(), fc.getSelectedFile());
    }

    public void openGame(File directory, File file) {
	setGame( new Game(file.getPath()));
	System.out.println("End of process");
	getGame().setDirectory(directory.getPath()+File.separator);
	getGame().setFile(file.getName());
	setActTurnNumber(getGame().getMapcoll().getLatestTurn(), true);
	setGameLoaded(true);
	recents.remove(file.getPath());
	recents.add(0, file.getPath()); 
    }

    public void saveGame() {
	saveProperties();
        // saving the game file
        // TODO: save only when necessary
        String fileName = getGame().getDirectory() + getGame().getFile();
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(getGame().toString());
            fw.flush();
            fw.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found "+e);
        } catch (IOException e) {
            System.out.println("IO Exception "+e);
        }

        // saving the command file
        int lastTurn = getGame().getMapcoll().getLatestTurn();
        Turn last = (Turn)getGame().getTurn(lastTurn);
        fileName = getGame().getDirectory() + "commands_"+lastTurn+".dat";
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(last.getCc().toString());
            fw.flush();
            fw.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found "+e);
        } catch (IOException e) {
            System.out.println("IO Exception "+e);
        }
        // saving the comment file
        saveCommentFile(getActTurnNumber());
    }

    int actTurnNumber;
    
    /**
     * Get the value of actTurnNumber.
     * @return Value of actTurnNumber.
     */
    public int getActTurnNumber() {
         return actTurnNumber; 
    }

    /**
     * Set the value of actTurnNumber.
     * @param v  Value to assign to actTurnNumber.
     */
    public void setActTurnNumber(int  v) {
        setActTurnNumber(v, false);
    }    

    public void loadCommentFile(int turnNum) {
        commentTextArea.setText("");
        try {
            BufferedReader commentIn = new BufferedReader(new FileReader(getGame().getDirectory()+"comment_"+turnNum+".txt"));            
            while( commentIn.ready() ) {
                commentTextArea.append(commentIn.readLine()+"\n");
            }
        } catch( IOException e ) {
            System.err.println("Comment file reading problem: "+e);
        }
    }

    public void saveCommentFile(int turnNum) {
        try {
            BufferedWriter commentOut = new BufferedWriter(new FileWriter(getGame().getDirectory()+"comment_"+turnNum+".txt"));
            commentOut.write(commentTextArea.getText());
            commentOut.flush();
            commentOut.close();
        } catch( IOException e ) {
            System.err.println("Comment file writing problem");
        }
    }

    public Turn getTurn(int v) {
        Turn tt = (Turn)getGame().getTurn(v);
        if( tt == null ) {
            System.out.println("No precalculated turn");
            Turn newTurn = new Turn(game, v);
            CommandCollection cc = CommandCollection.readFromFile(game, v);
            if( cc == null ) {
                cc = new CommandCollection(getGame());
            }
            newTurn.setCc(cc);
//             try {
//                 newTurn.setAreadb(AODParser.getParser().mapCollectionProcess(v, false));
//             } catch( ParseException e ) {
//                 System.out.println("Exception: "+e);
//             }
	    newTurn.setAreadb(getGame().mapCollectionProcess(v));
            getGame().setTurn(v, newTurn);
	    return newTurn;
        }
	return tt;
    }

    /**
     * Set the value of actTurnNumber.
     * @param v  Value to assign to actTurnNumber.
     * @param center Center the map
     */
    public void setActTurnNumber(int  v, boolean center) {
        saveCommentFile(actTurnNumber);
        this.actTurnNumber = v;
        setTitle(windowTitle+"  "+getGame().getGameId()+" Turn: "+getActTurnNumber());
	Turn tt = getTurn(v);
	map.setAdb(tt.getAreadb());
	map.setCc(tt.getCc());

	int supply = map.getAdb().getSupplyPointNum(getGame().getPlayer());
	int army = map.getAdb().getArmyStrength(getGame().getPlayer());
	String ending = Math.abs(supply - army) == 1 ? "s" : "";
	String strengthText = "Supply: "+supply+" Army: "+army;
	if( supply > army ) {
	    strengthText += " You may add "+(supply-army)+" unit point"+ending;
	} else if( supply < army ) {
	    strengthText += " You must remove "+(army-supply)+" unit point"+ending;
	}
        strengthLabel.setText(strengthText);

	int playerNum = getGame().getPlayerNum();

	for(int p=1; p<playerNum; ++p ) {
	    System.out.println("plNum:"+p);
	    System.out.println("Player: "+game.getPlayer(p).getName());
	    System.out.println("Victory points: "+map.getAdb().getVictoryPoints(game.getPlayer(p)));
	}

	System.out.println("A");
        if( center ) {
	    System.out.println("A1");
            AreaInformation ai = map.getAdb().getAreaInformationByOwner(getGame().getPlayer());
	    System.out.println("A2 "+ai);
	    if( ai != null ) {
		map.center(ai.getX(1), ai.getY(1));
	    } else {
		map.needRepaint = true;
		map.repaint();
	    }
	    System.out.println("A3");
        } else {
            map.needRepaint = true;
            map.repaint();
        }
	System.out.println("B");
        if( getActTurnNumber() == getGame().getMapcoll().getLatestTurn()) {
            commandButtonPanel.setEnabled(true);
            diplomacyMenuItem.setEnabled(true);
            if( getActTurnNumber() == 1 ) {
                victoryConditionsMenuItem.setEnabled(true);
            } else {
                victoryConditionsMenuItem.setEnabled(false);
            }
        } else {
            commandButtonPanel.setEnabled(false);
            diplomacyMenuItem.setEnabled(false);
            victoryConditionsMenuItem.setEnabled(false);
        }
	System.out.println("C");
	System.gc();
        changeNavigatorButtonsStatus();
        loadCommentFile(actTurnNumber);
    }

    public void setStatusLabel(String text) {
        statusLabel.setText(text);
    }


    boolean gameLoaded;
    
    /**
     * Get the value of gameLoaded.
     * @return Value of gameLoaded.
     */
    public boolean getGameLoaded() {
         return gameLoaded; 
    }
    
    /**
     * Set the value of gameLoaded.
     * @param v  Value to assign to gameLoaded.
     */
    public void setGameLoaded(boolean  v) {
        this.gameLoaded = v;
        navigatorPanel.setEnabled(gameLoaded);
        saveButton.setEnabled(gameLoaded);
        commentButton.setEnabled(gameLoaded);
        reportsButton.setEnabled(gameLoaded);
        commandButtonPanel.setEnabled(gameLoaded);
        viewMenu.setEnabled(gameLoaded);
        commandsMenu.setEnabled(gameLoaded);
        emailMenuItem.setEnabled(gameLoaded);
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


    int paramIndex;
    
    /**
     * Get the value of paramIndex.
     * @return Value of paramIndex.
     */
    public int getParamIndex() {
         return paramIndex; 
    }
    
    /**
     * Set the value of paramIndex.
     * @param v  Value to assign to paramIndex.
     */
    public void setParamIndex(int  v) {
        this.paramIndex = v;
    }

    private boolean isMyUnitHere(int areaId) {
        AreaInformation ai = map.getAdb().getAreaInformation(areaId);
        return ai.getUnitType() != 0 &&
            ai.getUnitOwner() == getGame().getPlayer().getNum();
    }

    private boolean validCommand(Command c) {
        AreaInformation ai1 = map.getAdb().getAreaInformation(c.getIntParam(0));
        AreaInformation ai2 = null;
        if( c.getParamNum() > 1 ) {
            ai2 = map.getAdb().getAreaInformation(c.getIntParam(1));
        }

	CommandType ctype = c.getType();
	if( ctype.equals(CommandType.MO) ) {
            return ai1.isNeighbour(ai2) && isMyUnitHere(c.getIntParam(0)); 
	} else if( ctype.equals(CommandType.SD) ) { 
            return ai1.isNeighbour(ai2) && isMyUnitHere(c.getIntParam(0)); 
	} else if( ctype.equals(CommandType.SA) ) { 
            return ai1.isNeighbour(ai2) && isMyUnitHere(c.getIntParam(0)); 
	} else if( ctype.equals(CommandType.AC) ) { 
            return !isMyUnitHere(c.getIntParam(0)) && 
                ai1.getSupplyPointNum() >= 1; 
	} else if( ctype.equals(CommandType.AA) ) { 	
            return !isMyUnitHere(c.getIntParam(0)) && 
                ai1.getSupplyPointNum() >= 2; 
	} else if( ctype.equals(CommandType.AS) ) { 		
            return !isMyUnitHere(c.getIntParam(0)) && 
                ai1.getSupplyPointNum() >= 1; 
	} else if( ctype.equals(CommandType.AF) ) { 		
            return !isMyUnitHere(c.getIntParam(0)) && 
                ai1.getSupplyPointNum() >= 2;  
	} else if( ctype.equals(CommandType.UU) ) {
	    return isMyUnitHere(c.getIntParam(0));
	} else if( ctype.equals(CommandType.DU) ) {
	    return isMyUnitHere(c.getIntParam(0));
	} else if( ctype.equals(CommandType.RU) ) {
	    return isMyUnitHere(c.getIntParam(0));
        }
        return true; 
    }

    private void addNextCommand() {
        Command c = new Command(game,commandType, commandParam, getParamIndex());
        if( validCommand(c) ) {
            System.out.println("command:"+c);
            map.getCc().addCommand(c);
            System.out.println("cc:"+map.getCc());
            map.needRepaint = true;
            map.repaint();
        } else {
            System.out.println("Invalid command");
            //MessageBox.showMessage("Invalid Command");
            JOptionPane.showMessageDialog(this, "Invalid Command", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setNextCommandParameter(int param) {
        if( commandType == null ) {
            return;
        }
        System.out.println("next Param : "+getParamIndex()+" "+param);

        if( commandType == CommandType.SP ) {
            // delete the area if it's already int the list
            int i=0;
            while( i<getParamIndex() && commandParam[i] != param ) {
                ++i;
            }
            if( i < getParamIndex() ) {
                // found
                System.out.println("index:"+i);
                for( int j=i; j<getParamIndex()-1; ++j ) {
                    commandParam[j] = commandParam[j+1];
                }
                setParamIndex(getParamIndex()-1);
                actCommand = new Command(game,commandType, commandParam, getParamIndex());
                map.getCc().addCommand(actCommand); // clone??
                updateCommandLabel();
                map.needRepaint = true;
                map.repaint();
                return;
            }
        }

        if( getParamIndex() >= Math.abs(commandType.paramNum) ) {
            JOptionPane.showMessageDialog(this, "No more parameters, please!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        commandParam[getParamIndex()] = param;
        setParamIndex(getParamIndex()+1);
        actCommand = new Command(game,commandType, commandParam, getParamIndex());
	System.out.println("next Command Parameter c:"+actCommand);
        updateCommandLabel();
        System.out.println("gpi:"+getParamIndex());
        System.out.println("pn:"+commandType.paramNum  );
        if( getParamIndex() == Math.abs(commandType.paramNum) ||
            commandType.paramNum < 0 ) {
            addNextCommand();
            if( commandType.paramNum < 0 ) {
                if( commandType == CommandType.SP ) {
                    Command c = new Command(game,commandType, commandParam, getParamIndex());
                    initSPCommand(c);
                } 
            }else {
                setParamIndex(0);
            }
        }
    }

    private void updateCommandLabel() {
	if( actCommand != null ) {
	    commandLabel.setText(actCommand.toString());
	} else {
	    System.out.println("null actCommand");
	    String text = "";
	    if( commandType != null ) {
		text = ">"+commandType.abbrev+" ";
		for( int i=0; i<getParamIndex(); ++i ) {
		    text += commandParam[i]+" ";
		}
	    }
	    commandLabel.setText(text);
	}
    }

    private void changeNavigatorButtonsStatus() {
        firstButton.setEnabled(true);
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        lastButton.setEnabled(true);        
        if( getActTurnNumber() == 1 ) {
            firstButton.setEnabled(false);
            prevButton.setEnabled(false);
        } 
	if( getActTurnNumber() == getGame().getMapcoll().getLatestTurn() ) {
            nextButton.setEnabled(false);
            lastButton.setEnabled(false);            
        }
    }

    javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".emg");
        }
            
        public String getDescription() {
            return "emg files";
        }
    };

        
    private JMenuBar  menuBar;
    private JMenu     fileMenu;
    private JMenuItem newGameMenuItem;
    private JMenuItem openGameMenuItem;
    private JMenuItem saveGameMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu     viewMenu;
    private JMenuItem relationsMenuItem;
    private JMenuItem allianceMenuItem;
    private JMenuItem chartsMenuItem;
    private JMenu     commandsMenu;
    private JMenuItem diplomacyMenuItem;
    private JMenuItem victoryConditionsMenuItem;
    private JMenuItem viewMenuItem;
    private JMenuItem sendMenuItem;
    private JMenuItem validateMenuItem;
    private JPopupMenu popupMenu;
    private JMenuItem cancelMenuItem;
    private JMenu     optionsMenu;
    private JCheckBoxMenuItem unitImagesMenuItem;
    private JMenuItem colorsMenuItem;
    private JMenuItem emailMenuItem;
    private JPanel    commandPanel;
    private JPanel    commandButtonPanel;
    private JToggleButton[]   commandButtons;
    private ButtonGroup commandButtonGroup;

    private JPanel    navigatorPanel;
    private JButton   openButton;
    private JButton   saveButton;
    private JToggleButton   commentButton;
    private JButton   reportsButton;
    private JButton   firstButton;
    private JButton   prevButton;
    private JButton   nextButton;
    private JButton   lastButton;
    private HexMap   map;
    private JSplitPane split;
    private JLabel    statusLabel;
    private JLabel    commandLabel;
    private JPanel    statusPanel;
    private JPanel    northPanel;
    private JLabel    strengthLabel;
    private int[]    commandParam = new int[20];
    private CommandType commandType;
    private Command  actCommand;
    private int      splitDividerLocation=500;
    private int      popupX, popupY;

    private JTextArea commentTextArea;
    private static String windowTitle = "AOD Manager";
    private static Manager __instance;
    private static String defaultBotMailAddress = "emg.pbm@shaw.ca";
    public boolean bccSelf = true;
    private Vector<String> recents = new Vector<String>();
} // AODManager
