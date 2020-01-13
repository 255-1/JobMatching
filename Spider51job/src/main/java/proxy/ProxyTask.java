package proxy;

import proxy.bean.IpBean;
import proxy.crawler.IpBeanCrawler;
import proxy.filter.IpBeanFilter;
import proxy.operation.IpBeanOperation;
import proxy.save.IpBeanDBUtils;
import proxy.thread.IpBeanOpeartionThread;
import save.ConnectMySQL;
import save.DBUtils;
import task.GlobalConfiguration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class ProxyTask {
    private static String tableName = GlobalConfiguration.getProxyTablename();
    private static int pages = GlobalConfiguration.getProxyPages();
    private static int threadNum = GlobalConfiguration.getProxyThreadNumber();

    private static int subProcessSize = pages / threadNum;

    public static void goCrawler() {
        List<IpBean> ipBeanList = new ArrayList<>();

        //先从数据库中拿到上一次爬取的可以使用的代理ip
        ipBeanList = IpBeanDBUtils.selectIpBeanList(tableName);
        //本机ip爬取第一页
        crawlerFirstPage(ipBeanList);
        //代理爬取后续pages页
        crawlerOtherPages(ipBeanList);
        //清空原有数据库，插入现在可用的ip
        Connection conn = ConnectMySQL.getConnectionProxy();
        DBUtils.truncateTable(conn, tableName);
        IpBeanDBUtils.insertIpBeanList(tableName, ipBeanList);

    }
    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @param: 需要爬取的列表
     * @Description:创建线程爬取urls
     */
    public static void crawlerOtherPages(List<IpBean> ipBeanList) {
        List<String> urls = getPages();
        List<Thread> threadList = new ArrayList<>();
        //准备线程所需要的ipBean容器
        IpBeanOperation ipBeanOperation = new IpBeanOperation(ipBeanList);
        for (int i = 0; i < threadNum; i++) {
            int startIndex = i * subProcessSize;
            int endIndex = i * subProcessSize + subProcessSize;
            IpBeanOpeartionThread ipBeanOpeartionThread =
                    new IpBeanOpeartionThread(urls.subList(startIndex, endIndex), ipBeanOperation);
            Thread thread = new Thread(ipBeanOpeartionThread);
            threadList.add(thread);
            thread.start();
        }
        threadJoin(threadList);
    }

    /**
     * @Author: PowerZZJ
     * @Description:线程列表的全完成才继续函数
     */
    public static void threadJoin(List<Thread> threadList) {
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadList.clear();
    }

    /**
     * @Author: PowerZZJ
     * @Description:建立后面页数的url
     */
    public static List<String> getPages() {
        List<String> urls = new ArrayList<>();
        //建立后续url
        for (int i = 2; i < pages + 2; i++) {
            urls.add("https://www.xicidaili.com/nn/" + i);
        }
        return urls;
    }

    /**
     * @Author: PowerZZJ
     * @Description:本机ip爬取第一页
     */
    public static void crawlerFirstPage(List<IpBean> ipBeanList) {
        System.out.println("本机爬取西刺代理第1页");
        IpBeanCrawler.urlParse("https://www.xicidaili.com/nn/1", ipBeanList);
        IpBeanFilter.filter(ipBeanList);
        IpBeanFilter.getAble(ipBeanList);
    }

}
