package db;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class Database {
	private DataSource dataSource;
	private static Database database;
	
	public Database(String jndiname) throws SQLException {
		try {
			dataSource = (DataSource) new InitialContext().lookup("java:comp/env" + jndiname);
		} catch (NamingException e) {
			throw new SQLException(jndiname + " is missing JNDI! : "+e.getMessage());
		}
	}
	
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	@SuppressWarnings("deprecation")
	public static Connection getMySQLConnection() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (DBStatic.mysql_pooling == false) {
			return (DriverManager.getConnection("jdbc:mysql://" + DBStatic.mysql_host + "/"
					+DBStatic.mysql_db, DBStatic.mysql_username, DBStatic.mysql_password));
		} else {
			if (database == null) {
				database = new Database("jdbc/db");
			}
			return (database.getConnection());
		}	
	}
	
	public static DBCollection getMyMongoDBConnection() throws UnknownHostException {
		Mongo m =  new Mongo(DBStatic.mymongo_host, DBStatic.mymongo_port);
		DB db = m.getDB(DBStatic.mymongo_db);
		DBCollection coll = db.getCollection("comments");
			
		return coll;
	}
}