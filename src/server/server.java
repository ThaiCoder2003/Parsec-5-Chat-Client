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
	private ServerSocket s;
	private Socket socket;
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
			String sql="insert into users ('username', 'password')"
					+ "values(?, ?)";
			Connection conn=getConnection(DB_URL, USER_NAME, PASSWORD);
			PreparedStatement preparedStmt=conn.prepareStatement(sql);
			preparedStmt.setString(1, username);
			preparedStmt.setString(2, password);
			
			int row=preparedStmt.executeUpdate();
			conn.close();
			
			return row;
		}
		catch (SQLException e) {
			return 0;
		}
	}
	
	public void addOnline(String username) {	
		online.add(username);
	}
	
	public void removeOnline(String username) {
		online.remove(username);
	}
	
	public void getOnlineList() throws IOException {
		String list="";
		for (String user:online) {
			list+=user;
			list+=", ";
		}
		
		list=list.substring(0, list.length()-2);
		
		for (client_handler client:clients) {
			if (online.contains(client.getUsername())) {
				client.getOut().write("Online Users: ");
				client.getOut().write(list);
				client.getOut().flush();
			}
		}
	}
	
	public server() throws IOException{
		try {
			lock=new Object();
			loadAccount();
			s=new ServerSocket(2023);
			while(true) {
				socket=s.accept();
				
				BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				String req=in.readLine();
				
				if(req.equals("Register")) {
					String username=in.readLine();
					String password=in.readLine();
					
					if (exists(username)==false) {
						int added=saveAccount(username, password);
						if (added!=0) {
							client_handler new_client=new client_handler(username, password, socket, lock);
							clients.add(new_client);
							out.write("Registered Successfully!");
							out.flush();
							Thread t=new Thread(new_client);
							t.start();
							addOnline(username);
						}
						else {
							out.write("Registration Failed!");
							out.flush();
						}
					}
					else {
						out.write("This username already exists!");
						out.flush();
					}
				}
				
				else if(req.equals("Login")){
					String username=in.readLine();
					String password=in.readLine();
					
					if (exists(username)==true){
						for (client_handler client:clients) {
							if (username.equals(client.getUsername())){
								if(password.equals(client.getPassword())){
									client_handler new_client=client;
									
									new_client.setSocket(socket);
									out.write("Logged in Successfully!");
									out.flush();
									Thread t=new Thread(new_client);
									t.start();
									addOnline(username);
								} 
								else {
									out.write("Incorrect username or password!");
									out.flush();
								}
								
								break;
							}
						}
					}
					
					else {
						out.write("Incorrect username or password!");
						out.flush();
					}
				}
			}
		}catch (Exception ex){
			System.err.println(ex);
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