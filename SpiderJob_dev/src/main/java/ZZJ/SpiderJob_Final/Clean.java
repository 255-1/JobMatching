package ZZJ.SpiderJob_Final;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import Base.JobBean;

/**
 * 清洗类，在做完域分析后确定下各字段范围
 * @author PowerZZJ
 *
 */
/**
 * @author PowerZZJ
 *
 */
/**
 * @author PowerZZJ
 *
 */
public class Clean {
	private List<JobBean> jobBeanList;
	private List<JobBean> removeList;
	
	//个性化定制
	private List<String> jobNameList = Arrays.asList("测试","分析","开发","运维","架构");
	private List<String> companyOrientationList = Arrays.asList("通信","金融","服务","计算机","互联网");
	//注意小写
	private List<String> jobInfoList = Arrays.asList("hadoop","spark","hive",
			"java","sql","kafka","tableau","python","scala","linux","etl");
	
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
		InitDate();
		
	}
	
	/**
	 * 初始化日期时间为近三天
	 */
	private void InitDate() {
		//发布日期处理
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
	
	public List<JobBean> StartClean() {
		System.out.println("开始字段处理");
		for(JobBean jobBean: jobBeanList) {
			if(jobBean.getJobName().isEmpty() || 
					jobBean.getCompany().isEmpty() ||
					jobBean.getAddress().isEmpty() ||
					jobBean.getSalary().isEmpty() ||
					jobBean.getDate().isEmpty() ||
					jobBean.getExp().isEmpty() ||
					jobBean.getEdu().isEmpty() ||
					jobBean.getOfferNumber().isEmpty() ||
					jobBean.getCompanyType().isEmpty() ||
					jobBean.getStaffNumber().isEmpty() ||
					jobBean.getCompanyOrientation().isEmpty()||
					jobBean.getJobURL().isEmpty()) {
				removeList.add(jobBean);
				continue;
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
			
			//移出职位名称不符合的,并统一职位名
			boolean notRemove_flag = false;
			for(String jobName: jobNameList) {
				if(jobBean.getJobName().contains(jobName)) {
					notRemove_flag = true;
					jobBean.setJobName("大数据"+jobName+"师");
				}
			}
			if(!notRemove_flag) {
				removeList.add(jobBean);
				continue;
			}

			//companyOrientation字段处理
			notRemove_flag = false;
			for(String companyOrientation: companyOrientationList) {
				if(jobBean.getCompanyOrientation().contains(companyOrientation)) {
					notRemove_flag = true;
					jobBean.setCompanyOrientation(companyOrientation);
				}
			}
			if(!notRemove_flag) {
				removeList.add(jobBean);
				continue;
			}
			
			
			
			//jobInfo字段处理,有英文名词用小写
			String skill="";
			for(String jobInfo : jobInfoList) {
				if(jobBean.getJobInfo().toLowerCase().contains(jobInfo)) {
					skill += jobInfo.toUpperCase()+",";
				}
			}
			if(skill.isEmpty()) {
				removeList.add(jobBean);
				continue;
			}else {
				jobBean.setJobInfo(skill);
			}
			
		}
		System.out.println("不符合的信息有:"+removeList.size()+"条");
		jobBeanList.removeAll(removeList);
		return jobBeanList;
	}
	
	
}
