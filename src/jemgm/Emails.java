/*
 * Emails.java
 *
 * Created on February 10, 2004, 12:33 PM
 */

package jemgm;

import javax.swing.*;
import java.util.Vector;
import java.util.Date;
import java.util.Properties;
import java.io.*;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author  salamon
 */
public class Emails extends javax.swing.JFrame implements ActionListener {
    
    /** Creates new form Emails */
    public Emails(Manager aodm) {
        this.aodm = aodm;
        initComponents();
        fillComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        botPanel = new javax.swing.JPanel();
        botEmailLabel = new javax.swing.JLabel();
        botEmailTextField = new javax.swing.JTextField();
        bccCheckBox = new javax.swing.JCheckBox();
        playersPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scrollPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        grabButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(null);

        setTitle("Emails");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        botPanel.setLayout(null);

        botPanel.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED), new javax.swing.border.TitledBorder("Bot")));
        botEmailLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        botEmailLabel.setText("E-mail");
        botEmailLabel.setToolTipText("E-mail address of the bot. Commands will be sent to this address.");
        botEmailLabel.setMaximumSize(new java.awt.Dimension(40, 15));
        botEmailLabel.setMinimumSize(new java.awt.Dimension(40, 15));
        botEmailLabel.setPreferredSize(new java.awt.Dimension(40, 15));
        botPanel.add(botEmailLabel);
        botEmailLabel.setBounds(10, 30, 50, 30);

        botEmailTextField.setText("asalamon@patkany.salamon.hu");
        botEmailTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botEmailTextFieldActionPerformed(evt);
            }
        });

        botPanel.add(botEmailTextField);
        botEmailTextField.setBounds(70, 30, 420, 30);

        bccCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        bccCheckBox.setSelected(true);
        bccCheckBox.setText("BCC to the sender");
        botPanel.add(bccCheckBox);
        bccCheckBox.setBounds(10, 70, 130, 32);

        getContentPane().add(botPanel);
        botPanel.setBounds(0, 0, 510, 110);

        playersPanel.setLayout(null);

        playersPanel.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED), new javax.swing.border.TitledBorder("Player Emails")));
        scrollPanel.setLayout(null);

        jScrollPane1.setViewportView(scrollPanel);

        playersPanel.add(jScrollPane1);
        jScrollPane1.setBounds(10, 30, 480, 200);

        getContentPane().add(playersPanel);
        playersPanel.setBounds(0, 110, 510, 250);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        grabButton.setText("Grab");
        grabButton.setToolTipText("Grab E-mail addresses from reports file");
        grabButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grabButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(grabButton);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel);
        buttonPanel.setBounds(0, 360, 510, 50);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-549)/2, (screenSize.height-443)/2, 549, 443);
    }//GEN-END:initComponents

    private void grabButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grabButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load report file");

        int retVal = fc.showOpenDialog(this);
        if( retVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }
        
        String fileName = fc.getSelectedFile().getPath();
        String []prefixes = new String[aodm.getGame().getPlayerNum()];
        for( int i=0; i<prefixes.length; ++i ) {
            prefixes[i] = "| "+aodm.getGame().getPlayer(i).abbrev;
        }
        try {
            BufferedReader reportIn = new BufferedReader(new FileReader(fileName));
            while( reportIn.ready() ) {
                String line = reportIn.readLine();
                int i=0;
                while( i<prefixes.length && !line.startsWith(prefixes[i]) ) {
                    ++i;
                }
                if( i<prefixes.length ) {
                    // found
                    String name = line.substring(8,38);
                    String email = line.substring(39);
                    int spacePos = email.indexOf(' ');
                    email = email.substring(0, spacePos);
                    playerTextFields[i].setText(email);
                }
            }
        } catch( IOException e ) {
            System.out.println("grab Emails Exception: "+e);
            JOptionPane.showMessageDialog(this, "Cannot grab e-mails", "Warning", JOptionPane.WARNING_MESSAGE);            
        }
        
    }//GEN-LAST:event_grabButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Add your handling code here:
        aodm.getGame().setBotEmail(botEmailTextField.getText());
	aodm.bccSelf = bccCheckBox.isSelected();
        Player p;
        for( int i=1; i<aodm.getGame().getPlayerNum(); ++i ) {
            p = aodm.getGame().getPlayer(i);            
            p.setEmail(playerTextFields[i].getText());
        }
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void fillComponents() {
        // bot email
        botEmailTextField.setText(aodm.getGame().getBotEmail());
	// bcc
	bccCheckBox.setSelected(aodm.bccSelf);
	// player e-mails
        playerLabels = new JLabel[aodm.getGame().getPlayerNum()];
        playerTextFields = new JTextField[aodm.getGame().getPlayerNum()];
	playerSendButtons = new JButton[aodm.getGame().getPlayerNum()];
	playerSendWholeButtons = new JButton[aodm.getGame().getPlayerNum()];
        Player p;
        scrollPanel.setLayout(new java.awt.GridLayout(aodm.getGame().getPlayerNum(), 1));
	Dimension d = new JLabel("Denethuorin").getPreferredSize();
        for( int i=1; i<aodm.getGame().getPlayerNum(); ++i ) {
	    JPanel mapControls = new JPanel();
	    mapControls.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 1));
            p = aodm.getGame().getPlayer(i);
            playerLabels[i] = new JLabel(p.name);
	    playerLabels[i].setPreferredSize(d);
            playerTextFields[i] = new JTextField(p.getEmail(),20);
	    playerSendButtons[i] = new JButton("Send map");
	    playerSendButtons[i].addActionListener(this);
	    playerSendWholeButtons[i] = new JButton("Send whole map");
	    playerSendWholeButtons[i].addActionListener(this);

            mapControls.add(playerLabels[i]);
            mapControls.add(playerTextFields[i]);   
	    mapControls.add(playerSendButtons[i]);
	    mapControls.add(playerSendWholeButtons[i]);
	    scrollPanel.add(mapControls);
        }
    }
    
    private void botEmailTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botEmailTextFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_botEmailTextFieldActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        Object source = evt.getSource();
	boolean onlyMyMap=true;
	int playerIndex = -1;
	for( int i=0; i<aodm.getGame().getPlayerNum(); ++i ) {
	    if( source.equals(playerSendButtons[i] ) ) {
		onlyMyMap = true;
		playerIndex = i;
		break;
	    }
	}
	for( int i=0; i<aodm.getGame().getPlayerNum(); ++i ) {
	    if( source.equals(playerSendWholeButtons[i] ) ) {
		onlyMyMap = false;
		playerIndex = i;
		break;
	    }
	}
	if( playerIndex > -1 ) {
	    //		AreaDataBase adb = AODParser.getParser().mapCollectionProcess(aodm.getActTurnNumber(), onlyMyMap);
	    AreaDataBase adb = aodm.getGame().getMapcoll().calculateAreaDatabase(aodm.getActTurnNumber(), onlyMyMap);
	    String mapMailMessage = adb.createWinAODFile();
	    System.out.println(mapMailMessage);
	    sendMapMail(playerTextFields[playerIndex].getText(), mapMailMessage);
	}

    }

    public void sendMapMail(String toAddress, String mapMailMessage) {
	try {
	    String fromAddress = aodm.getGame().getPlayer().getEmail();
	    if( fromAddress == null || fromAddress.equals("") ) {
		JOptionPane.showMessageDialog(this, "Please fill out you e-mail address", "Email warning", JOptionPane.WARNING_MESSAGE);
	    } else {
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(fromAddress));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress, false));
		if( aodm.bccSelf ) {
		    msg.setRecipients(Message.RecipientType.BCC,
				      InternetAddress.parse(fromAddress, false));
		}
		msg.setSubject(aodm.getGame().getGameId());
		msg.setText(mapMailMessage);
		msg.setSentDate(new Date());
		Transport.send(msg);
		JOptionPane.showMessageDialog(this, "E-mail sent to "+toAddress+" address", "E-mail", JOptionPane.INFORMATION_MESSAGE);
	    }
	} catch ( MessagingException me ) {
	    me.printStackTrace();
	    System.out.println("me:"+me);
	    JOptionPane.showMessageDialog(this, "Cannot send mail: "+me, "Mail Error", JOptionPane.ERROR_MESSAGE);
	}

    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox bccCheckBox;
    private javax.swing.JLabel botEmailLabel;
    private javax.swing.JTextField botEmailTextField;
    private javax.swing.JPanel botPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton grabButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel playersPanel;
    private javax.swing.JPanel scrollPanel;
    // End of variables declaration//GEN-END:variables
    private JLabel []playerLabels;
    private JTextField []playerTextFields;
    private JButton []playerSendButtons;
    private JButton []playerSendWholeButtons;
    private Manager aodm;
}
