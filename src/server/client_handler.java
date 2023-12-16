package server;

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;

public class client_handler implements Runnable{
	private Object lock;
	private String username;
	private BufferedReader in;
	private BufferedWriter out;
	private Socket socket;
	private String password;
	
	public client_handler(String username, String password, Socket socket, Object lock) throws IOException {
		this.username=username;
		this.password=password;
		
		in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.socket=socket;
		this.lock=lock;
	}
	
	public client_handler(String username, String password, Object lock) {
		this.username=username;
		this.password=password;
		this.lock=lock;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	
	public BufferedReader getIn() {
		return in;
	}
	
	public BufferedWriter getOut() {
		return out;
	}
	
	public void closeSocket() throws IOException {
		if (socket!=null) {
			socket.close();
		}
	}
	
	public void setSocket(Socket s) throws IOException {
		socket=s;
		
		in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	public void run() {
		
	}
	
}
