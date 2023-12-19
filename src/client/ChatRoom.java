package client;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.StyledDocument;

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
	private JLabel recieved=new JLabel(" ");
	JComboBox<String>rooms=new JComboBox<String>();
	private JTextField text;
	private JScrollPane chatPanel;
	private JTextPane chatWin;
	
	private HashMap<String, JTextPane> chatWindows = new HashMap<String, JTextPane>();
	
	private DataInputStream in;
	private DataOutputStream out;
	
	Thread receiver;
	
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
	
	private void TextMessage(String username, String text, boolean yours) {
		StyledDocument doc;
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
		body.add(chatWin);
		
		frame.add(body, BorderLayout.CENTER);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
//	class Receiver implements Runnable{
//		
//	}
//	
	public ChatRoom(String username, DataInputStream in, DataOutputStream out) {
		design();
	}
	public void save_chat() {
		
	}
}