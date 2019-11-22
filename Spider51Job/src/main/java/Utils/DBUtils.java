package Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class DBUtils {
	
	/**将JobBean容器存入数据库（有筛选）
	 * @param conn 数据库的连接
	 * @param tableName 表名
	 * @param jobBeanList jobBean列表
	 */
	public static void insert(Connection conn, String tableName,List<JobBean> jobBeanList) {
		int n=0;
		System.out.println("正在插入数据");
		PreparedStatement ps;
		for(JobBean j: jobBeanList) {
			//命令生成
			String command = String.format(
					"insert into "+tableName+
					" values('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
					j.getJobName(),
					j.getCompany(),
					j.getAddress(),
					j.getSalary(),
					j.getDate(),
					j.getExp(),
					j.getEdu(),
					j.getOfferNumber(),
					j.getJobInfo(),
					j.getCompanyType(),
					j.getStaffNumber(),
					j.getCompanyOrientation());
			
			try {
				ps = conn.prepareStatement(command);
				ps.executeUpdate();
				n++;
			} catch (Exception e) {
				//重复数据，不用报错直接跳过
				if(e.getMessage().contains("PRIMARY"))
					continue;
				System.out.println(j);
				System.out.println("存入数据库失败:"+e.getMessage());
			}
		}
		System.out.println("插入"+n+"条数据到数据库完成");

	}
	
	/**将JobBeanList，取出
	 * @param conn 数据库的连接
	 * @param tableName 表名
	 * @return jobBean容器
	 */
	public static List<JobBean> select(Connection conn, String tableName){
		PreparedStatement ps;
		ResultSet rs;
		List<JobBean> jobBeanList  = new ArrayList<JobBean>();

		String command = "select * from "+tableName;
		try {
			ps = conn.prepareStatement(command);
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
							rs.getString(12));
							
				jobBeanList.add(jobBean);
			}
			return jobBeanList;
		} catch (Exception e) {
			System.out.println("数据库查询失败");
		}
		return null;
	}
}
