package ZZJ.RYWorkstation;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Utils.ConnectMySQL;
import Utils.DBUtils;
import Utils.JobBean;
import Utils.JobBeanUtils;

public class Main {

	public static void main(String[] args) {

		List<JobBean> jobBeanList = new ArrayList<>();
		
		String URL = null;
		Scanner sc = new Scanner(System.in);
		System.out.println("输入51job：大数据+上海的搜索界面");
		URL = sc.nextLine();
		
		//保存职业名带“大数据”关键字的职位URL
		SpiderURL spider = new SpiderURL(URL,"大数据");
		spider.SpiderURL();
		List<String> urlList = spider.GetUrlList();
		
		//根据URL进去爬取，职位信息带有“大数据”关键字的信息
		SpiderJob sj = new SpiderJob(urlList,"大数据");
		sj.SpiderJob();
		jobBeanList = sj.GetJobBeanList();
		
		//调用JobBean工具类保存JobBeanList到本地
		//作为没请洗过的本地备份
		JobBeanUtils.saveJobBeanList(jobBeanList,"jobInfo_Ori.txt");
		
		jobBeanList = JobBeanUtils.LoadJobBeanList("jobInfo_Ori.txt");
		//清洗
		Clean clean = new Clean(jobBeanList);
		clean.startClean();
		jobBeanList = clean.GetJobBeanList();
		
		//将清洗后的数据存入数据库
		ConnectMySQL cm = new ConnectMySQL();
		Connection conn = cm.getConn();
		//调用数据库工具类将JobBean容器存入数据库
		DBUtils.insert(conn, "jobInfo", jobBeanList);
	}



}
