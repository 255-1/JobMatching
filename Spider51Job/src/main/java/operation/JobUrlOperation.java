package operation;

import crawler.JobURLCrawler;
import httpbrowser.HttpBrowser;
import org.apache.http.HttpHost;
import proxy.bean.IpBean;
import proxy.save.IpBeanDBUtils;
import save.JobBeanLocalUtils;
import task.GlobalConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class JobUrlOperation {
    private static int MAX_TRY_COUNT = GlobalConfiguration.getJoburlMaxTryCount();
    private static int JOBURLLIST_SAVE_SIZE = GlobalConfiguration.getJoburlSaveSize();

    private static List<IpBean> ipBeanList = IpBeanDBUtils.selectIpBeanList(GlobalConfiguration.getProxyTablename());

    //保存批次，到达阈值JOBURLLIST_SAVE_SIZE算一批
    private int count = 1;
    private final List<String> jobUrlList;

    public JobUrlOperation(List<String> jobUrlList) {
        this.jobUrlList = jobUrlList;
    }

    /**
     * @Author: PowerZZJ
     * @param: urls 爬取的网页列表
     * keyWord 职位名关键字
     * @Description:作为每个线程的任务，需要上锁ipBeanList，
     * 每个线程爬取代理ip然后等待机会整合进总代理ip列表
     * 先尝试本机ip爬取，不行就用代理ip最多，尝试MAX_TRY_COUNT次。
     */
    public void getJobUrl(List<String> urls, String keyWord) {
        if (urls == null) {return;}
        if (keyWord == null || keyWord.length() == 0) {return;}
        List<String> jobUrlList_tmp = new ArrayList<>();
        for (String url : urls) {
            //尝试本机ip爬取
            boolean success = tryFecterWithLocalIP(url, keyWord, jobUrlList_tmp);
            //尝试代理ip爬取
            if (!success && ipBeanList.size() >= MAX_TRY_COUNT) {
                success = tryFecterWithProxy(url, keyWord, jobUrlList_tmp);
                //依旧失败
                if (!success) {
                    continue;
                }
            }
            addIntoJobUrlList(jobUrlList_tmp);
            //到达保存阈值JOBURLLIST_SAVE_SIZE就保存到本地
            if (jobUrlListNeedSave()) {
                saveJobUrlList(keyWord);
            }
        }

    }

//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @Description:保存总职位信息列表
     */
    public void saveJobUrlList(String keyWord) {
        synchronized (jobUrlList) {
            System.out.println("jobUrlList到达保存阈值，开始保存第" + (count++) + "批"
                    + JOBURLLIST_SAVE_SIZE + "数据");
            String fileName = GlobalConfiguration.getJoburlSaveName();
            JobBeanLocalUtils.saveJobUrlList(jobUrlList, keyWord, fileName);
            jobUrlList.clear();
        }
    }

    /**
     * @Author: PowerZZJ
     * @return: 是否到达保存数值
     * @Description:判断职位信息列表是否到达保存阈值JOBBEANLIST_SAVE_SIZE
     */
    public boolean jobUrlListNeedSave() {
        return jobUrlList.size() >= JOBURLLIST_SAVE_SIZE;
    }

    /**
     * @Author: PowerZZJ
     * @param: url 爬取地址
     * keyWord 职位名关键字
     * ipMessageList_tmp 每个线程暂存的职位url列表
     * @return: 是否成功
     * @Description:尝试本机爬取网址,尝试MAX_TRY_COUNT次。
     */
    public boolean tryFecterWithLocalIP(String url, String keyWord, List<String> jobUrlList_tmp) {
        for (int i = 0; i < MAX_TRY_COUNT; i++) {
            if (JobURLCrawler.urlParse(url, keyWord, jobUrlList_tmp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Author: PowerZZJ
     * @param: url 爬取地址
     * keyWord 职位名关键字
     * ipMessageList_tmp 每个线程暂存的职位url列表
     * @return: 是否成功
     * @Description:尝试代理爬取网址，尝试MAX_TRY_COUNT次。
     */
    public boolean tryFecterWithProxy(String url, String keyWord, List<String> jobUrlList_tmp) {
        boolean success = false;
        for (int i = 0; i < MAX_TRY_COUNT && !success; i++) {
            HttpHost proxy = getRandomProxy();
            if (proxy != null) {
                success = JobURLCrawler.urlParse(url, proxy, keyWord, jobUrlList_tmp);
            }
        }
        return success;
    }

    /**
     * @Author: PowerZZJ
     * @return: HttpHost代理实例
     * @Description:从总代理ip获取ip，无需上锁
     */
    public HttpHost getRandomProxy() {
        if (ipBeanList.size() != 0) {
            int rand = (int) (Math.random() * ipBeanList.size());
            String ipAddress = ipBeanList.get(rand).getIpAddress();
            String ipPort = ipBeanList.get(rand).getIpPort();
            return HttpBrowser.getHttpHost(ipAddress, ipPort);
        } else {
            return null;
        }
    }

    /**
     * @Author: PowerZZJ
     * @param: 每个线程各自的暂存的职位url列表
     * @Description:上锁总职位url列表，将暂存的职位url列表加入到总职位url列表
     */
    public void addIntoJobUrlList(List<String> jobUrlList_tmp) {
        synchronized (jobUrlList) {
            if (jobUrlList_tmp.size() > 0) {
//                System.out.println(Thread.currentThread().getName() + "已进入合并区 " +
//                        "待合并大小 jobUrlList_tmp：" + jobUrlList_tmp.size());
                jobUrlList.addAll(jobUrlList_tmp);
                jobUrlList_tmp.clear();
            }
        }
    }

}
