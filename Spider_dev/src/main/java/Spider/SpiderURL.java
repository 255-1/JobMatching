package Spider;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 解析爬取（带有关键字）的职位url列表
 * @author（PowerZZJ
 * @version Date 2019年11月26日 
 */
public class SpiderURL extends Spider{
	private int PAGE_COUNT = 1;
	
	private Document document;
	private String keyWord;
	private String nextPageURL;
	private List<String> urlList;
	
	/**
	 * @param startURL 起始URL
	 */
	public SpiderURL(String startURL) {
		super();
		this.nextPageURL = startURL;
		this.urlList = new ArrayList<String>();
	}
	/**
	 * @param startURL 起始URL
	 * @param jobNameKeyWord 职位名关键字
	 */
	public SpiderURL(String startURL, String jobNameKeyWord) {
		this(startURL);
		this.keyWord = jobNameKeyWord;
	}
	
	/**
	 *筛选出职位名和职位url,
	 *keyWord不为空的情况下，对jobName进行筛选，否则全部url爬取
	 *@return url列表
	 */
	public List<String> GetPageInfo(Document document) {
		if(document == null) return null;
		Elements elements = document.select("p[class^=t1] a[target]");
		for(Element element :elements) {
			if(keyWord != null) {
				if(element.attr("title").contains(keyWord)) {
					urlList.add(element.attr("href"));
				}
			}else {
				urlList.add(element.attr("href"));
			}
		}
		return urlList;
	}
	
	/**获取下一也的URL
	 * @author PowerZZJ
	 *如果单独调用此方法必须保证事先获取了Spider下的Document
	 *@return 下一页url的字符串
	 **/
	public String GetNextPageURL(Document document) {
		if(document == null) return null;
		Elements elements = document.select("li[class=bk]");
		//获取下一页的连接，没有为空字符串
		String nextPage = elements.get(1).select("a").attr("href");
		return nextPage.isEmpty()?null:nextPage;
	}

	/**
	 *整合爬取迭代全过程
	 */
	@Override
	public void Spider() {
		System.out.println("URL爬取开始");
		while(nextPageURL != null) {
			try {
				System.out.println("--------------"+(PAGE_COUNT++)+"----------------");
				document = GetDom(nextPageURL);
				urlList = GetPageInfo(document);
				nextPageURL = GetNextPageURL(document);
			}catch(Exception e) {
				break;
			}
		}
		System.out.println("URL爬取结束");
	}
	
	/**返回URL列表
	 * @author PowerZZJ
	 * @return
	 */
	public List<String> GetUrlList(){
		if(urlList != null) {
			System.out.println("URL列表共"+urlList.size()+"条信息");
		}
		return urlList;
	}
}
