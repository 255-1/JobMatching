package proxy.filter;

import proxy.bean.IpBean;
import proxy.crawler.IpBeanCrawler;
import proxy.thread.IpTestAbleThread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class IpBeanFilter {

    /**
     * @Author: PowerZZJ
     * @param: 代理ip列表
     * @Description:过滤ip类型不是https以及延迟超过2秒的代理ip
     */
    public static void filter(List<IpBean> ipBeanList) {
        if (ipBeanList == null) return;
        Iterator<IpBean> it = ipBeanList.iterator();
        while (it.hasNext()) {
            IpBean ipBean = (IpBean) it.next();
            //保留代理属性不为null或者不为不为空字符串
            //保留延迟小于2s，保留HTTPS类型
            if (ipBeanIsValid(ipBean) && typeIsValid(ipBean) && speedIsValid(ipBean)) {
                continue;
            }
            it.remove();
        }
    }


    /**
     * @Author: PowerZZJ
     * @param: 代理ip列表
     * @Description:过滤ip不可用的 使用多线程检验IP地址是否可用
     */
    public static void getAble(List<IpBean> ipBeanList) {
        if (null == ipBeanList) return;
        ipBeanList.removeAll(getRemoveListByThread(ipBeanList));
    }


//-----------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @return: 不可用的代理列表
     * @Description:多线程检验代理是否可用
     */
    public static List<IpBean> getRemoveListByThread(List<IpBean> ipBeanList) {
        if (ipBeanList.size() == 0) return new ArrayList<IpBean>();
        //线程数等于IpBean大小
        int threadNumber = ipBeanList.size();
        //保存需要移除的IpBean列表
        List<IpBean> removeList = new ArrayList<>();
        List<Thread> threadList = new ArrayList<>();
        //启动和列表一样大的线程
        for (int i = 0; i < threadNumber; i++) {
            IpTestAbleThread ipTestAbleThread =
                    new IpTestAbleThread(removeList, ipBeanList.get(i));
            Thread thread = new Thread(ipTestAbleThread);
            thread.start();
            threadList.add(thread);
        }
        threadJoin(threadList);
        return removeList;
    }

    /**
     * @Author: PowerZZJ
     * @param: 线程列表
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
    }

    /**
     * @Author: PowerZZJ
     * @param: 代理ip
     * @return: 是否合法
     * @Description:判断代理ip各个属性是否为空字符串或者空
     */
    public static boolean ipBeanIsValid(IpBean ipBean) {
        String ipAddress = ipBean.getIpAddress();
        String ipPort = ipBean.getIpPort();
        String ipType = ipBean.getIpType();
        String ipSpeed = ipBean.getIpSpeed();
        if (ipAddress == null || ipAddress.length() == 0) return false;
        if (ipPort == null || ipPort.length() == 0) return false;
        if (ipType == null || ipType.length() == 0) return false;
        if (ipSpeed == null || ipSpeed.length() == 0) return false;
        return true;
    }

    /**
     * @Author: PowerZZJ
     * @param: 代理ip
     * @return: 是否合法
     * @Description:过滤ip延迟超过2秒的代理ip
     */
    public static boolean speedIsValid(IpBean ipBean) {
        String ipSpeed = ipBean.getIpSpeed();
        if (ipSpeed.indexOf("秒") != -1) {
            ipSpeed = ipSpeed.substring(0, ipSpeed.indexOf("秒"));
            double speed = Double.parseDouble(ipSpeed);
            return speed < 2.0;
        }
        return false;
    }

    /**
     * @Author: PowerZZJ
     * @param: 代理ip
     * @return: 是否合法
     * @Description:过滤ip类型不是https
     */
    public static boolean typeIsValid(IpBean ipBean) {
        return "HTTPS".equals(ipBean.getIpType());
    }

}
