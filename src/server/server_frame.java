package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class server_frame extends JFrame{
	JFrame frame;
	JPanel title;
	JPanel button;
	server server;
	
	public server_frame() {
		frame=new JFrame();
		frame.setTitle("Parsec 5 Server");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(500, 300));
	}
	
	public static void main(String[] args) {
		server_frame ui=new server_frame();
	}
}
