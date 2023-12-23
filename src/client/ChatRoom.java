package client;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatRoom{
	private static String DB_URL = "jdbc:mysql://localhost:3306/chat_info";
    private static String USER_NAME = "root";
    private static String PASSWORD = "ThaiDangQuoc:)2901";
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
	
	Thread recieveItem;
	
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
	
	private void TextMessagetoGlobal(String username, String time, String text, boolean yours) {
		StyledDocument doc;
		
		doc=chatWindows.get("Global").getStyledDocument();
		
		//create style for time
		Style timeStyle = doc.getStyle("Time style");
		if (timeStyle == null) {
			timeStyle = doc.addStyle("Time style", null);
			StyleConstants.setForeground(timeStyle, Color.gray);
			StyleConstants.setItalic(timeStyle, true);
		}
		
		//print out the time the message was sent
		try {
			doc.insertString(doc.getLength(), time+" ", timeStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		//create style for user
		Style userStyle=doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}
		
		//set text colors; if it is you, it is red, otherwise it is blue
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
		
		Style messageStyle = doc.getStyle("Message style");
		if (messageStyle == null) {
			messageStyle = doc.addStyle("Message style", null);
		    StyleConstants.setForeground(messageStyle, Color.BLACK);
		    StyleConstants.setBold(messageStyle, false);
		}
	   
		// In ra nội dung tin nhắn
	    try { doc.insertString(doc.getLength(), text + "\n",messageStyle); }
        catch (BadLocationException e){}
		
	    autoScroll();
	}
	
	//Load sent messages
	
	private void TextMessagetoPrivate(String reciever, String sender, String time, String text, boolean yours) {
		StyledDocument doc;
		
		//set the chat window where the message is printed out
		
		if (reciever.equals(username)) {
			doc=chatWindows.get(sender).getStyledDocument();
		} else {
			doc=chatWindows.get(reciever).getStyledDocument();
		}
		
		//create style for time
		Style timeStyle = doc.getStyle("Time style");
		if (timeStyle == null) {
			timeStyle = doc.addStyle("Time style", null);
			StyleConstants.setForeground(timeStyle, Color.gray);
			StyleConstants.setItalic(timeStyle, true);
		}
		
		//print out the time the message was sent
		try {
			doc.insertString(doc.getLength(), time+" ", timeStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		//create style for user
		Style userStyle=doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}
		
		if (sender.equals(username)) {
			StyleConstants.setForeground(userStyle, Color.RED);
		}
		
		else {
			StyleConstants.setForeground(userStyle, Color.BLUE);
		}
		
		//print out the sender's message
		try {
			doc.insertString(doc.getLength(), sender+": ", userStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		//create style for message
		Style textStyle=doc.getStyle("Text Style");
		if (textStyle == null) {
			textStyle = doc.addStyle("Text style", null);
			StyleConstants.setForeground(textStyle, Color.BLACK);
			StyleConstants.setBold(textStyle, true);
		}
		
		//print out the message
		try {
			doc.insertString(doc.getLength(), text+"\n", textStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		autoScroll();
	}
	
	private void EmojiMessagetoGlobal(String username, String time, String emoji, boolean yours) {
		StyledDocument doc;
		
		doc=chatWindows.get("Global").getStyledDocument();
		
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
		
		//set the emoji for sent message
		Style iconStyle=doc.getStyle("Icon Style");
		if (iconStyle == null) {
			iconStyle = doc.addStyle("Icon style", null);
			StyleConstants.setForeground(iconStyle, Color.BLACK);
			StyleConstants.setIcon(iconStyle, new ImageIcon(emoji));
		}
		
		//print out the emoji (with a placeholder text)
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
	
	private void EmojiMessagetoPrivate(String reciever, String sender, String time, String emoji) {
		StyledDocument doc;
		
		if (reciever.equals(username)) {
			doc=chatWindows.get(sender).getStyledDocument();
		} else {
			doc=chatWindows.get(reciever).getStyledDocument();
		}
		
		//style for time text
		Style timeStyle = doc.getStyle("Time style");
		if (timeStyle == null) {
			timeStyle = doc.addStyle("Time style", null);
			StyleConstants.setForeground(timeStyle, Color.gray);
			StyleConstants.setItalic(timeStyle, true);
		}
		
		//print time
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
		//Set text color to red
		if (sender.equals(username)) {
			StyleConstants.setForeground(userStyle, Color.RED);
		}
		
		else {
			StyleConstants.setForeground(userStyle, Color.BLUE);
		}
		
		
		//print our user
		try {
			doc.insertString(doc.getLength(), sender+": ", userStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		//set the emoji for sent message
		Style iconStyle=doc.getStyle("Icon Style");
		if (iconStyle == null) {
			iconStyle = doc.addStyle("Icon style", null);
			StyleConstants.setForeground(iconStyle, Color.BLACK);
			StyleConstants.setIcon(iconStyle, new ImageIcon(emoji));
		}
		
		//print out the emoji (with a placeholder text)
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
	
	private void FileMessagetoGlobal(String username, String time, String filename, byte[] file, boolean yours) {
		StyledDocument doc;
		
		doc=chatWindows.get("Global").getStyledDocument();
		
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
		
		//set a file into the message
		Style linkStyle=doc.getStyle("Link Style");
		if (linkStyle == null) {
			linkStyle = doc.addStyle("Link style", null);
			StyleConstants.setForeground(linkStyle, Color.magenta);
			StyleConstants.setUnderline(linkStyle, true);
			StyleConstants.setBold(linkStyle, true);
			linkStyle.addAttribute("file", new HyberLinkListener(filename, file));
		}
		
		//create a mouse listener to save the file
		if (chatWindows.get("Global").getMouseListeners()!=null) {
			chatWindows.get("Global").addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					Element ele = doc.getCharacterElement(chatWin.viewToModel2D(e.getPoint()));
		            AttributeSet as = ele.getAttributes();
		            HyberLinkListener listener = (HyberLinkListener)as.getAttribute("file");
		            if(listener != null)
		            {
		                listener.execute();
		            }
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		try {
			doc.insertString(doc.getLength(), "["+filename+"]", linkStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		autoScroll();
	}
	
	private void loadMessageToGlobal() throws IOException {
		try {
			//connect to mysql server, and get all users into array
			Connection conn=getConnection(DB_URL, USER_NAME, PASSWORD);
			String sql="select * from chat_history where receiver=?";
			PreparedStatement preparedStmt=conn.prepareStatement(sql);
			
			preparedStmt.setString(1, "Global");
			
			ResultSet rs=preparedStmt.executeQuery();
			
			while (rs.next()) {
				String sender=rs.getString("sender");
				String time=rs.getString("date");
				String content_type=rs.getString("content_type");
				String message=rs.getString("content");
				
				if (content_type.equals("Text")) {
					if (sender.equals(this.username)) {
						TextMessagetoGlobal(sender, time, message, true);
					}
					
					else {
						TextMessagetoGlobal(sender, time, message, false);
					}
				}
				
				else if (content_type.equals("Emoji")) {
					if (sender.equals(this.username)) {
						EmojiMessagetoGlobal(sender, time, message, true);
					}
					
					else {
						EmojiMessagetoGlobal(sender, time, message, false);
					}
				}
				
				else if(content_type.equals("File")) {
					String link=".\\file\\sent_file\\"+message;
					File getFile=new File(link);
					
					byte[] file=Files.readAllBytes(getFile.toPath());
					if (sender.equals(this.username)) {
						FileMessagetoGlobal(sender, time, message, file, true);
					}
					
					else {
						FileMessagetoGlobal(sender, time, message, file, false);
					}
				}
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void loadMessageToPrivate(String other) throws IOException {
		try {
			//connect to mysql server, and get all users into array
			Connection conn=getConnection(DB_URL, USER_NAME, PASSWORD);
			String sql="select * from chat_history where (receiver=? and sender=?) or (receiver=? and sender=?)";
			PreparedStatement preparedStmt=conn.prepareStatement(sql);
			
			preparedStmt.setString(1, this.username);
			preparedStmt.setString(2, other);
			preparedStmt.setString(3, other);
			preparedStmt.setString(4, this.username);
			
			ResultSet rs=preparedStmt.executeQuery();
			
			while (rs.next()) {
				String sender=rs.getString("sender");
				String reciever=rs.getString("receiver");
				String time=rs.getString("date");
				String content_type=rs.getString("content_type");
				String message=rs.getString("content");
				
				if (content_type.equals("Text")) {
					if (sender.equals(this.username)) {
						TextMessagetoPrivate(reciever, username, time, message, true);
					}
					
					else {
						TextMessagetoPrivate(username, sender, time, message, false);
					}
				}
				
				else if (content_type.equals("Emoji")) {
					if (sender.equals(this.username)) {
						EmojiMessagetoPrivate(reciever, username,time, message);
					}
					
					else {
						EmojiMessagetoPrivate(username, sender, time, message);
					}
				}
				
				else if(content_type.equals("File")) {
					String link=".\\file\\sent_file\\"+message;
					File getFile=new File(link);
					
					byte[] file=Files.readAllBytes(getFile.toPath());
					if (sender.equals(this.username)) {
						FileMessagetoPrivate(reciever, username, time, message, file);
					}
					
					else {
						FileMessagetoPrivate(username, sender, time, message, file);
					}
				}
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private void FileMessagetoPrivate(String reciever, String sender, String time, String filename, byte[] file) {
		StyledDocument doc;
		String win;
		
		if (reciever.equals(username)) {
			win=sender;
		} else {
			win=reciever;
		}
		
		doc=chatWindows.get(win).getStyledDocument();
		
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
		
		if (sender.equals(username)) {
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
		
		//set a file into the message
		Style linkStyle=doc.getStyle("Link Style");
		if (linkStyle == null) {
			linkStyle = doc.addStyle("Link style", null);
			StyleConstants.setForeground(linkStyle, Color.magenta);
			StyleConstants.setUnderline(linkStyle, true);
			StyleConstants.setBold(linkStyle, true);
			linkStyle.addAttribute("file", new HyberLinkListener(filename, file));
		}
		
		//create a mouse listener to save the file
		if (chatWindows.get(win).getMouseListeners()!=null) {
			chatWindows.get(win).addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					Element ele = doc.getCharacterElement(chatWin.viewToModel2D(e.getPoint()));
		            AttributeSet as = ele.getAttributes();
		            HyberLinkListener listener = (HyberLinkListener)as.getAttribute("file");
		            if(listener != null)
		            {
		                listener.execute();
		            }
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		try {
			doc.insertString(doc.getLength(), "["+filename+"]", linkStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		autoScroll() ;
	}
	
	private void design() {
		frame=new JFrame();
		
		FlowLayout flow = new FlowLayout(); // Create a layout manager
	    flow.setVgap(10);
	   	    
		frame.setTitle("Parsec 5 Chat");	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container pane=frame.getContentPane();
		pane.setPreferredSize(new Dimension(800, 550));
		
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
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		
		JPanel get_rooms=new JPanel();
		get_rooms.setLayout(new BoxLayout(get_rooms, BoxLayout.Y_AXIS));
		JPanel chooseRoom=new JPanel();
		JLabel choose=new JLabel("Choose who to chat: ");
		
		chooseRoom.add(choose);
		rooms.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					reciever.setText((String) rooms.getSelectedItem());
					if (chatWin!=chatWindows.get(reciever.getText())) {
						text.setText("");
						chatWin=chatWindows.get(reciever.getText());
						chatPanel.setViewportView(chatWin);
						
						if (reciever.getText().equals(" ")) {
							text.setEnabled(false);
							send_message.setEnabled(false);
							choose_file.setEnabled(false);
						}
						
						else {
							text.setEnabled(true);
							send_message.setEnabled(true);
							choose_file.setEnabled(true);
						}
					}
				}
			}
		});
		
		JPanel emojiPlace=new JPanel();
		
		JLabel bigSmileIcon = new JLabel(new ImageIcon(".\\file\\emoji\\smile.png"));
		bigSmileIcon.addMouseListener(new IconListener(bigSmileIcon.getIcon().toString()));
		emojiPlace.add(bigSmileIcon);
		
		JLabel smileIcon = new JLabel(new ImageIcon(".\\file\\emoji\\big-smile.png"));
		smileIcon.addMouseListener(new IconListener(smileIcon.getIcon().toString()));
		emojiPlace.add(smileIcon);
		
		JLabel happyIcon = new JLabel(new ImageIcon(".\\file\\emoji\\happy.png"));
		happyIcon.addMouseListener(new IconListener(happyIcon.getIcon().toString()));
		emojiPlace.add(happyIcon);
		
		JLabel loveIcon = new JLabel(new ImageIcon(".\\file\\emoji\\love.png"));
		loveIcon.addMouseListener(new IconListener(loveIcon.getIcon().toString()));
		emojiPlace.add(loveIcon);
		
		JLabel sadIcon= new JLabel (new ImageIcon(".\\file\\emoji\\sad.png"));
		sadIcon.addMouseListener(new IconListener(sadIcon.getIcon().toString()));
		emojiPlace.add(sadIcon);
		
		JLabel madIcon = new JLabel(new ImageIcon(".\\file\\emoji\\mad.png"));
		madIcon.addMouseListener(new IconListener(madIcon.getIcon().toString()));
		emojiPlace.add(madIcon);
		
		JLabel suspiciousIcon= new JLabel (new ImageIcon(".\\file\\emoji\\suspicious.png"));
		suspiciousIcon.addMouseListener(new IconListener(suspiciousIcon.getIcon().toString()));
		emojiPlace.add(suspiciousIcon);
		
		JLabel angryIcon = new JLabel(new ImageIcon(".\\file\\emoji\\angry.png"));
		angryIcon.addMouseListener(new IconListener(angryIcon.getIcon().toString()));
		emojiPlace.add(angryIcon);
		
		JLabel confusedIcon= new JLabel (new ImageIcon(".\\file\\emoji\\confused.png"));
		confusedIcon.addMouseListener(new IconListener(confusedIcon.getIcon().toString()));
		emojiPlace.add(confusedIcon);
		
		JLabel unhappyIcon = new JLabel(new ImageIcon(".\\file\\emoji\\unhappy.png"));
		unhappyIcon.addMouseListener(new IconListener(unhappyIcon.getIcon().toString()));
		emojiPlace.add(unhappyIcon);
		
		JLabel appleIcon= new JLabel (new ImageIcon(".\\file\\emoji\\apple.png"));
		appleIcon.addMouseListener(new IconListener(appleIcon.getIcon().toString()));
		emojiPlace.add(appleIcon);
		
		JLabel orangeIcon = new JLabel(new ImageIcon(".\\file\\emoji\\orange.png"));
		orangeIcon.addMouseListener(new IconListener(orangeIcon.getIcon().toString()));
		emojiPlace.add(orangeIcon);
		
		JLabel cherryIcon= new JLabel (new ImageIcon(".\\file\\emoji\\cherry.png"));
		cherryIcon.addMouseListener(new IconListener(cherryIcon.getIcon().toString()));
		emojiPlace.add(cherryIcon);
		
		JLabel cakeIcon= new JLabel (new ImageIcon(".\\file\\emoji\\cake.png"));
		cakeIcon.addMouseListener(new IconListener(cakeIcon.getIcon().toString()));
		emojiPlace.add(cakeIcon);
		
		JLabel vietnamIcon = new JLabel(new ImageIcon(".\\file\\emoji\\vietnam.png"));
		vietnamIcon.addMouseListener(new IconListener(vietnamIcon.getIcon().toString()));
		emojiPlace.add(vietnamIcon);
		
		JLabel usIcon= new JLabel (new ImageIcon(".\\file\\emoji\\us.png"));
		usIcon.addMouseListener(new IconListener(usIcon.getIcon().toString()));
		emojiPlace.add(usIcon);
		
		JLabel ukIcon= new JLabel (new ImageIcon(".\\file\\emoji\\uk.png"));
		ukIcon.addMouseListener(new IconListener(ukIcon.getIcon().toString()));
		emojiPlace.add(ukIcon);
		
		JLabel canadaIcon = new JLabel(new ImageIcon(".\\file\\emoji\\canada.png"));
		canadaIcon.addMouseListener(new IconListener(canadaIcon.getIcon().toString()));
		emojiPlace.add(canadaIcon);
		
		JLabel italyIcon= new JLabel (new ImageIcon(".\\file\\emoji\\italy.png"));
		italyIcon.addMouseListener(new IconListener(italyIcon.getIcon().toString()));
		emojiPlace.add(italyIcon);
		
		JLabel spainIcon= new JLabel (new ImageIcon(".\\file\\emoji\\spain.png"));
		spainIcon.addMouseListener(new IconListener(spainIcon.getIcon().toString()));
		emojiPlace.add(spainIcon);
		
		JLabel egyptIcon = new JLabel(new ImageIcon(".\\file\\emoji\\egypt.png"));
		egyptIcon.addMouseListener(new IconListener(egyptIcon.getIcon().toString()));
		emojiPlace.add(egyptIcon);
		
		JLabel swedenIcon= new JLabel (new ImageIcon(".\\file\\emoji\\sweden.png"));
		swedenIcon.addMouseListener(new IconListener(swedenIcon.getIcon().toString()));
		emojiPlace.add(swedenIcon);
		
		JLabel australiaIcon= new JLabel (new ImageIcon(".\\file\\emoji\\australia.png"));
		australiaIcon.addMouseListener(new IconListener(australiaIcon.getIcon().toString()));
		emojiPlace.add(cherryIcon);
		
		chooseRoom.add(rooms);
		get_rooms.add(chooseRoom);
		
		JPanel getRoom=new JPanel();
		getRoom.add(new JLabel("Room:"));
		getRoom.add(reciever);
		
		get_rooms.add(getRoom);
		
		body.add(get_rooms);
		
		JPanel chatDisplay=new JPanel();
		chatPanel=new JScrollPane();
		chatPanel.setPreferredSize(new Dimension(400, 300));
		chatPanel.setViewportView(chatWin);
		chatPanel.setForeground(Color.WHITE);
		
		chatDisplay.add(chatPanel);
		body.add(chatDisplay);
		
		JPanel chatPlace=new JPanel();
		chatPlace.setLayout(new BoxLayout(chatPlace, BoxLayout.Y_AXIS));
		
		text=new JTextField();
		JPanel stuffs=new JPanel();
		text.setPreferredSize(new Dimension(350, 35));
		text.setEnabled(false);
		stuffs.add(text);
		
		send_message=new JButton(new ImageIcon(".\\file\\component\\send.png"));
		send_message.setEnabled(false);
		
		send_message.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LocalDateTime myDateObj = LocalDateTime.now();
				DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
				
				String time = myDateObj.format(myFormatObj);
				try {
					out.writeUTF("Text");
					out.writeUTF(time);
					out.writeUTF(reciever.getText());
					out.writeUTF(text.getText());
					out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// In ra tin nhắn lên màn hình chat với người nhận
				if (reciever.getText().equals("Global")) {
					TextMessagetoGlobal(username, time, text.getText(), true);
				}
				
				else {
					TextMessagetoPrivate(reciever.getText(), username, time, text.getText(), true);
				}
				
				text.setText("");
			}
		});
		
		choose_file=new JButton(new ImageIcon(".\\file\\component\\attach-file.png"));
		choose_file.setEnabled(false);
		
		choose_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				LocalDateTime myDateObj = LocalDateTime.now();
				DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
				
				String time = myDateObj.format(myFormatObj);
				int rVal = fileChooser.showOpenDialog(new JFrame());
				if (rVal == JFileChooser.APPROVE_OPTION) {
					String filename=fileChooser.getSelectedFile().getName();
					byte[] selectedFile = new byte[(int) fileChooser.getSelectedFile().length()];
					BufferedInputStream bis;
					try {
						bis = new BufferedInputStream(new FileInputStream(fileChooser.getSelectedFile()));

						bis.read(selectedFile, 0, selectedFile.length);
						String path=".\\file\\sent_file\\"+filename;
						Path file = Paths.get(path);
						Files.write(file, selectedFile);
						out.writeUTF("File");
						out.writeUTF(time);
						out.writeUTF(reciever.getText());
						out.writeUTF(filename);
						out.writeUTF(String.valueOf(selectedFile.length));
						
						int size=selectedFile.length;
						int bufferSize=2048;
						int offset=0;
						
						// Lần lượt gửi cho server từng buffer cho đến khi hết file
						while (size > 0) {
							out.write(selectedFile, offset, Math.min(size, bufferSize));
							offset += Math.min(size, bufferSize);
							size -= bufferSize;
						} 
						
						out.flush();
						FileMessagetoPrivate(reciever.getText(), username, time, fileChooser.getSelectedFile().getName(), selectedFile);
						bis.close();
						
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
			
		});
		
		stuffs.add(send_message);
		stuffs.add(choose_file);
		
		chatPlace.add(emojiPlace);
		chatPlace.add(stuffs);
		body.add(chatPlace);
		
		frame.add(body, BorderLayout.CENTER);
		
		chatWindows.put(" ", new JTextPane());
		chatWin=chatWindows.get(" ");
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
				try {
					out.writeUTF("Log out");
					out.flush();
					
					try {
						recieveItem.join();
					}
					catch(InterruptedException ex) {
						ex.printStackTrace();
					}
					
					if (in!=null) {
						in.close();
					}
					
					if (out!=null) {
						out.close();
					}
				}
				
				catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	
	class Reciever implements Runnable{
		private DataInputStream in;
		boolean isLoaded=false;
		
		public Reciever(DataInputStream in) {
			this.in=in;
		}
		
		public void run(){
			try {
				while(true) {
					String procedure=in.readUTF();
					//You can log out
					if (procedure.equals("You can leave")) {
						break;
					}
					
					//recieve text message
					else if(procedure.equals("Text")) {
						String mode=in.readUTF();
						String time=in.readUTF();
						String sender=in.readUTF();
						String text=in.readUTF();
						if (mode.equals("Global")){
							TextMessagetoGlobal(sender, time, text, false);
						}
						
						else if(mode.equals("Private")) {
							TextMessagetoPrivate(username, sender, time, text, false);
						}
					}
					

					else if(procedure.equals("Emoji")) {
						String mode=in.readUTF();
						String time=in.readUTF();
						String sender=in.readUTF();
						String emoji=in.readUTF();
						
						if (mode.equals("Global")) {
							EmojiMessagetoGlobal(sender, time, emoji, false);
						}
						
						else if (mode.equals("Private")) {
							EmojiMessagetoPrivate(username, sender, time, emoji);
						}
					}
					
					//recieves file
					else if(procedure.equals("File")) {
						String mode=in.readUTF();
						String time=in.readUTF();
						String sender=in.readUTF();
						String filename=in.readUTF();
						int size=Integer.parseInt(in.readUTF());
						
						byte[] buffer=new byte[2048];
						ByteArrayOutputStream file = new ByteArrayOutputStream();
						
						while (size>0) {
							in.read(buffer, 0, Math.min(size, 2048));
							file.write(buffer, 0, Math.min(size, 2048));
							size-=2048;
						}
						if (mode.equals("Global")) {
							FileMessagetoGlobal(sender, time, filename, file.toByteArray(), false);
						}
						else if (mode.equals("Private")) {
							FileMessagetoPrivate(username, sender, time, filename, file.toByteArray());
						}
					}
					
					else if (procedure.equals("User List")) {
						String collected=in.readUTF();
						String []list=collected.split(",");
						rooms.removeAllItems();
						
						for (String user: list) {
							if (user.equals(username) == false) {
								// Cập nhật danh sách các người dùng trực tuyến vào ComboBox onlineUsers (trừ bản thân)
								rooms.addItem(user);
								if (chatWindows.get(user) == null) {
									JTextPane temp = new JTextPane();
									temp.setFont(new Font("Arial", Font.PLAIN, 14));
									temp.setEditable(false);
									chatWindows.put(user, temp);
								}
							}
						}
						
						rooms.validate();
						if (isLoaded==false) {
							for (String user: list) {
								if (user.equals("Global")) {
									loadMessageToGlobal();
								}
								
								else {
									loadMessageToPrivate(user);
								}
							}
							
							isLoaded=true;
						}
					}
				}
			}catch (IOException e){
				e.printStackTrace();
			}finally {
				try {
					if (in!=null) {
						in.close();
					}
				}catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	class HyberLinkListener extends AbstractAction{
		
		String filename;
		byte[] file;
		
		public HyberLinkListener(String filename, byte[] file) {
			this.filename=filename;
			this.file=Arrays.copyOf(file, file.length);
		}
		public void actionPerformed(ActionEvent e) {
			execute();
		}
		
		public void execute() {
			//select the chosen file
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(filename));
			
			//click on the file link to save them into a certain directory you like
			int rVal = fileChooser.showSaveDialog(new JFrame());
			if (rVal == JFileChooser.APPROVE_OPTION) {
				
				File saveFile = fileChooser.getSelectedFile();
				BufferedOutputStream bos = null;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(saveFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				//after saving file, you can choose to open it
				int nextAction = JOptionPane.showConfirmDialog(null, "Saved file to " + saveFile.getAbsolutePath() + "\nDo you want to open this file?", "Successful", JOptionPane.YES_NO_OPTION);
				if (nextAction == JOptionPane.YES_OPTION) {
					try {
						Desktop.getDesktop().open(saveFile);
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
				
				//write file to location
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
	
	 public static Connection getConnection(String dbURL, String userName, 
	            String password) {
	        Connection conn = null;
	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	            conn = DriverManager.getConnection(dbURL, userName, password);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return conn;
	    }
//	
	public ChatRoom(String username, DataInputStream in, DataOutputStream out) {
		setUsername(username);
		setDataInputStream(in);
		setDataOutputStream(out);
		recieveItem=new Thread(new Reciever(in));
		recieveItem.start();
		design();
	}
	
	class IconListener extends MouseAdapter {
		String emoji;
		
		public IconListener(String emoji) {
			this.emoji = emoji;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (text.isEnabled() == true) {
				LocalDateTime myDateObj = LocalDateTime.now();
				DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
				
				String time = myDateObj.format(myFormatObj);
				try {
					out.writeUTF("Emoji");
					out.writeUTF(time);
					out.writeUTF(reciever.getText());
					out.writeUTF(this.emoji);
					out.flush();
					
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				if (reciever.getText().equals("Global")) {
					EmojiMessagetoGlobal(username, time, emoji, true);
				}
				
				else {
					EmojiMessagetoPrivate(reciever.getText(), username, time, emoji);
				}
				
				
			}
		}
	}
}