package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class ConnectMySQL {
	//数据库信息
	private  String DBaddress = "jdbc:mysql://localhost/51job?serverTimezone=UTC";
	private  String userName = "root";
	private  String password = "Woshishabi2813";
	
	private Connection conn;
	
	//加载驱动，连接数据库
	public ConnectMySQL() {
		LoadDriver();
		//连接数据库
		try {
			conn = DriverManager.getConnection(DBaddress, userName, password);
			System.out.println(userName+"连接MySQL成功");
		} catch (SQLException e) {
			System.out.println("数据库连接失败");
		}
	}
	
	//加载驱动
	private void LoadDriver() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("加载驱动成功");
		} catch (Exception e) {
			System.out.println("驱动加载失败");
		}
	}
	
	//获取连接
	public Connection getConn() {
		return conn;
	}
}
