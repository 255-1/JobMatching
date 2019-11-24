package ZZJ.RYWorkstation;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import Base.ConnectMySQL;
import Base.JobBean;
import Utils.DBUtils;
import Utils.JobBeanUtils;

public class Main {

	public static void main(String[] args) {
		String URL = null;
		List<JobBean> jobBeanList = new ArrayList<>();
		Scanner sc = new Scanner(System.in);
		
		//时间获取
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);


//		System.out.println("输入51job：大数据+上海的搜索界面");
//		URL = sc.nextLine();
//		
//		//保存职业名带“大数据”关键字的职位URL
//		SpiderURL spider = new SpiderURL(URL,"大数据");
//		spider.SpiderURL();
//		List<String> urlList = spider.GetUrlList();
//		
//		//根据URL进去爬取，职位信息带有“大数据”关键字的信息
//		SpiderJob sj = new SpiderJob(urlList,"大数据");
//		sj.SpiderJob();
//		jobBeanList = sj.GetJobBeanList();
//		
//		//作为没请洗过的本地备份
//		JobBeanUtils.saveJobBeanList(jobBeanList,"BigData_"+year+month+day+"_Ori.txt");
		
		jobBeanList = JobBeanUtils.LoadJobBeanList("BigData_"+year+month+day+"_Ori.txt");
		
//		//将没清洗后的数据存入数据库
//		ConnectMySQL cm = new ConnectMySQL();
//		Connection conn = cm.getConn();
//		//调用数据库工具类将JobBean容器存入数据库
//		DBUtils.insert(conn, "jobInfo_Ori", jobBeanList);
		
		
		//清洗
		Clean clean = new Clean(jobBeanList);
		clean.startClean();
		jobBeanList = clean.GetJobBeanList();
		
		//清洗后的本地备份
		JobBeanUtils.saveJobBeanList(jobBeanList,"BigData_"+year+month+day+"_Clean.txt");
		
		//将清洗后的数据存入数据库
		ConnectMySQL cm = new ConnectMySQL();
		Connection conn = cm.getConn();
		//调用数据库工具类将JobBean容器存入数据库
		DBUtils.insert(conn, "jobInfo", jobBeanList);
	}



}
