package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class login{
	private JFrame frame;
	private JPanel title;
	private JPanel login;
	private JPanel functions;
	private JButton signin;
	private JButton signup;
	
	JTextField username;
	JPasswordField password;
	
	private Socket s;
	
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
		text2.setText("Login");
		text2.setFont(new Font("Arial", Font.PLAIN, 20));
		head2.add(text2);
		title=new JPanel();
		title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
		title.add(head1);
		title.add(Box.createVerticalGlue());
		title.add(head2);
		
		pane.add(title, BorderLayout.PAGE_START);
		
		JPanel username_area=new JPanel();
		JLabel user=new JLabel();
		user.setText("Username");
		
		username=new JTextField();
		username.setPreferredSize(new Dimension(150, 20));
		
		username_area.add(user);
		username_area.add(username);
		
		JPanel password_area=new JPanel();
		JLabel pass=new JLabel();
		pass.setText("Password");
		
		password=new JPasswordField();
		password.setPreferredSize(new Dimension(150, 20));
		
		password_area.add(pass);
		password_area.add(password);
		
		login=new JPanel();  
		login.setLayout(flow);
		login.add(username_area);
		login.add(password_area);
		
		pane.add(login, BorderLayout.CENTER);
		
		functions=new JPanel();
		
		JPanel con=new JPanel();
		
		signin=new JButton();
		signin.setPreferredSize(new Dimension(80, 35));
		signin.setText("Login");
		
		signup=new JButton();
		signup.setPreferredSize(new Dimension(90, 35));
		signup.setText("Register");
		
		con.add(signin);
		con.add(signup);
		
		functions.add(con, BorderLayout.CENTER);
		pane.add(functions, BorderLayout.PAGE_END);
		
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public login() {
		design();
		
		signin.addActionListener(new connect_to_server());
		signup.addActionListener(new connect_to_server());
	}
	
	class connect_to_server implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			System.out.println("Hello World");
		}
	}
	
	public static void main(String[] args) {
		login log=new login();
	}
}