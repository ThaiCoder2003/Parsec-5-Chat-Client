package server;

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;

public class server{
	Object lock;
	
	private static String DB_URL = "jdbc:mysql://localhost:3306/chat_info";
    private static String USER_NAME = "root";
    private static String PASSWORD = "ThaiDangQuoc:)2901";
	private static ServerSocket s;
	private Socket socket;
	private static DataInputStream in;
	private static DataOutputStream out;
	static ArrayList<client_handler> clients=new ArrayList<>();
	static ArrayList<String> online=new ArrayList<>();
	
	public void loadAccount() {
		try {
			Connection conn=getConnection(DB_URL, USER_NAME, PASSWORD);
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery("select * from users");
			
			while (rs.next()) {
				String username=rs.getString("username");
				String password=rs.getString("password");
				
				clients.add(new client_handler(username, password, lock));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int saveAccount(String username, String password) {
		try {
			String sql="insert into users(username, password)"
					+ " values(?, ?)";
			Connection conn=getConnection(DB_URL, USER_NAME, PASSWORD);
			PreparedStatement preparedStmt=conn.prepareStatement(sql);
			preparedStmt.setString(1, username);
			preparedStmt.setString(2, password);
			
			int row=preparedStmt.executeUpdate();
			conn.close();
			
			return row;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void addOnline(String username) {	
		online.add(username);
	}
	
	public static void removeOnline(String username) {
		online.remove(username);
	}
	
	public static void getOnlineList() throws IOException {
		String list="";
		for (String user:online) {
			list+=user;
			list+=",";
		}
		
		list=list.substring(0, list.length()-1);
		
		for (client_handler client:clients) {
			if (online.contains(client.getUsername())) {
				client.getOut().writeUTF("Online Users: ");
				client.getOut().writeUTF(list);
				client.getOut().flush();
			}
		}
	}
	
	public server() throws IOException{
		try {
			lock=new Object();
			loadAccount();
			s=new ServerSocket(2023);
			while(!s.isClosed()) {
				socket=s.accept();
				
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
				
				String req=in.readUTF();
				
				if(req.equals("Register")) {
					String username=in.readUTF();
					String password=in.readUTF();
					
					if (exists(username)==false) {
						int added=saveAccount(username, password);
						System.out.println(added);
						if (added!=0) {
							client_handler new_client=new client_handler(username, password, socket, lock);
							clients.add(new_client);
							out.writeUTF("Registered Successfully!");
							out.flush();
							Thread t=new Thread(new_client);
							t.start();
							addOnline(username);
							getOnlineList();
						}
						else {
							out.writeUTF("Registration Failed!");
							out.flush();
						}
					}
					else {
						out.writeUTF("This username already exists!");
						out.flush();
					}
				}
				
				else if(req.equals("Login")){
					
					String username=in.readUTF();
					String password=in.readUTF();
					
					if (exists(username)==true){
						for (client_handler client:clients) {
							if (username.equals(client.getUsername())){
								if(password.equals(client.getPassword())){
									client_handler new_client=client;
									
									new_client.setSocket(socket);
									out.writeUTF("Logged in Successfully!");
									out.flush();
									Thread t=new Thread(new_client);
									t.start();
									addOnline(username);
									getOnlineList();
								} 
								else {
									out.writeUTF("Incorrect username or password!");
									out.flush();
								}
								
								break;
							}
						}
					}
					
					else {
						out.writeUTF("Incorrect username or password!");
						out.flush();
					}
				}
			}
			
		}catch (Exception ex){
			System.err.println(ex);
		}
	 }
	
	public static void closeServer() throws IOException{
		for (client_handler client:clients) {
			client.close();
		}
	
		clients.clear();
		if (in!=null) {
			in.close();
		}
		
		if (out!=null) {
			out.close();
		}
		
		if (!s.isClosed()) {
			s.close();
		}
	}
	
	 public boolean exists(String username) {
		 for (client_handler client:clients) {
			 if (username.equals(client.getUsername())){
				return true;
			 }
		 }
		 
		 return false;
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

}

class client_handler implements Runnable{
	private Object lock;
	private String username;
	private DataInputStream in;
	private DataOutputStream out;
	private Socket socket;
	private String password;
	
	public client_handler(String username, String password, Socket socket, Object lock) throws IOException {
		this.username=username;
		this.password=password;
		
		in=new DataInputStream(socket.getInputStream());
		out=new DataOutputStream(socket.getOutputStream());
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
	
	
	public DataInputStream getIn() {
		return in;
	}
	
	public DataOutputStream getOut() {
		return out;
	}
	
	public void setSocket(Socket s) throws IOException {
		socket=s;
		
		in=new DataInputStream(socket.getInputStream());
		out=new DataOutputStream(socket.getOutputStream());
	}
	
	public void broadcastTextMessage(String message, String time) throws IOException {
		for (client_handler client:server.clients) {
			try {
				if (!client.getUsername().equals(this.username) && server.online.contains(client.getUsername())) {
					synchronized(lock){
						client.getOut().writeUTF("Text");
						client.getOut().writeUTF(time);
						client.getOut().writeUTF(username);
						client.getOut().writeUTF(message);
					}
				}
			}catch(Exception e) {
				close();
			}			
		}
	}
	
	public void sendTextMessageToOne(String receiver, String message, String time) throws IOException {
		for (client_handler client:server.clients) {
			try {
				if (client.getUsername().equals(receiver)) {
					synchronized(lock){
						client.getOut().writeUTF("Text");
						client.getOut().writeUTF(time);
						client.getOut().writeUTF(username);
						client.getOut().writeUTF(message);
						break;
					}
				}
			}catch(Exception e) {
				close();
			}
			
		}
	}
	
	public void broadcastEmojiMessage(String emoji, String time) throws IOException {
  
		for (client_handler client:server.clients) {
			try {
				if (!client.getUsername().equals(this.username) && server.online.contains(client.getUsername())) {
					synchronized(lock){
						client.getOut().writeUTF("Emoji");
						client.getOut().writeUTF(time);
						client.getOut().writeUTF(username);
						client.getOut().writeUTF(emoji);
					}
				}
			}catch(Exception e) {
				close();
			}			
		}
	}
	
	public void sendEmojiMessageToOne(String receiver, String emoji, String time) throws IOException { 
		for (client_handler client:server.clients) {
			try {
				if (client.getUsername().equals(receiver)) {
					synchronized(lock){
						client.getOut().writeUTF("Emoji");
						client.getOut().writeUTF(time);
						client.getOut().writeUTF(username);
						client.getOut().writeUTF(emoji);
						break;
					}
				}
			}catch(Exception e) {
				close();
			}
			
		}
	}
	
	public void broadcastFile(String filename, int size, byte[] buffer, String time) throws IOException { 
		for (client_handler client:server.clients) {
			try {
				if (!client.getUsername().equals(this.username) && server.online.contains(client.getUsername())) {
					synchronized(lock){
						client.getOut().writeUTF("File");
						client.getOut().writeUTF(time);
						client.getOut().writeUTF(filename);
						client.getOut().writeUTF(String.valueOf(size));
						while (size>0) {
							in.read(buffer, 0, Math.min(size, 2048));
							client.getOut().write(buffer, 0, Math.min(size, 2048));
							size-=2048;
						}
						
						client.getOut().flush();
					}
				}
			}catch(Exception e) {
				close();
			}			
		}
	}
	
	public void sendFileToOne(String filename, int size, byte[] buffer, String receiver, String time) throws IOException { 
		for (client_handler client:server.clients) {
			try {
				if (client.getUsername().equals(receiver)) {
					synchronized(lock){
						client.getOut().writeUTF("File");
						client.getOut().writeUTF(time);
						client.getOut().writeUTF(filename);
						client.getOut().writeUTF(String.valueOf(size));
						while (size>0) {
							in.read(buffer, 0, Math.min(size, 2048));
							client.getOut().write(buffer, 0, Math.min(size, 2048));
							size-=2048;
						}
						
						client.getOut().flush();
						break;
					}
				}
			}catch(Exception e) {
				close();
			}
			
		}
	}
	
	public void close() throws IOException {
		
		server.removeOnline(username);
		
		if (in!=null) {
			in.close();
		}
		
		if (out!=null) {
			out.close();
		}
		
		if (socket!=null) {
			socket.close();
		}
	}
	
	public void run() {
		while (socket!=null) {
			try {
				String request=in.readUTF();
				
				if (request.equals("Log out")) {
					out.writeUTF("You can leave");
					out.flush();
					close();
					server.getOnlineList();
				}
				
				else if(request.equals("Text")) {
					String time=in.readUTF();
					String reciever=in.readUTF();
					String text=in.readUTF();
					
					if (reciever.equals("Global")) {
						broadcastTextMessage(text, time);
					}
					
					else {
						sendTextMessageToOne(reciever, text, time);
					}
				}
				
				else if(request.equals("Emoji")){
					String time=in.readUTF();
					String reciever=in.readUTF();
					String emoji=in.readUTF();
					
					if (reciever.equals("Global")) {
						broadcastEmojiMessage(emoji, time);
					}
					
					else {
						sendEmojiMessageToOne(reciever, emoji, time);
					}
				}
				
				else if(request.equals("File")){
					String time=in.readUTF();
					String receiver=in.readUTF();
					String filename=in.readUTF();
					int size=Integer.parseInt(in.readUTF());
					byte[] buffer=new byte[2048];
					
					if (receiver.equals("Global")) {
						broadcastFile(filename, size, buffer, time);
					}
					
					else {
						sendFileToOne(filename, size, buffer, receiver, time);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
	}
	
}
