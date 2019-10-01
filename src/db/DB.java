package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {
	private static Connection conn = null;

	public static Connection getConnection() throws ClassNotFoundException {
		if (conn == null) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				// capta as propriedades do BD
				Properties props = loadProperties();
				// capta a url do BD
				String url = props.getProperty("dburl");
				//String url = "jdbc:sqlserver://localhost:1433;databaseName=MLIBANIODADOS";
				// conecta no BD
				conn = DriverManager.getConnection(url, props);
			} 
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}
	
	public static void closeConnection() {
		if(conn != null) {
			try {
				conn.close();
			}
			catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	private static Properties loadProperties() {
		// leitura e gravação do arquivo de configuração criado (propriedades de
		// conexão)
		try (FileInputStream fs = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(fs);
			return props;
		} catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	public static void closeStatement(Statement st) {
		if(st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				// Tratando com RuntimeExcetption (pra nao precisar ficar tratando com try-catch toda vez)
				throw new DbException(e.getMessage());
			}
		}
	}
	
	public static void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// Tratando com RuntimeExcetption (pra nao precisar ficar tratando com try-catch toda vez)
				throw new DbException(e.getMessage());
			}
		}
	}
}
