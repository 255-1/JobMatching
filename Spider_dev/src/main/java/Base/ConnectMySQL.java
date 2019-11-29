package Base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 加载mysql连接驱动
 * 用来返回和数据库的连接
 * @author PowerZZJ
 * @version Date 2019年11月26日 
 */
public class ConnectMySQL {
	//数据库信息,DBaddress中去除时区可能遇到问题
	private  String DBaddress = "jdbc:mysql://localhost/51job?serverTimezone=UTC";
	private  String userName = "root";
	private  String password = "Woshishabi2813";
	//连接数据库的变量
	private Connection conn;
	
	//加载驱动
	private void LoadDriver() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("加载驱动成功");
		} catch (Exception e) {
			System.out.println("驱动加载失败");
		}
	}
	
	/**
	 * 加载驱动，连接数据库
	 */
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
	
	/**返回和mysql的连接
	 * @author PowerZZJ
	 * @return
	 */
	public Connection GetConnection() {
		return this.conn;
	}
}
