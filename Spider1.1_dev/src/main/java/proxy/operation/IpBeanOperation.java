package proxy.operation;

import httpbrowser.HttpBrowser;
import org.apache.http.HttpHost;
import proxy.bean.IpBean;
import proxy.crawler.IpBeanCrawler;
import proxy.filter.IpBeanFilter;
import task.GlobalConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class IpBeanOperation {
    private List<IpBean> ipBeanList;
    private static int MAX_TRY_COUNT = GlobalConfiguration.getProxyMaxTryCount();

    public IpBeanOperation(List<IpBean> ipBeanList) {
        this.ipBeanList = ipBeanList;
    }

    /**
     * @Author: PowerZZJ
     * @param: urls 爬取的网页列表
     * @Description:作为每个线程的任务，需要上锁，
     * 每个线程爬取代理ip到tmp中，然后等待cpu调度整合进总代理ip列表
     * 先尝试本机ip爬取，不行就用代理ip最多，尝试MAX_TRY_COUNT次。
     */
    public void getIpPool(List<String> urls) {
        if (urls == null) return;
        List<IpBean> ipBeanList_tmp = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            boolean success = tryFecterWithLocalIP(url, ipBeanList_tmp);

            if (false == success) {
                success = tryFecterProxyWithProxy(url, ipBeanList_tmp);
                //使用代理尝试依旧失败
                if (false == success) {
//                    System.out.println(Thread.currentThread().getName() + "使用代理超出" + MAX_TRY_COUNT + "次，放弃：" + url);
                    continue;
                }
            }
            IpBeanFilter.filter(ipBeanList_tmp);
            IpBeanFilter.getAble(ipBeanList);
            addIntoIpBeanList(ipBeanList_tmp);
        }

    }
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @param: url 爬取地址
     * ipBeanList 每个线程暂存的列表
     * @return: 是否成功
     * @Description:尝试本机爬取网址
     */
    public boolean tryFecterWithLocalIP(String url, List<IpBean> ipBeanList) {
        return IpBeanCrawler.urlParse(url, ipBeanList);
    }

    /**
     * @Author: PowerZZJ
     * @param: url 爬取地址
     * ipBeanList 每个线程暂存的列表
     * @return: 是否成功
     * @Description:尝试使用代理爬取网址尝试MAX_TRY_COUNT次。
     */
    public boolean tryFecterProxyWithProxy(String url, List<IpBean> ipBeanList) {
        boolean success = false;
        for (int i = 0; i < MAX_TRY_COUNT && success == false; i++) {
            HttpHost proxy = getRandomProxy();
            if (proxy != null) {
                success = IpBeanCrawler.urlParse(url, proxy, ipBeanList);
            }
        }
        return success;
    }

    /**
     * @Author: PowerZZJ
     * @return: 2个字符串大小的数组
     * @Description:从总代理ip列表一组ip,，需要上锁 数组0:ip地址。数组1:端口
     */
    public HttpHost getRandomProxy() {
        synchronized (ipBeanList) {
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

    /**
     * @Author: PowerZZJ
     * @param: 每个线程各自的暂存的代理ip列表
     * @Description:上锁总代理ip，将暂存的ip列表加入到总代理ip中
     */
    public void addIntoIpBeanList(List<IpBean> ipBeanList_tmp) {
        synchronized (ipBeanList) {
            System.out.println("线程" + Thread.currentThread().getName() + "已进入合并区 " +
                    "待合并大小 ipBeanList_tmp：" + ipBeanList_tmp.size());
            ipBeanList.addAll(ipBeanList_tmp);
            ipBeanList_tmp.clear();
            System.out.println("ipBeanList现在总大小是：" + ipBeanList.size());
        }
    }
}
