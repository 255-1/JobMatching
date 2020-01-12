package proxy.thread;

import httpbrowser.HttpBrowser;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import proxy.bean.IpBean;
import task.GlobalConfiguration;

import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class IpTestAbleThread implements Runnable {

    private List<IpBean> removeList;
    private IpBean ipBean;
    private String[] testWebs = GlobalConfiguration.getProxyTestWeb();

    public IpTestAbleThread(List<IpBean> removeList, IpBean ipBean) {
        this.removeList = removeList;
        this.ipBean = ipBean;
    }

    /**
     * @Author: PowerZZJ
     * @Description:每个IPFilter线程各自检测一个ipMessage是否可用, 不可用的加入到ipMessageList_remove中
     */
    @Override
    public void run() {
        String ipAddress = ipBean.getIpAddress();
        String ipPort = ipBean.getIpPort();
        HttpHost proxy = HttpBrowser.getHttpHost(ipAddress, ipPort);
        testPings(proxy, testWebs);
    }
//--------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @param: proxy 代理
     * webs 测试网站，可以是列表
     * @Description:测试ping多个网站
     */
    public void testPings(HttpHost proxy, String[] urls) {
        for (String url : urls) {
            boolean successPing = testPing(proxy, url);
            if (successPing == false) break;
        }
    }

    /**
     * @Author: PowerZZJ
     * @param: web 测试地址
     * proxy 代理
     * @Description:使用代理ping网站，如果不通则加进remove列表
     */
    public boolean testPing(HttpHost proxy, String url) {
        HttpGet httpGet = new HttpGet(url);
        HttpBrowser.addHeaders(httpGet);
        HttpBrowser.addConfigs(httpGet, proxy);
        try (CloseableHttpClient httpClient = HttpBrowser.createHttpClient();
             CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
            return true;
        } catch (Exception e) {
            removeList.add(ipBean);
        }
        return false;
    }

    /**
     * @Author: PowerZZJ
     * @Description:上锁removeList，然后写入需要移除的ip
     */
    public void addIntoRemoveList() {
        synchronized (removeList) {
            removeList.add(ipBean);
        }
    }
}
