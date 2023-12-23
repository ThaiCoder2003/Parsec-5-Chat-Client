package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class server_frame{
	private JFrame frame;
	private JPanel title;
	private JPanel button;
	server server;
	private JButton start;
	private JPanel footer;
	private JLabel alert; 
	
	Thread t;
	
	public server_frame() {
		frame=new JFrame();
		frame.setTitle("Parsec 5 Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container pane=frame.getContentPane();
		pane.setPreferredSize(new Dimension(400, 190));
		JPanel head1=new JPanel();
		head1.setBackground(Color.orange);
		
		JLabel text1=new JLabel();
		text1.setText("PARSEC 5 CHAT");
		text1.setFont(new Font("Arial", Font.BOLD, 36));
		head1.add(text1);
		JPanel head2=new JPanel();
		
		JLabel text2=new JLabel();
		text2.setText("Server");
		text2.setFont(new Font("Arial", Font.PLAIN, 20));
		head2.add(text2);
		title=new JPanel();
		title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
		title.add(head1);
		title.add(head2);
		
		button=new JPanel();
		
		JPanel con=new JPanel();
		
		start=new JButton();
		start.setPreferredSize(new Dimension(120, 60));
		start.setText("Start Server");
		start.setEnabled(true);
		
		con.add(start);
		
		button.add(con, BorderLayout.CENTER);
		
		footer=new JPanel();
		alert=new JLabel();
		
		footer.add(alert, BorderLayout.CENTER);
		
		pane.add(title, BorderLayout.NORTH);
		pane.add(button, BorderLayout.CENTER);
		pane.add(footer, BorderLayout.SOUTH);
		
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				t=new Thread() {
					public void run() {
						try {
							server=new server();
						} catch (IOException e) {
							alert.setForeground(Color.red);
							alert.setText("Failed to start server!");
						}
					}
				};
				
				t.start();
				start.setEnabled(false);
				alert.setForeground(Color.GREEN);
				alert.setText("Server started successfully!");
			}
			
		});
		
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
	}
	
	public static void main(String[] args) {
		server_frame ui=new server_frame();
	}
}
