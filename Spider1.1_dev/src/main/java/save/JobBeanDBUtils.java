package save;

import bean.JobBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class JobBeanDBUtils {
    private static Connection connJob;

    static {
        connJob = ConnectMySQL.getConnectionJob();
    }

    /**
     * @Author: PowerZZJ
     * @param:tableName 表名
     * jobBeanList 职业列表
     * @Description: 职业列表插入数据库
     */
    public static void insertJobBeanList(String tableName, List<JobBean> jobBeanList) {
        if (null == tableName || "".equals(tableName)) return;
        if (null == jobBeanList) return;

        System.out.println("开始插入" + jobBeanList.size() + "条数据到" + tableName);
        String insertCommand = "insert into " + tableName +
                "(jobName,company,address," +
                "salary,date,exp," +
                "edu,offerNumber,jobInfo," +
                "companyType,staffNumber,companyOrientation," +
                "jobURL)" +
                " values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');";
        int success = 0;
        for (JobBean j : jobBeanList) {
            String command = String.format(insertCommand,
                    j.getJobName(), j.getCompany(), j.getAddress(), j.getSalary(),
                    j.getDate(), j.getExp(), j.getEdu(), j.getOfferNumber(),
                    j.getJobInfo(), j.getCompanyType(), j.getStaffNumber(), j.getCompanyOrientation(),
                    j.getJobURL());
            if (DBUtils.executeUpdate(connJob, command)) {
                success += 1;
            }

        }
        System.out.println("成功插入" + success + "条数据到" + tableName + "中");
    }

    /**
     * @Author: PowerZZJ
     * @param:tableName 表名
     * @Description: 从数据读取数据转为职业列表
     * where unifyName is null 用来提取增量的职位名，因为添加过名字的都是之前爬取的
     */
    public static List<JobBean> selectJobBeanList(String tableName) {
        if (null == tableName || "".equals(tableName)) return null;
        String command = "select jobName,company,address,salary,"
                + "date,exp,edu,offerNumber,"
                + "jobInfo,companyType,staffNumber,companyOrientation,"
                + "jobURL from " + tableName
                + " where unifyName is null;";
        ResultSet rs = DBUtils.executeSelect(connJob, command);
        if (null == rs) {
            System.out.println("成功读取数据库" + tableName + "0条数据");
            return new ArrayList<JobBean>();
        }
        return getJobBeanListFromResultSet(rs);
    }

    /**
     * @Author: PowerZZJ
     * @param: rs 数据库读取结果
     * @return: 职业列表
     * @Description: 从ResultSet结果中读取职业列表
     */
    public static List<JobBean> getJobBeanListFromResultSet(ResultSet rs) {
        List<JobBean> jobBeanList = new ArrayList<JobBean>();
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JobBean jobBean = new JobBean();
            try {
                jobBean.setJobName(rs.getString(1));
                jobBean.setCompany(rs.getString(2));
                jobBean.setAddress(rs.getString(3));
                jobBean.setSalary(rs.getString(4));
                jobBean.setDate(rs.getString(5));
                jobBean.setExp(rs.getString(6));
                jobBean.setEdu(rs.getString(7));
                jobBean.setOfferNumber(rs.getString(8));
                jobBean.setJobInfo(rs.getString(9));
                jobBean.setCompanyType(rs.getString(10));
                jobBean.setStaffNumber(rs.getString(11));
                jobBean.setCompanyOrientation(rs.getString(12));
                jobBean.setJobURL(rs.getString(13));
                jobBeanList.add(jobBean);
            } catch (SQLException e) {
                e.printStackTrace();
                continue;
            }
        }
        System.out.println("读取" + jobBeanList.size() + "条数据");
        return jobBeanList;
    }
}
