package Spider;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Spider父类，
 * 爬取51job,liepin网全部网页的 方法
 * Spider功能从这个类中继承
 * @author PowerZZJ
 * @version Date 2019年11月26日 
 */
public class Spider {
	
	/**获取51job和猎聘网
	 * @author PowerZZJ
	 * @param httpEntity
	 * @return
	 */
	public String GetCharset(byte[] bytes) {
		//以下字符集解析与转换的代码，不要动.
		String get_Charset_String = new String(bytes);
		Document get_Charset_Document = Jsoup.parse(get_Charset_String);
		//字符集信息提取，51job和猎聘
		return get_Charset_Document.select("meta[http-equiv=Content-Type]")
				.attr("content").split("=")[1];
	}
	
	/**获取URL中的非js加密的网页
	 * @author PowerZZJ
	 * @param strURL 
	 */
	public Document GetDom(String strURL) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(strURL);
		httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(4000).setConnectionRequestTimeout(1000)
				.setSocketTimeout(4000).build();
		httpGet.setConfig(requestConfig);
		
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			if(httpResponse.getStatusLine().getStatusCode() == 200) {
				//以下字符集解析与转换的代码，不要动，
				//不要尝试使用EntityUtils.toString()方法
				byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
				String Ori_Entity = new String(bytes, GetCharset(bytes));
				//转换为统一的utf-8
				String entity = new String(Ori_Entity.getBytes(), "utf-8");
				return Jsoup.parse(entity);
			}
		}catch(Exception e) {
			System.out.println("网页连接失败:"+strURL);
//			e.printStackTrace();
		}
		return null;
	}
	
	
	/**整合爬取全过程进行爬取
	 * @author PowerZZJ
	 */
	public void Spider() {}

	
}
