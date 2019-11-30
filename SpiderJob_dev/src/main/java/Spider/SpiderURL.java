package Spider;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 爬取网页中的职位URL
 * @author: PowerZZJ
 * @date: 2019/11/30
 */
public class SpiderURL extends Spider {
    private int PAGE_COUNT = 1;

    private List<String> urlList;

    public SpiderURL() {
    }

    public SpiderURL(String strURL) {
        super(strURL);
        this.urlList = new ArrayList<>();
    }

    /**
     * @Author: PowerZZJ
     * @Description:整合爬取职位URL的迭代过程
     */
    public void SpiderURL() {
        System.out.println("URL爬取开始");
        while (getStrURL() != null && !getStrURL().isEmpty()) {
            try {
                System.out.println("--------------" + (PAGE_COUNT++) + "----------------");
                SpiderAll(getStrURL());
                urlList.addAll(AcquirePageInfo(getDocument()));
                setStrURL(AcquireNextPageURL(getDocument()));
            } catch (Exception e) {
                break;
            }
        }
        System.out.println("URL爬取结束");
    }

    //返回1页所有的职位的url列表
    public List<String> AcquirePageInfo(Document document) {
        return null;
    }

    //获取下一页的url
    public String AcquireNextPageURL(Document document) {
        return null;
    }


    public List<String> getUrlList() {
        if (urlList != null) {
            System.out.println("URL列表共" + urlList.size() + "条信息");
        }
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }
}
