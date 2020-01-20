package proxy.save;

import proxy.bean.IpBean;
import save.ConnectMySQL;
import save.DBUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class IpBeanDBUtils {
    private static Connection connProxy;

    static {
        connProxy = ConnectMySQL.getConnectionProxy();
    }

    /**
     * @Author: PowerZZJ
     * @param: tableName 表名
     * ipBeanList 代理IP列表
     * @Description: 代理IP列表插入到表中
     */
    public static void insertIpBeanList(String tableName, List<IpBean> ipBeanList) {
        if (null == tableName || tableName.length() == 0) {return;}
        if (ipBeanList == null) {return;}

        String insertCommand = "insert into " + tableName +
                "(IPAddress,IPPort,IPType,IPSpeed) "
                + "values('%s','%s','%s','%s');";
        int success = 0;
        for (IpBean ip : ipBeanList) {
            String command = String.format(insertCommand,
                    ip.getIpAddress(), ip.getIpPort(), ip.getIpType(), ip.getIpSpeed());
            if (DBUtils.executeUpdate(connProxy, command)) {
                success += 1;
            }
        }
        System.out.println("成功插入" + success + "条数据到" + tableName + "中");
    }

    /**
     * @Author: PowerZZJ
     * @param:tableName 表名
     * @Description: 从数据读取数据转为代理IP列表
     */
    public static List<IpBean> selectIpBeanList(String tableName) {
        if (null == tableName || tableName.length() == 0) {return new ArrayList<>();}
        String command = "select IPAddress,IPPort,IPType,IPSpeed from " + tableName;

        ResultSet rs = DBUtils.executeSelect(connProxy, command);
        if (null == rs) {
            System.out.println("成功读取数据库" + tableName + "0条数据");
            return new ArrayList<>();
        }
        return getIpBeanListFromResultSet(rs);
    }
    //--------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @param: rs 数据库读取结果
     * @return: 代理IP列表
     * @Description: 从ResultSet结果中读取代理IP列表
     */
    public static List<IpBean> getIpBeanListFromResultSet(ResultSet rs) {
        List<IpBean> ipBeanList = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) {break;}
            } catch (SQLException e) {
                e.printStackTrace();
            }
            IpBean ipBean = new IpBean();
            try {
                ipBean.setIpAddress(rs.getString(1));
                ipBean.setIpPort(rs.getString(2));
                ipBean.setIpType(rs.getString(3));
                ipBean.setIpSpeed(rs.getString(4));
                ipBeanList.add(ipBean);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("读取" + ipBeanList.size() + "条代理ip数据");
        return ipBeanList;
    }
}
