package Spider;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Base.JobBean;

/**
 * 根据职位url，爬取相关职位信息
 * 51job包括：职位名称，
 *公司名，地址，工资，
 *发布日期，经验要求，
 *学历要求，招聘人数，职位信息，
 *公司类型，公司人数，公司方向 
 *职位在51job中的url
 * @author PowerZZJ
 * @version Date 2019年11月28日 
 */
public class SpiderJob extends Spider{
	
	private List<String> jobURLList;
	private Document document;
	private List<JobBean> jobBeanList;
	private JobBean jobBean;
	private String[] keyWordArray = new String[] {}; //关键字列表
	
	/**
	 * @param urlList url列表
	 */
	public SpiderJob(List<String> urlList) {
		this.jobURLList = urlList;
		this.jobBeanList = new ArrayList<>();
	}
	/**
	 * @param urlList url列表
	 * @param keyWordArray 职位信息关键字数组
	 */
	public SpiderJob(List<String> urlList, String...keyWordArray) {
		this(urlList);
		this.keyWordArray = keyWordArray;
	}
	
	
	/**
	 *从网页中提取出职位名称，
	 *公司名，地址，工资，
	 *发布日期，经验要求，
	 *学历要求，招聘人数，职位信息，
	 *公司类型，公司人数，公司方向 
	 *@return 一个不带jobURL的JobBean
	 */
	public JobBean GetPageInfo(Document document) {
		if(document == null) return null;
		Elements elements_Base = document.select("div[class=cn]");
		Elements elements_JobInfo = document.select("[class=bmsg job_msg inbox]");
		Elements elements_Company = document.select("div[class=com_tag] p");
		//如果有关键字判定职位信息是否包含关键
		String jobInfo = elements_JobInfo.text();
		if(keyWordArray.length>0) {
			for(String keyWord: keyWordArray) {
				if(!jobInfo.contains(keyWord)) {
					return null;
				}
			}
		}
		
		String jobName = elements_Base.select("h1").attr("title");
		String company = elements_Base.select("a").attr("title");
		String salary = elements_Base.select("strong").text();
		String companyType = elements_Company.get(0).attr("title");
		String staffNumber = elements_Company.get(1).attr("title");
		String companyOrientation = elements_Company.get(2).attr("title");
		
		String[]  infos = elements_Base.select("p[class=msg ltype]").get(0).ownText().split("    ");
		String address = infos[0];
		String exp = infos[1];
		String edu = infos[2];
		String offerNumber = infos[3];
		String date = infos[4];
		
		//生成空字符jobURL的jobBean
		JobBean jobBean = new JobBean(
				jobName, company, address,
				salary, date, exp,
				edu, offerNumber, jobInfo,
				companyType, staffNumber,companyOrientation,"");
		return jobBean;
	}
	
	/**
	 *整合爬取过程和遍历整个jobURLList中的职位信息
	 *把jobURL的加进JobBean
	 */
	@Override
	public void Spider() {
		System.out.println("Job信息开始爬取");
		if(jobURLList != null)	{
			for(String jobURL: jobURLList) {
				try {
					document = GetDom(jobURL);
					jobBean = GetPageInfo(document);
					jobBean.setJobURL(jobURL);
					jobBeanList.add(jobBean);
				}catch(Exception e) {
					continue;
				}
			}
		}
		System.out.println("Job信息爬取结束");
	}
	
	public List<JobBean> GetJobBeanList(){
		if(jobBeanList != null) {
			System.out.println("JobBean列表共"+jobBeanList.size()+"条信息");
		}
		return this.jobBeanList;
	}
	
}
