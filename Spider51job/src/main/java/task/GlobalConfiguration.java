package task;

import java.io.File;
import java.util.Calendar;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class GlobalConfiguration {
    private static int PROXY_MAX_TRY_COUNT;
    private static String[] PROXY_TEST_WEB;
    private static int PROXY_THREAD_NUMBER;
    private static int PROXY_PAGES;

    private static int JOBURL_MAX_TRY_COUNT;
    private static int JOBURL_SAVE_SIZE;
    private static int JOBURL_PAGES;
    private static int JOBURL_THREAD_NUMBER;

    private static int JOBINFO_MAX_TRY_COUNT;
    private static int JOBINFO_SAVE_SIZE;
    private static int JOBINFO_THREAD_NUMBER;

    private static String PROXY_TABLENAME;
    private static String JOBINFO_TABLENAME;

    private static String SAVE_PATH;
    private static String JOBINFO_SAVE_NAME;
    private static String JOBURL_SAVE_NAME;
    private static String REFERENCE_SAVE_NAME;

    static {

        //代理爬取代理网站的尝试次数
        PROXY_MAX_TRY_COUNT = 10;
        //爬取代理网站的页数
        PROXY_PAGES = 400;
        //爬取代理网站的线程数，一般与页数相等
        PROXY_THREAD_NUMBER = PROXY_PAGES;
        //测试代理ip可用性的网站
        PROXY_TEST_WEB = new String[]{"https://www.51job.com",
                "https://search.51job.com/", "https://job.51job.com/"};

        //代理爬取jobUrl的尝试次数
        JOBURL_MAX_TRY_COUNT = 10;
        //jobUrl保存到本地的数量阈值
        JOBURL_SAVE_SIZE = 500;
        //每个职业的爬取页数
        JOBURL_PAGES = 1800;
        //jobUrl的爬取线程数，能整除JOBURL_PAGES的数字
        JOBURL_THREAD_NUMBER = JOBURL_PAGES / 3;

        //代理爬取jobInfo的尝试次数
        JOBINFO_MAX_TRY_COUNT = 3;
        //职位信息保存本地的数量阈值
        JOBINFO_SAVE_SIZE = 500;
        //职位信息爬取的线程数，基于路由器的带宽
        JOBINFO_THREAD_NUMBER = 500;

        //代理表的表名
        PROXY_TABLENAME = "proxypool";
        //职位信息的表名
        JOBINFO_TABLENAME = "jobInfo";
        //保存名字的格式
        SAVE_PATH = "data";
        JOBINFO_SAVE_NAME = SAVE_PATH + "/" + getSaveNameByCalendar() + "_jobinfo.txt";
        JOBURL_SAVE_NAME = SAVE_PATH + "/" + getSaveNameByCalendar() + "_joburl.txt";
        REFERENCE_SAVE_NAME = SAVE_PATH + "/" + getSaveNameByCalendar() + "_reference.txt";
    }

    public static String getSaveNameByCalendar() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return year + "_" + month + "_" + day;
    }

    public static String getReferenceSaveName() {
        return REFERENCE_SAVE_NAME;
    }

    public static String getJobinfoSaveName() {
        return JOBINFO_SAVE_NAME;
    }

    public static String getJoburlSaveName() {
        return JOBURL_SAVE_NAME;
    }

    public static String getProxyTablename() {
        return PROXY_TABLENAME;
    }

    public static String getJobinfoTablename() {
        return JOBINFO_TABLENAME;
    }

    public static int getProxyMaxTryCount() {
        return PROXY_MAX_TRY_COUNT;
    }

    public static String[] getProxyTestWeb() {
        return PROXY_TEST_WEB;
    }

    public static int getProxyThreadNumber() {
        return PROXY_THREAD_NUMBER;
    }

    public static int getProxyPages() {
        return PROXY_PAGES;
    }

    public static int getJoburlMaxTryCount() {
        return JOBURL_MAX_TRY_COUNT;
    }

    public static int getJoburlSaveSize() {
        return JOBURL_SAVE_SIZE;
    }

    public static int getJoburlPages() {
        return JOBURL_PAGES;
    }

    public static int getJoburlThreadNumber() {
        return JOBURL_THREAD_NUMBER;
    }

    public static int getJobinfoMaxTryCount() {
        return JOBINFO_MAX_TRY_COUNT;
    }

    public static int getJobinfoSaveSize() {
        return JOBINFO_SAVE_SIZE;
    }

    public static int getJobinfoThreadNumber() {
        return JOBINFO_THREAD_NUMBER;
    }
}
