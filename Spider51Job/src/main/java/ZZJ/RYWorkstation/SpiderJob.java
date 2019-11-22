package ZZJ.RYWorkstation;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Utils.JobBean;



/**爬取网页信息
 * @author PowerZZJ
 *
 */
public class SpiderJob {
	
	private List<String> jobUrlList;
	private Document document;//网页全部信息
	private List<JobBean> jobBeanList;
	private String keyWord; //职位要求的关键字
	
	public SpiderJob(List<String> strURL, String jobInfoKeyWord) {
		this.keyWord = jobInfoKeyWord;
		this.jobUrlList = strURL;
		this.document = null;
		this.jobBeanList = new ArrayList<JobBean>();
	}
	
	/**获取网页全部信息
	 * @param 网址
	 * @return 网页全部信息
	 */
	public Document GetDom(String strURL) {
		try {
			URL url = new URL(strURL);
			//解析，并设置超时
			document = Jsoup.parse(url, 4000);
			return document;
		}catch(Exception e) {
			System.out.println("网址404"+strURL);
		}
		return null;
	}
	

	/**筛选当前网页信息,转成JobBean对象，存入容器
	 * @param document 网页全部信息
	 */
	public void GetPageInfo(Document document) {
		Elements elements = document.select(".cn");
		
		String jobName = elements.select("h1").attr("title");
		String company = elements.select("a").attr("title");
		//split后的绝不可以改
		String infos[] = elements.select("p").get(1).ownText().split("    ");
		String address = infos[0];
		String exp = infos[1];
		String edu = infos[2];
		String offerNumber = infos[3];
		String date = infos[4];
		String salary = elements.select("strong").text();
		
		elements = document.select(".tCompanyPage")
				.select(".tCompany_main")
				.select(".tBorderTop_box");
		String jobInfo = elements.get(0).text();
		
		elements = document
				.select(".tCompanyPage")
				.select(".tCompany_sidebar")
				.select(".com_tag");
		String companyType = elements.select("p").get(0).attr("title");
		String staffNumber = elements.select("p").get(1).attr("title");
		String companyOrientation = elements.select("p").get(2).attr("title");
		
		JobBean jobBean = new JobBean(
				jobName, company, address,
				salary, date, exp,
				edu, offerNumber, jobInfo,
				companyType, staffNumber, companyOrientation);
		
		//合法判断
		if(isValide(jobBean)) {
			jobBeanList.add(jobBean);
		}
	}
	
	//合法性判断，职位要求信息也要有“大数据”或者“data”
	private boolean isValide(JobBean jobBean) {
		if(jobBean.getJobInfo().contains(keyWord)) {
			return true;
		}else {
			return false;
		}
	}
	
	/**开始爬取
	 * 
	 */
	public void SpiderJob() {
		System.out.println("开始爬取URL列表中的信息");
		for(String strURL: jobUrlList) {
			try {
				//获取全部信息
				document = GetDom(strURL);
				//把相关信息加入容器
				GetPageInfo(document);
			}catch(Exception e) {
				//有些网页可能过期了
				continue;
			}
		}
	}
	
	public List<JobBean> GetJobBeanList() {
		return this.jobBeanList;
	}
	

}
