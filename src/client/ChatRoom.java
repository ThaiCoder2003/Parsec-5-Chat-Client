package client;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ChatRoom{
	private JFrame frame;
	private JPanel title;
	private JButton send_message;
	private JButton choose_file;
	private String username;
	private JLabel reciever=new JLabel(" ");
	JComboBox<String>rooms=new JComboBox<String>();
	private JTextField text;
	private JScrollPane chatPanel;
	private JTextPane chatWin;
	
	private HashMap<String, JTextPane> chatWindows = new HashMap<String, JTextPane>();
	
	private DataInputStream in;
	private DataOutputStream out;
	
	Thread Reciever;
	
	private void autoScroll() {
		chatPanel.getVerticalScrollBar().setValue(chatPanel.getVerticalScrollBar().getMaximum());
	}
	
	public void setUsername(String username) {
		this.username=username;
	}
	
	public void setDataInputStream(DataInputStream in) {
		this.in=in;
	}
	
	public void setDataOutputStream(DataOutputStream out) {
		this.out=out;
	}
	
	private void TextMessage(String username, String time, String text, boolean yours) {
		StyledDocument doc;
		
		if (username.equals(this.username)) {
			doc=chatWindows.get(reciever.getText()).getStyledDocument();
		}
		
		else {
			doc=chatWindows.get(username).getStyledDocument();
		}
		
		Style timeStyle = doc.getStyle("Time style");
		if (timeStyle == null) {
			timeStyle = doc.addStyle("Time style", null);
			StyleConstants.setForeground(timeStyle, Color.gray);
			StyleConstants.setItalic(timeStyle, true);
		}
		
		try {
			doc.insertString(doc.getLength(), time+" ", timeStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Style userStyle=doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}
		
		if (yours==true) {
			StyleConstants.setForeground(userStyle, Color.RED);
		}
		
		else {
			StyleConstants.setForeground(userStyle, Color.BLUE);
		}
		
		try {
			doc.insertString(doc.getLength(), username+": ", userStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Style textStyle=doc.getStyle("Text Style");
		if (textStyle == null) {
			textStyle = doc.addStyle("Text style", null);
			StyleConstants.setForeground(textStyle, Color.BLACK);
			StyleConstants.setBold(textStyle, true);
		}
		
		try {
			doc.insertString(doc.getLength(), text+"\n", textStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		autoScroll();
	}
	
	private void EmojiMessage(String username, String time, String emoji, boolean yours) {
		StyledDocument doc;
		
		if (username.equals(this.username)) {
			doc=chatWindows.get(reciever.getText()).getStyledDocument();
		}
		
		else {
			doc=chatWindows.get(username).getStyledDocument();
		}
		
		Style timeStyle = doc.getStyle("Time style");
		if (timeStyle == null) {
			timeStyle = doc.addStyle("Time style", null);
			StyleConstants.setForeground(timeStyle, Color.gray);
			StyleConstants.setItalic(timeStyle, true);
		}
		
		try {
			doc.insertString(doc.getLength(), time+" ", timeStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Style userStyle=doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}
		
		if (yours==true) {
			StyleConstants.setForeground(userStyle, Color.RED);
		}
		
		else {
			StyleConstants.setForeground(userStyle, Color.BLUE);
		}
		
		try {
			doc.insertString(doc.getLength(), username+": ", userStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Style iconStyle=doc.getStyle("Icon Style");
		if (iconStyle == null) {
			iconStyle = doc.addStyle("Icon style", null);
			StyleConstants.setForeground(iconStyle, Color.BLACK);
			StyleConstants.setIcon(iconStyle, new ImageIcon(emoji));
		}
		
		try {
			doc.insertString(doc.getLength(), "something here", iconStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		try {
			doc.insertString(doc.getLength(), "\n", userStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		autoScroll();
	}
	
	private void FileMessage(String username, String time, String filename, byte[] file, boolean yours) {
		StyledDocument doc;
		String window=null;
		if (username.equals(this.username)) {
			window=reciever.getText();
		}
		
		else {
			window=username;
		}
		
		doc=chatWindows.get(window).getStyledDocument();
		
		Style timeStyle = doc.getStyle("Time style");
		if (timeStyle == null) {
			timeStyle = doc.addStyle("Time style", null);
			StyleConstants.setForeground(timeStyle, Color.gray);
			StyleConstants.setItalic(timeStyle, true);
		}
		
		try {
			doc.insertString(doc.getLength(), time+" ", timeStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Style userStyle=doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}
		
		if (yours==true) {
			StyleConstants.setForeground(userStyle, Color.RED);
		}
		
		else {
			StyleConstants.setForeground(userStyle, Color.BLUE);
		}
		
		try {
			doc.insertString(doc.getLength(), username+": ", userStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Style linkStyle=doc.getStyle("Link Style");
		if (linkStyle == null) {
			linkStyle = doc.addStyle("Link style", null);
			StyleConstants.setForeground(linkStyle, Color.magenta);
			StyleConstants.setUnderline(linkStyle, true);
			StyleConstants.setBold(linkStyle, true);
		}
		
		if (chatWindows.get(window).getMouseListeners()!=null) {
			
		}
		
		try {
			doc.insertString(doc.getLength(), "\n", userStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		autoScroll();
	}
	
	private void design() {
		frame=new JFrame();
		
		FlowLayout flow = new FlowLayout(); // Create a layout manager
	    flow.setVgap(10);
	   	    
		frame.setTitle("Parsec 5 Login");	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container pane=frame.getContentPane();
		pane.setPreferredSize(new Dimension(400, 220));
		
		JLabel text1=new JLabel();
		text1.setText("PARSEC 5 CHAT");
		text1.setFont(new Font("Arial", Font.BOLD, 36));
		JPanel head1=new JPanel();
		head1.setBackground(Color.orange);
		head1.add(text1);
		JPanel head2=new JPanel();
		
		JLabel text2=new JLabel();
		text2.setText("Chat");
		text2.setFont(new Font("Arial", Font.PLAIN, 20));
		head2.add(text2);
		title=new JPanel();
		title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
		title.add(head1);
		title.add(Box.createVerticalGlue());
		title.add(head2);
		
		pane.add(title, BorderLayout.PAGE_START);
		
		JPanel body=new JPanel();
		JLabel choose=new JLabel("Choose who to chat: ");
		body.add(choose);
		body.add(rooms);
		chatWin=new JTextPane();
		body.add(chatWin);
		
		frame.add(body, BorderLayout.CENTER);
		
		chatWindows.put(" ", new JTextPane());
		chatWin=chatWindows.get(" ");
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
//	class Receiver implements Runnable{
//		
//	}
	
	class HyperLinkListener extends AbstractAction{
		String filename;
		byte[] file;
		
		public HyperLinkListener(String filename, byte[] file) {
			this.filename=filename;
			this.file=Arrays.copyOf(file, file.length);
		}
		public void actionPerformed(ActionEvent e) {
			execute();
		}
		
		public void execute() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(filename));
			int rVal = fileChooser.showSaveDialog(null);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				
				File saveFile = fileChooser.getSelectedFile();
				BufferedOutputStream bos = null;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(saveFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				

				int nextAction = JOptionPane.showConfirmDialog(null, "Saved file to " + saveFile.getAbsolutePath() + "\nDo you want to open this file?", "Successful", JOptionPane.YES_NO_OPTION);
				if (nextAction == JOptionPane.YES_OPTION) {
					try {
						Desktop.getDesktop().open(saveFile);
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
				
				if (bos != null) {
					try {
						bos.write(this.file);
						bos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
//	
	public ChatRoom(String username, DataInputStream in, DataOutputStream out) {
		setUsername(username);
		design();
	}
	
	public void save_chat() {
	
	}
}