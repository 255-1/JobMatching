package ZZJ.RYWorkstation;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Base.JobBean;



/**爬取带有关键字的职业的URL
 * @author PowerZZJ
 *
 */
public class SpiderURL {
	//记录爬到第几页
	private static int pageCount = 1;
	
	private String keyWord; //职位名字关键字
	private String strURL;
	private String nextPageURL;
	private Document document;//网页全部信息
	private List<String> urlList;
	
	public SpiderURL(String strURL, String jobNamekeyWord) {
		this.keyWord = jobNamekeyWord;
		this.strURL = strURL;
		nextPageURL = strURL;//下一页URL初始化为当前，方便遍历
		urlList = new ArrayList<String>();
		
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
			System.out.println("getDom失败");
			e.printStackTrace();
		}
		return null;
	}
	

	/**筛选当前网页信息,筛选关键字
	 * @param document 网页全部信息
	 */
	public void GetPageInfo(Document document) {
		//通过CSS选择器用#resultList .el获取el标签信息
		Elements elements = document.select("#resultList .el");
		//总体信息删去
		elements.remove(0);
		//筛选信息
		for(Element element: elements) {
			Elements elementsSpan = element.select("span");
			String jobName = elementsSpan.get(0).select("a").attr("title");
			String jobURL = elementsSpan.select("a").attr("href");
			//名字不带关键字的不加入urlList
			if(jobName.contains(keyWord)) {
				urlList.add(jobURL);
			}
			
		}
	}
	
	/**获取下一页的URL
	 * @param document 网页全部信息
	 * @return 有,则返回URL
	 */
	public String GetNextPageURL(Document document) {
		try {
			Elements elements = document.select(".bk");
			//第二个bk才是下一页
			Element element = elements.get(1);
			nextPageURL = element.select("a").attr("href");
			if(nextPageURL != null) {
				System.out.println("---------"+(pageCount++)+"--------");
				return nextPageURL;
			}
		}catch(Exception e) {
			System.out.println("获取下一页URL失败");
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**开始爬取
	 * 
	 */
	public void SpiderURL() {
		System.out.println("开始爬取URL信息");
		while(!nextPageURL.equals("")) {
			//获取全部信息
			document = GetDom(nextPageURL);
			//把相关信息加入容器
			GetPageInfo(document);
			//查找下一页的URL
			nextPageURL = GetNextPageURL(document);
		}
	}
	
	//获取每个工作的URL
	public List<String> GetUrlList() {
		System.out.println("URL列表总大小是："+urlList.size());
		return urlList;
	}
}
