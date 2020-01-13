package proxy.thread;

import proxy.operation.IpBeanOperation;

import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class IpBeanOpeartionThread implements Runnable {
    private List<String> urls;
    private IpBeanOperation ipBeanOperation;

    public IpBeanOpeartionThread(List<String> urls, IpBeanOperation ipBeanOperation) {
        this.urls = urls;
        this.ipBeanOperation = ipBeanOperation;
    }

    /**
     * @Author: PowerZZJ
     * @Description:每个线程需要完成Operation的任务
     */
    @Override
    public void run() {
        ipBeanOperation.getIpPool(urls);
    }
}
