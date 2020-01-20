package crawler;

import bean.JobBean;
import httpbrowser.HttpBrowser;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class JobInfoCrawler {

    /**
     * @Author: PowerZZJ
     * @param: 职位url，JobBean
     * @return: 是否爬取成功
     * @Description:本机爬取职位信息，直接修改JobBean相关属性
     */
    public static boolean jobInfoParse(String url, JobBean jobBean) {
        if (url == null || "".equals(url)) {return false;}
        if (jobBean == null) {return false;}

        String html = HttpBrowser.getHtml(url);
        if (html.length() == 0) {return false;}
        return getJobBean(html, url, jobBean);
    }

    /**
     * @Author: PowerZZJ
     * @param: 职位url，代理，JobBean
     * @return: 是否爬取成功
     * @Description:使用代理爬取职位信息，直接修改JobBean相关属性
     */
    public static boolean jobInfoParse(String url, HttpHost proxy, JobBean jobBean) {
        if (null == url || url.length() == 0) {return false;}
        if (null == jobBean) {return false;}

        String html = HttpBrowser.getHtml(url, proxy);
        if (html.length() == 0) {return false;}
        return getJobBean(html, url, jobBean);
    }

    /**
     * @Author: PowerZZJ
     * @param: 职位信息
     * @return: 是否爬取成功
     * @Description:使用代理爬取职位信息重载
     */
    public static boolean jobInfoParse(String url, String ipAddress, String ipPort, JobBean jobBean) {
        if (null == url || url.length() == 0) {return false;}
        if (null == ipAddress || ipAddress.length() == 0) {return false;}
        if (null == ipPort || ipPort.length() == 0) {return false;}
        if (null == jobBean) {return false;}
        HttpHost proxy = HttpBrowser.getHttpHost(ipAddress, ipPort);
        return jobInfoParse(url, proxy, jobBean);
    }

//--------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @Description:解析网页，修改JobBean属性
     */
    public static boolean getJobBean(String html, String url, JobBean jobBean) {
        Document document = Jsoup.parse(html);
        return getJobBeanFromDocument(document, url, jobBean);
    }

    /**
     * @Author: PowerZZJ
     * @param: 通过Jsoup解析后的Document
     * @return: 是勾爬取职位信息成功
     * @Description:通过Document提取职业信息
     */
    public static boolean getJobBeanFromDocument(Document document, String url, JobBean jobBean) {
        List<JobBean> jobBeanList = new ArrayList<>();
        //会遇到非51job网站的招聘信息，直接放弃
        Elements elements_Base = document.select("div[class=cn]");
        Elements elements_JobInfo = document.select("[class=bmsg job_msg inbox]");
        Elements elements_Company = document.select("div[class=com_tag] p");
        if (elements_Base == null || elements_Base.size() == 0) {return false;}
        if (elements_JobInfo == null || elements_JobInfo.size() == 0) {return false;}
        if (elements_Company == null || elements_Company.size() == 0) {return false;}

        String jobInfo = elements_JobInfo.toString();
        //去除空格，标签的换行替换为其他指定字符，去除标签<>
        jobInfo = jobInfo.replaceAll("&nbsp;", " ");
        jobInfo = jobInfo.replaceAll("[\\r|\\n]+", "linefeed");
        jobInfo = jobInfo.replaceAll("<[^>]+>", "");

        String jobName = elements_Base.select("h1").attr("title");
        String company = elements_Base.select("a").attr("title");
        String salary = elements_Base.select("strong").text();

        if (elements_Company.size() == 0) {return false;}
        String companyType = elements_Company.get(0).attr("title");
        String staffNumber = elements_Company.get(1).attr("title");
        String companyOrientation = elements_Company.get(2).attr("title");
        //按照教育要求长度分类
        String[] infos = elements_Base.select("p[class=msg ltype]").get(0).ownText().split("    ");
        String address = null;
        String exp = null;
        String edu = null;
        String offerNumber = null;
        String date = null;
        //有些职位对学历没有要求
        if (infos.length == 4) {
            address = infos[0];
            exp = infos[1];
            offerNumber = infos[2];
            date = infos[3];
            edu = "没有要求";
        }
        //一般情况
        if (infos.length >= 5) {
            address = infos[0];
            exp = infos[1];
            edu = infos[2];
            offerNumber = infos[3];
            date = infos[4];
        }

        jobBean.setJobName(jobName);
        jobBean.setCompany(company);
        jobBean.setAddress(address);
        jobBean.setSalary(salary);
        jobBean.setDate(date);
        jobBean.setExp(exp);
        jobBean.setEdu(edu);
        jobBean.setOfferNumber(offerNumber);
        jobBean.setJobInfo(jobInfo);
        jobBean.setCompanyType(companyType);
        jobBean.setStaffNumber(staffNumber);
        jobBean.setCompanyOrientation(companyOrientation);
        jobBean.setJobURL(url);

        return true;
    }

}
