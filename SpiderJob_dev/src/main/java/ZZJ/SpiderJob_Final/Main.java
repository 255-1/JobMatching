package ZZJ.SpiderJob_Final;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import Base.ConnectMySQL;
import Base.JobBean;
import Spider.SpiderJob_51job;
import Spider.SpiderURL_51job;
import StaticUtils.DBUtils;
import StaticUtils.JobBeanUtils;

public class Main {
	public static void main(String[] args) {
		String URL = null;
		List<JobBean> jobBeanList = new ArrayList<JobBean>();
		Scanner sc = new Scanner(System.in);
		Connection conn = new ConnectMySQL().GetConnection();
		
		//时间获取
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);
		
		System.out.println("输入51job：大数据+上海的搜索界面");
		URL = sc.nextLine();
		//保存职业名带“大数据”关键字的职位URL
		SpiderURL_51job su = new SpiderURL_51job(URL, "大数据");
		su.SpiderURL();
		List<String> urlList = su.getUrlList();

		//根据URL进去爬取，职位信息不带关键字的信息
		SpiderJob_51job sj = new SpiderJob_51job(urlList);
		sj.SpiderJob();
		jobBeanList = sj.getJobBeanList();

		//没请洗过的本地备份
		JobBeanUtils.SaveJobBeanList(jobBeanList,"BigData_"+year+month+day+"_Ori.txt");

		//将没清洗的数据存入数据库
		DBUtils.InsertJobBeanList(conn, "jobinfo_ori", jobBeanList);

		Clean clean = new Clean(jobBeanList);
		jobBeanList = clean.StartClean();

		//请洗过的本地备份
		JobBeanUtils.SaveJobBeanList(jobBeanList,"BigData_"+year+month+day+"_Clean.txt");

		DBUtils.TruncateTable(conn, "jobinfo_clean");
		DBUtils.InsertJobBeanList(conn, "jobinfo_clean", jobBeanList);
	}
}
