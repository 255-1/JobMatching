package StaticUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Base.JobBean;

/**
 * 提供JobBean与MySQL数据库间的读取存储操作
 * 此模块下的函数应在有<JobBean>列表和数据库数据的情况下自由使用
 * @author PowerZZJ
 * @version Date 2019年11月26日 
 */
public class DBUtils {
	

	/** 向数据库的指定表中插入JobBean列表中的符合数据库基本约束的信息
	 * @author PowerZZJ
	 * @param conn 数据库连接
	 * @param tableName 表名
	 * @param jobBeanList JobBean列表
	 */
	public static void InsertJobBeanList(Connection conn, String tableName, List<JobBean> jobBeanList) {
		int insert_Success_Number = 0;
		PreparedStatement ps = null;
		String insert_Command = "insert into "+tableName+
				"(jobName,company,address,salary,date,exp,edu,offerNumber,jobInfo,companyType,staffNumber,companyOrientation,jobURL)"+
				"values('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')";
		
		System.out.println("开始插入数据，现在一共"+jobBeanList.size()+"条数据");
		for(JobBean j:jobBeanList) {
			String command = String.format(insert_Command,
					j.getJobName(),j.getCompany(),j.getAddress(),
					j.getSalary(),j.getDate(),j.getExp(),
					j.getEdu(),j.getOfferNumber(),j.getJobInfo(),
					j.getCompanyType(),j.getStaffNumber(),j.getCompanyOrientation(),
					j.getJobURL());
			
			try {
				ps = conn.prepareStatement(command);
				ps.executeUpdate();
				insert_Success_Number++;
			}catch(Exception e) {
				//数据库设置了联合唯一约束，重复的无法插入
				if (e.getMessage().contains("key")) {
					continue;
				}
				System.out.println(j);
				System.out.println("存入数据库失败"+e.getMessage());
			}
		}
		System.out.println("成功插入"+insert_Success_Number+"条数据到"+tableName+"中");
	}
	
	/**从数据库指定的表中查询数据，转换成JobBean列表
	 * @author PowerZZJ
	 * @param conn 数据库连接
	 * @param tableName 表名
	 * @return JobBean列表
	 */
	public static List<JobBean> SelectJobBeanList(Connection conn, String tableName){
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<JobBean> jobBeanList = new ArrayList<>();
		String select_Command = "select jobName,company,address,"
				+ "salary,date,exp,"
				+ "edu,offerNumber,jobInfo,"
				+ "companyType,staffNumber,companyOrientation,"
				+ "jobURL from "+tableName;
		try {
			ps = conn.prepareStatement(select_Command);
			rs = ps.executeQuery();
			while(rs.next()) {
				JobBean jobBean = new JobBean(rs.getString(1),
						rs.getString(2),
						rs.getString(3),
						rs.getString(4),
						rs.getString(5),
						rs.getString(6),
						rs.getString(7),
						rs.getString(8),
						rs.getString(9),
						rs.getString(10),
						rs.getString(11),
						rs.getString(12),
						rs.getString(13));
				jobBeanList.add(jobBean);
			}
			return jobBeanList;
		}catch(Exception e) {
			System.out.println("表"+tableName+"查询失败");
			System.out.println(e.getMessage());
			return null;
		}
		
	}
	
	
	/**清空表中的数据
	 * @author PowerZZJ
	 * @param conn 数据库连接
	 * @param tableName 表名
	 */
	public static void TruncateTable(Connection conn,String tableName) {
		PreparedStatement ps = null;
		String truncate_Command = "truncate table "+tableName;
		try {
			ps = conn.prepareStatement(truncate_Command);
			ps.execute();
			System.out.println("清空"+tableName+"表成功");
		}catch(Exception e) {
			System.out.println("清空"+tableName+"表失败");
			System.out.println(e.getMessage());
		}
		
	}
}
