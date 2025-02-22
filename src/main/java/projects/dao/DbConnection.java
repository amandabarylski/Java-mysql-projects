package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

public class DbConnection {

	private static String HOST = "localhost";
	private static String PASSWORD = "projects";
	private static int PORT = 3306;
	private static String SCHEMA = "projects";
	private static String USER = "projects";
	
	public static Connection getConnection() {
		//For this I copied and pasted from the code I copied down while following along with the videos.
		//As this assignment uses the same standard variable names it still works.
		//The only difference is my String in the video notes is url instead of uri.
		//I am not sure if I made a mistake while copying it down, though, as I am more used to working with urls.
		String uri = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false",
				HOST, PORT, SCHEMA, USER, PASSWORD);
		
		//This is fairly similar to what I have in my notes, though I wrote my messages differently
		//and passed a message into my new DbException instead of e.
		try {
			Connection conn = DriverManager.getConnection(uri);
			System.out.println("Successfully connected to " + SCHEMA);
			return conn;
		} catch (SQLException e) {
			System.out.println("Unable to connect to" + SCHEMA);
			throw new DbException("Unable to connect to" + SCHEMA);
		}
	}
	
}
