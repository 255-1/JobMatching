package save;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class ConnectMySQL {
    private static String jobDBAddress;
    private static String proxyDBAddress;
    private static String userName;
    private static String passwd;
    private static ResourceBundle rb = ResourceBundle.getBundle("db-config");

    static {
        jobDBAddress = rb.getString("mysql_job.address");
        proxyDBAddress = rb.getString("mysql_proxy.address");
        userName = rb.getString("mysql.userName");
        passwd = rb.getString("mysql.passwd");
    }

    /**
     * @Author: PowerZZJ
     * @return: job数据库的连接
     * @Description:返回job数据库的连接
     */
    public synchronized static Connection getConnectionJob() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jobDBAddress, userName, passwd);
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("job数据库连接失败");
        return null;
    }

    /**
     * @Author: PowerZZJ
     * @return: job数据库的连接
     * @Description:返回job数据库的连接
     */
    public synchronized static Connection getConnectionProxy() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(proxyDBAddress, userName, passwd);
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("job数据库连接失败");
        return null;
    }

    /**
     * @Author: PowerZZJ
     * @param: conn 数据库连接
     * @Description: 关闭数据库的连接
     */
    public static void close(final Connection conn) {
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
