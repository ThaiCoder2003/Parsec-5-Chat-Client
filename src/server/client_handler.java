package server;

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;

public class client_handler{
	private Object lock;
	private String username;
	private Socket socket;
	private String password;
	private boolean isOnline;
	
	public client_handler(String username, String password, boolean isOnline, Socket socket, Object lock) {
		this.username=username;
		this.password=password;
		this.isOnline=isOnline;
		this.socket=socket;
		this.lock=lock;
	}
}
