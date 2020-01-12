package task;

import operation.JobUrlOperation;
import save.JobBeanLocalUtils;
import thread.JobUrlThread;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class JobUrlTask {
    private static int pages = GlobalConfiguration.getJoburlPages();
    private static int threadNum = GlobalConfiguration.getJoburlThreadNumber();
    private static int subProcessSize = pages / threadNum;

    /**
     * @Author: PowerZZJ
     * @Description:职位url爬取全过程
     */
    public static void goCrawler() {
        List<String> jobUrlList = new ArrayList<String>();
        //从配置文件获取关键字和基页
        HashMap<String, String> baseUrlMap = getkeyWordMap();
        for (Map.Entry<String, String> entry : baseUrlMap.entrySet()) {
            System.out.println("开始爬取" + entry.getKey() + "的JobUrl");
            crawlerPages(entry, jobUrlList);
            System.out.println("结束爬取" + entry.getKey() + "的JobUrl");
            saveRemainList(entry, jobUrlList);
        }
        baseUrlMap.clear();
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @Description:保存残余数据,并释放资源
     */
    public static void saveRemainList(Map.Entry<String, String> entry,
                                      List<String> jobUrlList) {
        System.out.println("保存残余joburl有：" + jobUrlList.size() + "条");
        String fileName = GlobalConfiguration.getJoburlSaveName();
        JobBeanLocalUtils.saveJobUrlList(jobUrlList, entry.getKey(), fileName);
        jobUrlList.clear();
    }

    /**
     * @Author: PowerZZJ
     * @param: 配置字典，JobUrl列表
     * @Description:启动多线程爬取jobUrl
     */
    public static void crawlerPages(Map.Entry<String, String> entry,
                                    List<String> jobUrlList) {
        List<String> urls = getPages(entry.getValue());
        List<Thread> threadList = new ArrayList<>();
        //准备线程所需要的jobUrl容器
        JobUrlOperation jobUrlOperation = new JobUrlOperation(jobUrlList);
        for (int i = 0; i < threadNum; i++) {
            int startIndex = i * subProcessSize;
            int endIndex = i * subProcessSize + subProcessSize;
            JobUrlThread jobUrlThread = new JobUrlThread(urls.subList(startIndex, endIndex),
                    jobUrlOperation, entry.getKey());
            Thread thread = new Thread(jobUrlThread);
            threadList.add(thread);
            thread.start();
        }
        threadJoin(threadList);
    }

    /**
     * @Author: PowerZZJ
     * @Description:通过字典元素，建立后面页数的url
     */
    public static List<String> getPages(String baseUrl) {
        List<String> urls = new ArrayList<>();
        //建立后续url
        for (int i = 1; i <= pages; i++) {
            urls.add(baseUrl + i + ".html");
        }
        return urls;
    }

    /**
     * @Author: PowerZZJ
     * @Description:阻塞线程列表，直到全完成才继续函数
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
     * @return: 职位名对应url的字典
     * @Description:从配置文件读取职位名对应url的字典
     */
    public static HashMap<String, String> getkeyWordMap() {
        ResourceBundle keyword_config = ResourceBundle.getBundle("51job-jobnamekeyword-config");
        String[] keyWordArray = keyword_config.getString("jobnamekeyword").split(",");

        HashMap<String, String> urlsMap = new HashMap<>();
        for (String keyWord : keyWordArray) {
            String keyWord_UTF8 = null;
            //配置文件的字符集是ISO，转成utf8
            try {
                keyWord_UTF8 = new String(keyWord.getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //配置文件查找关键字，依旧使用ISO字符集
            String url = keyword_config.getString(keyWord);
            //将utf8的职位名和地址对应
            urlsMap.put(keyWord_UTF8, url);
        }
        return urlsMap;
    }

}
