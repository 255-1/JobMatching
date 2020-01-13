package operation;

import bean.JobBean;
import crawler.JobInfoCrawler;
import httpbrowser.HttpBrowser;
import org.apache.http.HttpHost;
import proxy.bean.IpBean;
import proxy.save.IpBeanDBUtils;
import save.JobBeanLocalUtils;
import task.GlobalConfiguration;

import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class JobInfoOperation {
    //尝试次数
    private static int MAX_TRY_COUNT = GlobalConfiguration.getJobinfoMaxTryCount();
    //保存阈值数量
    private static int JOBBEANLIST_SAVE_SIZE = GlobalConfiguration.getJobinfoSaveSize();
    private static List<IpBean> ipBeanList = IpBeanDBUtils.selectIpBeanList(GlobalConfiguration.getProxyTablename());

    //保存批次，到达阈值算一批
    private int Count = 1;
    private List<JobBean> jobBeanList;

    public JobInfoOperation(List<JobBean> jobBeanList) {
        this.jobBeanList = jobBeanList;
    }

    /**
     * @Author: PowerZZJ
     * @param: urls 爬取的网页列表
     * @Description:作为每个线程的任务，需要上锁jobBeanList，
     * 每个线程爬取职位信息然后等待机会整合进总职位信息列表
     * 先尝试本机ip爬取，不行就用代理ip最多，尝试MAX_TRY_COUNT次。
     */
    public void getJobInfo(List<String> urls) {
        if (urls == null || urls.size() == 0) return;
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i).split(",")[1];
            JobBean jobBean = new JobBean();
            //尝试本机ip爬取
            boolean success = tryFecterWithLocalIP(url, jobBean);
            if (success == false && ipBeanList.size()>=MAX_TRY_COUNT) {
                //尝试代理ip爬起
                success = tryFecterWithProxy(url, jobBean);
                if (success == false) {
                    continue;
                }
            }
            addIntoJobBeanList(jobBean);
            //到达保存阈值JOBBEANLIST_SAVE_SIZE就保存到本地
            if (jobBeanListNeedSave()) {
                saveJobBeanList();
            }
        }
    }

//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @Description:保存总职位信息列表
     */
    public void saveJobBeanList() {
        synchronized (jobBeanList) {
            System.out.println("jobBeanList到达保存阈值，开始保存第" + (Count++) + "批"
                    + JOBBEANLIST_SAVE_SIZE + "数据");
            String fileName = GlobalConfiguration.getJobinfoSaveName();
            JobBeanLocalUtils.saveJobBeanList(jobBeanList, fileName);
            //保存后清空列表
            jobBeanList.clear();
        }
    }


    /**
     * @Author: PowerZZJ
     * @return: 是否到达保存数值
     * @Description:判断职位信息列表是否到达保存阈值JOBBEANLIST_SAVE_SIZE
     */
    public boolean jobBeanListNeedSave() {
        return jobBeanList.size() >= JOBBEANLIST_SAVE_SIZE;
    }

    /**
     * @Author: PowerZZJ
     * @Description:上锁总职位信息列表，将暂存的职位信息加入到总职位信息列表
     */
    public void addIntoJobBeanList(JobBean jobBean) {
        synchronized (jobBeanList) {
            jobBeanList.add(jobBean);
        }
    }


    /**
     * @Author: PowerZZJ
     * @param: url 爬取地址
     * @return: 是否成功
     * @Description:尝试本机爬取网址,
     */
    public boolean tryFecterWithLocalIP(String url, JobBean jobBean) {
        return JobInfoCrawler.jobInfoParse(url, jobBean);
    }

    /**
     * @Author: PowerZZJ
     * @param: 职位url，JobBean
     * @return: 是否成功
     * @Description:尝试代理爬取网址，尝试MAX_TRY_COUNT次。
     */
    public boolean tryFecterWithProxy(String url, JobBean jobBean) {
        boolean success = false;
        for (int i = 0; i < MAX_TRY_COUNT && success == false; i++) {
            HttpHost proxy = getRandomProxy();
            if (proxy != null) {
                success = JobInfoCrawler.jobInfoParse(url, proxy, jobBean);
                if (success) {
                    break;
                }
            }
        }
        return success;
    }


    /**
     * @Author: PowerZZJ
     * @return: HttpHost代理实例
     * @Description:获取代理ip，无需上锁
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
}