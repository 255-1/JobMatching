package Spider;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 51job的职位URL爬取
 * @author: PowerZZJ
 * @date: 2019/11/30
 */
public class SpiderURL_51job extends SpiderURL{

    private String jobName_keyWord;

    public SpiderURL_51job(){}
    public SpiderURL_51job(String startURL){
        super(startURL);
    }
    public SpiderURL_51job(String startURL, String jobName_keyWord){
        this(startURL);
        this.jobName_keyWord = jobName_keyWord;
    }


    /**
     * @Author: PowerZZJ
     * @param:
     * @return: 获取51job(筛选职位名)返回职业详细URL列表
     * @Description:获取51job(筛选职位名)URL列表
     */
    @Override
    public List<String> AcquirePageInfo(Document document) {
        if (document == null) return null;
        //保存当前网页的url信息
        List<String> urlList = new ArrayList<>();
        Elements elements = getDocument().select("p[class^=t1] a[target]");
        for (Element element : elements) {
            if (jobName_keyWord != null) {
                if (element.attr("title").contains(jobName_keyWord)) {
                    urlList.add(element.attr("href"));
                }
            } else {
                urlList.add(element.attr("href"));
            }
        }
        return urlList;
    }


    /**
     * @Author: PowerZZJ
     * @param:
     * @return: 下一页URL
     * @Description:获取下一页URL的字符串
     */
    @Override
    public String AcquireNextPageURL(Document document) {
        if (document == null) return null;
        Elements elements = getDocument().select("li[class=bk]");
        String nextPage = elements.get(1).select("a").attr("href");
        return nextPage.isEmpty() ? null : nextPage;
    }
}
