package ZZJ.SpiderJob_Final;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import Base.ConnectMySQL;
import Base.JobBean;
import Spider.SpiderJob;
import Spider.SpiderURL;
import StaticUtils.DBUtils;
import StaticUtils.JobBeanUtils;

/**
 * 每一个单元测试，至少debug确认内部流程
 * @author PowerZZJ
 * @version Date 2019年11月26日 
 */
public class UnitTest {

	public static void main(String[] args) {
//		//JobBean测试
//		JobBean jobBean = new JobBean("a","b","c","d",
//				"e","f","g","h",
//				"i","j","k","l",
//				"m");
//		System.out.println(jobBean);
//		System.out.println(jobBean.saveString());
//-----------------------------------------------------------------------------------	
//		//ConnectMySQL测试
//		Connection conn = new ConnectMySQL().GetConnection();
//-----------------------------------------------------------------------------------------------		
//		//DBUtils测试
//		JobBean jobBean = new JobBean("a","b","c","d",
//				"e","f","g","h",
//				"i","j","k","l",
//				"m");
//		List<JobBean>jobBeanList = new ArrayList<>();
//		Connection conn = new ConnectMySQL().GetConnection();
//		
//		jobBeanList.add(jobBean);
//		DBUtils.InsertJobBeanList(conn, "jobinfo_test", jobBeanList);
//		DBUtils.TruncateTable(conn, "jobinfo_test");
//		DBUtils.InsertJobBeanList(conn, "jobinfo_test", jobBeanList);
//		
//		jobBeanList = DBUtils.SelectJobBeanList(conn, "jobinfo_test");
//		System.out.println(jobBeanList);
		
//-------------------------------------------------------------------------------------------------		
//		//JobBeanUtils测试
//		JobBean jobBean = new JobBean("a","b","c","d",
//				"e","f","g","h",
//				"i","j","k","l",
//				"m");
//		List<JobBean>jobBeanList = new ArrayList<>();
//		jobBeanList.add(jobBean);
//		
//		JobBeanUtils.SaveJobBeanList(jobBeanList, "JobBeanUtilsTest.txt");
//		jobBeanList = JobBeanUtils.LoadJobBeanList("JobBeanUtilsTest.txt");
//		System.out.println(jobBeanList);
		
//--------------------------------------------------------------------------------------------------
//		//Spider类测试
//		Spider spider = new Spider();
//		Spider spider_With_KeyWord = new Spider("zzj");
//		
//		spider.GetDom("https://www.baidu.com");
//		spider_With_KeyWord.GetDom("https://www.baidu.com");
//		
//		spider.GetDom("https://www.1231dsad.com");
//		spider_With_KeyWord.GetDom("https://www.1231dsad.com");
		
//--------------------------------------------------------------------------------------------------
//		//SpiderURL测试
//		String strURL = "https://search.51job.com/list/020000,000000,0000,00,9,99,%25E5%25A4%25A7%25E6%2595%25B0%25E6%258D%25AE,2,1.html?lang=c&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&ord_field=0&dibiaoid=0&line=&welfare=";
//		strURL = "https://search.51job.com/list/020000,000000,0000,00,9,99,%25E5%25A4%25A7%25E6%2595%25B0%25E6%258D%25AE,2,290.html?lang=c&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&ord_field=0&dibiaoid=0&line=&welfare=";
////		//错误网站测试
////		strURL = "https://www.baidu.com";
////		strURL = "https://www.baiduasdasdasd.com";
////		strURL = "";
//		SpiderURL spiderURL = new SpiderURL(strURL);
//		spiderURL.Spider();
//		System.out.println(spiderURL.GetUrlList());
//		
//		SpiderURL spider_With_KeeyWord = new SpiderURL(strURL, "经理");
//		spider_With_KeeyWord.Spider();
//		System.out.println(spider_With_KeeyWord.GetUrlList());
		
//--------------------------------------------------------------------------------------------------------------
		//SpiderJob 测试
//		List<String> jobURLList=new ArrayList<>();
//		jobURLList.add("https://jobs.51job.com/shenzhen/97355666.html?s=01&t=0");
//		
//		SpiderJob spider_Without_KeyWord = new SpiderJob(jobURLList);
//		spider_Without_KeyWord.Spider();
//		System.out.println(spider_Without_KeyWord.GetJobBeanList());
		
//		String[] keyWord = new String[]{"java"};
//		SpiderJob spider_With_KeyWord = new SpiderJob(jobURLList, keyWord);
//		spider_With_KeyWord.Spider();
//		System.out.println(spider_With_KeyWord.GetJobBeanList().size());

//-------------------------------------------------------------------------------------------------------------------------
		//SpiderURL 和 SpiderJob集成测试
//		List<String> urlList = new ArrayList<>();
//		List<JobBean> jobBeanList = new ArrayList<>();
//		String[] jobInfoKeyWord = new String[]{"Java"};
//		
//		String strURL = "https://search.51job.com/list/020000,000000,0000,00,9,99,%25E5%25A4%25A7%25E6%2595%25B0%25E6%258D%25AE,2,1.html?lang=c&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&ord_field=0&dibiaoid=0&line=&welfare=";
//		//错误网站测试
//		strURL = "https://www.baidu.com";
//		strURL = "https://www.bai2131231du.com";
		//不带关键字测试
//		SpiderURL spiderURL = new SpiderURL(strURL);
//		spiderURL.Spider();
//		urlList = spiderURL.GetUrlList();
//		
//		SpiderJob spiderJob = new SpiderJob(urlList);
//		spiderJob.Spider();
//		jobBeanList = spiderJob.GetJobBeanList();
//		
//		for(JobBean jobBean: jobBeanList) {
//			System.out.println(jobBean);
//		}
		//带关键字测试
//		SpiderURL spiderURL = new SpiderURL(strURL,"大数据");
//		spiderURL.Spider();
//		urlList = spiderURL.GetUrlList();
//		
//		SpiderJob spiderJob = new SpiderJob(urlList);
//		spiderJob.Spider();
//		jobBeanList = spiderJob.GetJobBeanList();
//		StaticUtils.JobBeanUtils.SaveJobBeanList(jobBeanList, "bigdata.txt");
//-------------------------------------------------------------------------------------------------------------------------------------
		//其他集成测试
//		List<String> urlList = new ArrayList<>();
//		List<JobBean> jobBeanList = new ArrayList<>();
//		String[] jobInfoKeyWord = new String[]{"Java"};
		
//		jobBeanList = JobBeanUtils.LoadJobBeanList("bigdata.txt");
//		for(JobBean jobBean: jobBeanList) {
//			System.out.println(jobBean);
//		}
//		Connection conn = new ConnectMySQL().GetConnection();
//		jobBeanList = JobBeanUtils.LoadJobBeanList("bigdata.txt");
//		DBUtils.InsertJobBeanList(conn, "jobinfo_ori", jobBeanList);
//--------------------------------------------------------------------------------------------------------------------------------------------
		//Clean测试
		List<JobBean> jobBeanList = new ArrayList<>();
		jobBeanList = JobBeanUtils.LoadJobBeanList("BigData_20191128_Ori.txt");
		Clean clean = new Clean(jobBeanList);
		jobBeanList = clean.StartClean();
		
		Connection conn = new ConnectMySQL().GetConnection();
		DBUtils.InsertJobBeanList(conn, "jobinfo_clean", jobBeanList);
		
		
		
		
		
		
		
		
		
	}

}
