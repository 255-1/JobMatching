package save;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class DBUtils {
    /**
     * @Author: PowerZZJ
     * @param: command 查询命令
     * @return: 查询结果
     * @Description: 返回查找结果
     */
    public static ResultSet executeSelect(Connection conn, String command) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(command);
            ResultSet rs = ps.executeQuery();
            //判断是否有第一个，没有返回空查询
            if (rs.next() == true) {
                //重置rs的指针位置
                rs.beforeFirst();
                return rs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Author: PowerZZJ
     * @param: command 插入命令,或者清空命令
     * @return: 是否插入，或者清空成功
     * @Description: 执行插入命令，清空命令，成功返回true
     */
    public static boolean executeUpdate(Connection conn, String command) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(command);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    /**
     * @Author: PowerZZJ
     * @param: conn 数据库连接
     * tableName 需要清空的表名
     * @Description:清空指定数据库的指定表
     */
    public static void truncateTable(Connection conn, String tableName) {
        if (null == conn) return;
        if (null == tableName || "".equals(tableName)) return;
        String truncateCommand = "truncate table " + tableName;
        if (executeUpdate(conn, truncateCommand)) {
            System.out.println("清空" + tableName + "成功");
        } else {
            System.out.println("清空" + tableName + "失败");
        }

    }

    /**
     * @Author: PowerZZJ
     * @Description:关闭所有数据库连接
     */
    public static void close(Connection... conns) {
        for (Connection conn : conns) {
            ConnectMySQL.close(conn);
        }
    }
}
