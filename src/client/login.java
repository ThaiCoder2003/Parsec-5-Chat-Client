package client;

import javax.swing.*;
import javax.swing.event.*;
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
	
	private JTextField username;
	private JPasswordField password;
	
	private String host="localhost";
	private int serverPort=2023;
	
	private Socket s;
	private DataInputStream read;
	private DataOutputStream write;
	
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
		signin.setEnabled(false);
		
		signup=new JButton();
		signup.setPreferredSize(new Dimension(90, 35));
		signup.setText("Register");
		signup.setEnabled(false);
		
		con.add(signin);
		con.add(signup);
		
		functions.add(con, BorderLayout.CENTER);
		pane.add(functions, BorderLayout.PAGE_END);
		
		signin.addActionListener(new connect_to_server());
		signup.addActionListener(new connect_to_server());
		username.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (username.getText().equals("") || String.copyValueOf(password.getPassword()).equals("")) {
					signin.setEnabled(false);
					signup.setEnabled(false);
				}
				else {
					signin.setEnabled(true);
					signup.setEnabled(true);
				}
			  }
			  public void removeUpdate(DocumentEvent e) {
				  if (username.getText().equals("") || String.copyValueOf(password.getPassword()).equals("")) {
						signin.setEnabled(false);
						signup.setEnabled(false);
					}
					else {
						signin.setEnabled(true);
						signup.setEnabled(true);
					}
			  }
			  public void insertUpdate(DocumentEvent e) {
				  if (username.getText().equals("") || String.copyValueOf(password.getPassword()).equals("")) {
						signin.setEnabled(false);
						signup.setEnabled(false);
					}
					else {
						signin.setEnabled(true);
						signup.setEnabled(true);
					}
			}
		});		
		
		password.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (username.getText().equals("") || String.copyValueOf(password.getPassword()).equals("")) {
					signin.setEnabled(false);
					signup.setEnabled(false);
				}
				else {
					signin.setEnabled(true);
					signup.setEnabled(true);
				}
			  }
			  public void removeUpdate(DocumentEvent e) {
				  if (username.getText().equals("") || String.copyValueOf(password.getPassword()).equals("")) {
						signin.setEnabled(false);
						signup.setEnabled(false);
					}
					else {
						signin.setEnabled(true);
						signup.setEnabled(true);
					}
			  }
			  public void insertUpdate(DocumentEvent e) {
				  if (username.getText().equals("") || String.copyValueOf(password.getPassword()).equals("")) {
						signin.setEnabled(false);
						signup.setEnabled(false);
					}
					else {
						signin.setEnabled(true);
						signup.setEnabled(true);
					}
			}
		});		
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public login() {
		design();	
	}
	
	public void Connect() {
		try {
			if (s!=null) {
				s.close();
			}
			s=new Socket(host, serverPort);
			read=new DataInputStream(s.getInputStream());
			write=new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();;
		}
	}
	
	public String Login(String username, String password) {
		try {
			Connect();
			write.writeUTF("Login");
			write.writeUTF(username);
			write.writeUTF(password);
			write.flush();
			
			String res=read.readUTF();
			return res;
			
		}catch (IOException e) {
			return "Cannot login at the moment!";
		}
	}
	
	public String Register(String username, String password) {
		try {
			Connect();
			write.writeUTF("Register");
			write.writeUTF(username);
			write.writeUTF(password);
			write.flush();
			
			String res=read.readUTF();
			return res;
			
		}catch (IOException e) {
			return "Cannot register at the moment!";
		}
	}
	
	class connect_to_server implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String action=e.getActionCommand();
			if (action=="Login") {
				String getUsername=username.getText();
				String response=Login(getUsername, String.copyValueOf(password.getPassword()));
				
				if(response.equals("Logged in Successfully!")) {
					String welcome="Welcome back, "+getUsername+"!";
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showConfirmDialog(new JFrame(), welcome, "Welcome back!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
							ChatRoom room=new ChatRoom(getUsername, read, write);
						}
					});
					
					frame.dispose();
				}
				
				else {
					JOptionPane.showConfirmDialog(new JFrame(), response, "Login failed!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					
					password.setText("");
				}
			}
			
			else if (action=="Register") {
				JPasswordField confirm=new JPasswordField();
				confirm.setPreferredSize(new Dimension(150, 20));
				int confirm_panel=JOptionPane.showConfirmDialog(new JFrame(), confirm, "Confirm your password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (confirm_panel==JOptionPane.OK_OPTION) {
					if (String.copyValueOf(confirm.getPassword()).equals(String.copyValueOf(password.getPassword()))) {
						String response=Register(username.getText(), String.copyValueOf(password.getPassword()));
						
						if (response.equals("Registered Successfully!")) {
							String getUsername=username.getText();
							String welcome="Welcome to Parsec 5 Chat, "+getUsername+"!\nHope you have the best time here! :)";
							
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									JOptionPane.showConfirmDialog(new JFrame(), welcome, "Welcome new user!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
									ChatRoom room=new ChatRoom(getUsername, read, write);
								}
							});
							
							frame.dispose();	
						}
						
						else {
							JOptionPane.showConfirmDialog(new JFrame(), response, "Registration failed!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
							
							password.setText("");
						}
					}
					
					else {
						JOptionPane.showConfirmDialog(new JFrame(), "Invalid confirm password!", "Registration failed!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		login log=new login();
		
	}
}