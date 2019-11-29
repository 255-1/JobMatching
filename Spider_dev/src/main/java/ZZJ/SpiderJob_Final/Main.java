package ZZJ.SpiderJob_Final;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import Base.ConnectMySQL;
import Base.JobBean;
import Spider.SpiderJob;
import Spider.SpiderURL;
import StaticUtils.DBUtils;
import StaticUtils.JobBeanUtils;

public class Main {
	public static void main(String[] args) {
		String URL = null;
		List<JobBean> jobBeanList = new ArrayList<>();
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
		SpiderURL spider = new SpiderURL(URL,"大数据");
		spider.Spider();
		List<String> urlList = spider.GetUrlList();
		
		//根据URL进去爬取，职位信息不带关键字的信息
		SpiderJob sj = new SpiderJob(urlList);
		sj.Spider();
		jobBeanList = sj.GetJobBeanList();
		
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
