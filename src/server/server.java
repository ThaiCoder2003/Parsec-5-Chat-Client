package server;

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;

public class server{
	private static String DB_URL = "jdbc:mysql://localhost:3306/chat_info";
    private static String USER_NAME = "root";
    private static String PASSWORD = "ThaiDangQuoc:)2901";
	private ServerSocket s;
	private Socket socket;
	static ArrayList<client_handler> clients=new ArrayList<>();
	static ArrayList<client_handler> online=new ArrayList<>();
	
	public void loadClient() {
		try {
			Connection conn=getConnection(DB_URL, USER_NAME, PASSWORD);
			Statement stmt=conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public server() throws IOException{
		try {
			while(true) {
				socket=s.accept();
				
				BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			}
		}catch (Exception ex){
			System.err.println(ex);
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

}