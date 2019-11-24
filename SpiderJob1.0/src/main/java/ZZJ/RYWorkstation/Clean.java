package ZZJ.RYWorkstation;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import Base.ConnectMySQL;
import Base.JobBean;
import Utils.DBUtils;
import Utils.JobBeanUtils;
/**
 * 清洗类，在做完域分析后确定下各字段范围
 * @author PowerZZJ
 *
 */
public class Clean {
	private List<JobBean> jobBeanList;
	private List<JobBean> removeList;
	
	private List<String> dateList;
	private List<String> addressList =Arrays.asList("上海","上海-黄浦区","上海-闵行区",
			"上海-徐汇区","上海-浦东新区","上海-长宁区",
			"上海-静安区","上海-普陀区", "上海-青浦区",
			"上海-虹口区","上海-杨浦区","上海-嘉定区",
			"上海-宝山区","上海-松江区","上海-奉贤区");
	private List<String> expList =Arrays.asList("5-7年经验","无工作经验","3-4年经验",
			"1年经验","2年经验","8-9年经验");
	private List<String> eduList =Arrays.asList("本科","硕士","大专","高中","中专","博士");
	private List<String> offerNumberList =Arrays.asList("招1人","招30人","招若干人",
			"招2人","招5人","招15人",
			"招3人","招10人","招6人","招4人");
	private List<String> companyTypeList =Arrays.asList("国企","民营公司","合资",
			"外资（欧美）","上市公司","事业单位",
			"外资（非欧美）","创业公司");
	private List<String> staffNumberList =Arrays.asList("500-1000人","150-500人","50-150人",
			"1000-5000人","5000-10000人","少于50人",
			"10000人以上");
	
	public Clean(List<JobBean> jobBeanList) {
		this.jobBeanList = jobBeanList;
		this.removeList = new ArrayList<>();
		
		//日期处理
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_MONTH);
		int today_month = calendar.get(Calendar.MONTH)+1;
		calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-1);
		int yesterday = calendar.get(Calendar.DAY_OF_MONTH);
		int yesterday_month = calendar.get(Calendar.MONTH)+1;
		calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-1);
		int day_before_yesterday = calendar.get(Calendar.DAY_OF_MONTH);
		int day_before_yesterday_month = calendar.get(Calendar.MONTH)+1;
		String day1 = today_month+"-"+today+"发布";
		String day2 = yesterday_month+"-"+yesterday+"发布";
		String day3 = day_before_yesterday_month+"-"+day_before_yesterday+"发布";
		dateList =Arrays.asList(day1,day2,day3);
	}
	
	
	public void startClean() {
		System.out.println("开始字段处理");
		for(JobBean jobBean: jobBeanList) {
			if(jobBean.getJobName()==null || 
					jobBean.getCompany()==null ||
					jobBean.getAddress()==null ||
					jobBean.getSalary()==null ||
					jobBean.getDate()==null ||
					jobBean.getExp()==null ||
					jobBean.getEdu()==null ||
					jobBean.getOfferNumber()==null ||
					jobBean.getCompanyType()==null ||
					jobBean.getStaffNumber()==null ||
					jobBean.getCompanyOrientation() == null) {
				removeList.add(jobBean);
				continue;
			}
			//移出职位名称不符合的,并统一职位名
			if(!jobBean.getJobName().contains("开发") && 
					!jobBean.getJobName().contains("架构") &&
					!jobBean.getJobName().contains("运维") &&
					!jobBean.getJobName().contains("分析") &&
					!jobBean.getJobName().contains("测试")) {
				removeList.add(jobBean);
				continue;
			}else {
				if(jobBean.getJobName().contains("架构"))
					jobBean.setJobName("大数据架构师");
				if(jobBean.getJobName().contains("开发"))
					jobBean.setJobName("大数据开发");
				if(jobBean.getJobName().contains("运维"))
					jobBean.setJobName("大数据运维");
				if(jobBean.getJobName().contains("分析"))
					jobBean.setJobName("大数据分析");
				if(jobBean.getJobName().contains("测试"))
					jobBean.setJobName("大数据测试");
			}
			//移出位置不符合的
			if(!addressList.contains(jobBean.getAddress())) {
				removeList.add(jobBean);
				continue;
			}
			
			//工资字段处理
			if(!jobBean.getSalary().contains("万/月")) {
				removeList.add(jobBean);
				continue;
			}
			
			//date字段处理
			if(!dateList.contains(jobBean.getDate())) {
				removeList.add(jobBean);
				continue;
			}
			
			//exp字段处理
			if(!expList.contains(jobBean.getExp())) {
				removeList.add(jobBean);
				continue;
			}
			
			//edu字段处理
			if(!eduList.contains(jobBean.getEdu())) {
				removeList.add(jobBean);
				continue;
			}
			
			//offerNumber字段处理
			if(!offerNumberList.contains(jobBean.getOfferNumber())) {
				removeList.add(jobBean);
				continue;
			}
			
			//companyType字段处理
			if(!companyTypeList.contains(jobBean.getCompanyType())) {
				removeList.add(jobBean);
				continue;
			}
			
			//staffNumber字段处理
			if(!staffNumberList.contains(jobBean.getStaffNumber())) {
				removeList.add(jobBean);
				continue;
			}
		
			//companyOrientation字段处理
			if(!jobBean.getCompanyOrientation().contains("互联网") && 
					!jobBean.getCompanyOrientation().contains("计算机") &&
					!jobBean.getCompanyOrientation().contains("服务") &&
					!jobBean.getCompanyOrientation().contains("金融") &&
					!jobBean.getCompanyOrientation().contains("通信")) {
				removeList.add(jobBean);
				continue;
			}else {
				if(jobBean.getCompanyOrientation().contains("互联网"))
					jobBean.setCompanyOrientation("互联网");
				if(jobBean.getCompanyOrientation().contains("计算机"))
					jobBean.setCompanyOrientation("计算机");
				if(jobBean.getCompanyOrientation().contains("服务"))
					jobBean.setCompanyOrientation("服务");
				if(jobBean.getCompanyOrientation().contains("金融"))
					jobBean.setCompanyOrientation("金融");
				if(jobBean.getCompanyOrientation().contains("通信"))
					jobBean.setCompanyOrientation("通信");
			}
			
			//jobInfo字段处理
			String skill="";
			String jobInfo = jobBean.getJobInfo().toLowerCase();
			if(jobInfo.contains("hadoop")) {
				skill+="Hadoop,";
			}
			if(jobInfo.contains("spark")) {
				skill+="Spark,";
			}
			if(jobInfo.contains("hive")) {
				skill+="Hive,";
			}
			if(jobInfo.contains("java")) {
				skill+="Java,";
			}
			if(jobInfo.contains("sql")) {
				skill+="SQL,";
			}
			if(jobInfo.contains("kafka")) {
				skill+="Kafka,";
			}
			if(jobInfo.contains("tableau")) {
				skill+="Tableau,";
			}
			if(jobInfo.contains("python")) {
				skill+="Python,";
			}
			if(jobInfo.contains("scala")) {
				skill+="Scala,";
			}
			if(jobInfo.contains("linux")) {
				skill+="Linux,";
			}
			if(skill.contentEquals("")) {
				removeList.add(jobBean);
			}else {
				jobBean.setJobInfo(skill);
			}
			
		}
		System.out.println("不符合的信息有:"+removeList.size()+"条");
		jobBeanList.removeAll(removeList);
	}
	
	public List<JobBean> GetJobBeanList(){
		return jobBeanList;
	}
	
	
}
