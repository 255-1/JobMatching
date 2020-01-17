package crawler;

import httpbrowser.HttpBrowser;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class JobURLCrawler {
    /**
     * @Author: PowerZZJ
     * @param: 普通url，关键字，职位url列表
     * @return: 是否爬取成功
     * @Description:本机爬取职位url列表
     */
    public static boolean urlParse(String url, String keyWord, List<String> jobUrlList) {
        if (url == null || url.length() == 0) {return false;}
        if (jobUrlList == null) {return false;}
        if (keyWord == null || keyWord.length() == 0) {return false;}
        String html = HttpBrowser.getHtml(url);
        if (html.length() == 0) {return false;}
        addJobUrlList(html, keyWord, jobUrlList);
        return true;
    }

    /**
     * @Author: PowerZZJ
     * @return: 是否爬取成功
     * @Description:代理爬取职位url列表重载
     */
    public static boolean urlParse(String url, String ipAddress, String ipPort,
                                   String keyWord, List<String> jobUrlList) {
        if (null == url || url.length() == 0) {return false;}
        if (null == jobUrlList) {return false;}
        if (null == ipAddress || ipAddress.length() == 0) {return false;}
        if (null == ipPort || ipPort.length() == 0) {return false;}
        if (keyWord == null || keyWord.length() == 0) {return false;}

        HttpHost proxy = HttpBrowser.getHttpHost(ipAddress, ipPort);
        return urlParse(url, proxy, keyWord, jobUrlList);
    }

    /**
     * @Author: PowerZZJ
     * @param: 普通url，代理，关键字，职位url列表
     * @return: 是否爬取成功
     * @Description:代理爬取职位url列表重载
     */
    public static boolean urlParse(String url, HttpHost proxy,
                                   String keyWord, List<String> jobUrlList) {
        if (null == url || url.length() == 0) {return false;}
        if (null == jobUrlList) {return false;}
        if (keyWord == null || keyWord.length() == 0) {return false;}

        String html = HttpBrowser.getHtml(url, proxy);
        if (html.length() == 0) {return false;}
        addJobUrlList(html, keyWord, jobUrlList);
        return true;
    }

//--------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @Description:解析网页，添加joburl到joburl列表中
     */
    public static void addJobUrlList(String html, String keyWord, List<String> jobUrlList) {
        Document document = Jsoup.parse(html);
        jobUrlList.addAll(getJobUrlListFromDocument(document, keyWord));
    }

    /**
     * @Author: PowerZZJ
     * @param: 通过Jsoup解析后的Document
     * @return: 职业url列表，关键字
     * @Description:通过Document提取 职业url
     */
    public static List<String> getJobUrlListFromDocument(Document document, String keyWord) {
        List<String> jobUrlList = new ArrayList<>();
        Elements elements = document.select("p[class^=t1] a[target]");
        for (Element element : elements) {
            if (!"".equals(keyWord)) {
                //统一为小写
                keyWord = keyWord.toLowerCase();
                String titleToLowerCase = element.attr("title").toLowerCase();
                if (titleToLowerCase.contains(keyWord)) {
                    jobUrlList.add(element.attr("href"));
                }
            } else {
                jobUrlList.add(element.attr("href"));
            }
        }
        return jobUrlList;
    }
}
