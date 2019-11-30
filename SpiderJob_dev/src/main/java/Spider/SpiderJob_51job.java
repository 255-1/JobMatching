package Spider;

import Base.JobBean;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * @description: 爬取51job职业的详细信息
 * @author: PowerZZJ
 * @date: 2019/11/30
 */
public class SpiderJob_51job extends SpiderJob{
    private JobBean jobBean;
    private String jobInfo_keyWord;

    public SpiderJob_51job(){}
    public SpiderJob_51job(List<String> URLList){
        super(URLList);
    }
    public SpiderJob_51job(List<String> URLList, String JobInfoKeyWord){
        this(URLList);
        this.jobInfo_keyWord = JobInfoKeyWord;
    }

    /**
     *@Author: PowerZZJ
     *@param:
     *@return:JobBean
     *@Description: 从网页获取职位详细信息存入JobBean
     *从网页中提取出职位名称，
     *公司名，地址，工资，
     *发布日期，经验要求，
     *学历要求，招聘人数，职位信息，
     *公司类型，公司人数，公司方向
     */
    @Override
    public JobBean AcquirePageInfo(Document document, String jobURL) {
        if(getDocument() == null) return null;
        Elements elements_Base = getDocument().select("div[class=cn]");
        Elements elements_JobInfo = getDocument().select("[class=bmsg job_msg inbox]");
        Elements elements_Company = getDocument().select("div[class=com_tag] p");

        //如果有关键字判定职位信息是否包含关键
        String jobInfo = elements_JobInfo.text();
        if(jobInfo_keyWord != null){
            if(!jobInfo.contains(jobInfo_keyWord)){
                return null;
            }
        }

        String jobName = elements_Base.select("h1").attr("title");
        String company = elements_Base.select("a").attr("title");
        String salary = elements_Base.select("strong").text();
        String companyType = elements_Company.get(0).attr("title");
        String staffNumber = elements_Company.get(1).attr("title");
        String companyOrientation = elements_Company.get(2).attr("title");
        //有职位没有教育要求
        String[]  infos = elements_Base.select("p[class=msg ltype]").get(0).ownText().split("    ");
        String address = infos[0];
        String exp = infos[1];
        String edu = infos[2];
        String offerNumber = infos[3];
        String date = infos[4];

        JobBean jobBean = new JobBean(
                jobName, company, address,
                salary, date, exp,
                edu, offerNumber, jobInfo,
                companyType, staffNumber,companyOrientation,jobURL);
        return jobBean;

    }
}
