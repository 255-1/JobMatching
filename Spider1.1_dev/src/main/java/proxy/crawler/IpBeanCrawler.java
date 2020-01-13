package proxy.crawler;

import httpbrowser.HttpBrowser;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import proxy.bean.IpBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class IpBeanCrawler {

    /**
     * @Author: PowerZZJ
     * @param: url， 代理列表
     * @return: 是否爬取成功
     * @Description:本机爬取西刺代理
     */
    public static boolean urlParse(String url, List<IpBean> ipBeanList) {
        if (null == ipBeanList) return false;
        //获取html
        String html = HttpBrowser.getHtml(url);
        if (null == html) return false;
        addIpBeanList(html, ipBeanList);
        return true;
    }


    /**
     * @Author: PowerZZJ
     * @param: url， 代理，代理列表
     * @return: 是否爬取成功
     * @Description:代理爬取西刺代理
     */
    public static boolean urlParse(String url, HttpHost proxy, List<IpBean> ipBeanList) {
        if (null == ipBeanList) return false;
        //获取html
        String html = HttpBrowser.getHtml(url, proxy);
        if (null == html) return false;
        addIpBeanList(html, ipBeanList);
        return true;
    }

    /**
     * @Author: PowerZZJ
     * @param: 职位列表
     * @return: 是否爬取成功
     * @Description:代理爬取西刺代理的重载
     */
    public static boolean urlParse(String url, String ipAddress, String ipPort, List<IpBean> ipBeanList) {
        HttpHost proxy = HttpBrowser.getHttpHost(ipAddress, ipPort);
        return urlParse(url, proxy, ipBeanList);
    }
//------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @Description:解析网页，添加ipBean到列表中
     */
    public static void addIpBeanList(String html, List<IpBean> ipBeanList) {
        //Jsoup解析html
        Document document = Jsoup.parse(html);
        //提取关键字，存入ipBeanListList
        ipBeanList.addAll(getIpBeanListListFromDocument(document));
    }

    /**
     * @Author: PowerZZJ
     * @param: 通过Jsoup解析后的Document
     * @return: 代理ip列表
     * @Description:通过Document提取代理ip属性
     */
    public static List<IpBean> getIpBeanListListFromDocument(Document document) {
        List<IpBean> ipBeanList = new ArrayList<>();
        Elements trs = document.select("table[id=ip_list] tbody tr");
        for (int i = 1; i < trs.size(); i++) {
            String ipAddress = trs.get(i).select("td").get(1).text();
            String ipPort = trs.get(i).select("td").get(2).text();
            String ipType = trs.get(i).select("td").get(5).text();
            String ipSpeed = trs.get(i).select("td").get(6).select("div[class=bar]").
                    attr("title");
            if (ipAddress == null || ipAddress.length() == 0) continue;
            if (ipPort == null || ipPort.length() == 0) continue;
            if (ipType == null || ipType.length() == 0) continue;
            if (ipSpeed == null || ipSpeed.length() == 0) continue;
            //创建ipBean对象
            IpBean ipBean = new IpBean();
            ipBean.setIpAddress(ipAddress);
            ipBean.setIpPort(ipPort);
            ipBean.setIpType(ipType);
            ipBean.setIpSpeed(ipSpeed);

            ipBeanList.add(ipBean);
        }
        return ipBeanList;
    }


}
