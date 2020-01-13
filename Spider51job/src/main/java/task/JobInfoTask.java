package task;

import bean.JobBean;
import operation.JobInfoOperation;
import save.JobBeanLocalUtils;
import thread.JobInfoThread;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: PowerZZJ
 * @date: 2020/1/4
 */
public class JobInfoTask {
    private static int threadNum = GlobalConfiguration.getJobinfoThreadNumber();

    /**
     * @Author: PowerZZJ
     * @Description:职位信息爬取全过程
     */
    public static void goCrawler(List<String> jobUrlList) {
        List<JobBean> jobBeanList = new ArrayList<>();
        System.out.println("开始爬取jobInfo");
        crawlerPages(jobUrlList, jobBeanList);
        //将小于保存阈值的残余JobBean进行保存
        saveRemainList(jobBeanList);
    }
//----------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------

    /**
     * @Author: PowerZZJ
     * @param: 职位url列表，职位信息列表
     * @Description:多线程爬取jobinfo
     */
    public static void crawlerPages(List<String> jobUrlList, List<JobBean> jobBeanList) {
        List<Thread> threadList = new ArrayList<>();
        //round是线程开启的轮数，+1为了处理余数的情况
        //eg：jobUrl共1389，线程500,走3轮，最后1轮处理余数398个内容
        int round = jobUrlList.size() / threadNum + 1;
        //准备多线程的容器
        JobInfoOperation jobInfoOperation = new JobInfoOperation(jobBeanList);
        for (int i = 0; i < round; i++) {
            JobInfoThread jobInfoThread;
            //最后1轮，subList从最大整数倍（可以是0）到jobUrls的大小
            if (i == round - 1) {
                jobInfoThread = new JobInfoThread(
                        jobUrlList.subList(i * threadNum, jobUrlList.size()), jobInfoOperation);
            } else {
                //到线程整数倍，subList从上一个整数倍（可以是0），到上一个整数倍加一倍
                jobInfoThread = new JobInfoThread(
                        jobUrlList.subList(i * threadNum, i * threadNum + threadNum), jobInfoOperation);
            }
            Thread thread = new Thread(jobInfoThread);
            threadList.add(thread);
            thread.start();
        }
        threadJoin(threadList);
    }

    /**
     * @Author: PowerZZJ
     * @Description:保存残余数据,并释放资源
     */
    public static void saveRemainList(List<JobBean> jobBeanList) {
        System.out.println("保存残余jobInfo有：" + jobBeanList.size() + "条");
        String fileName = GlobalConfiguration.getJobinfoSaveName();
        JobBeanLocalUtils.saveJobBeanList(jobBeanList, fileName);
        jobBeanList.clear();
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


}
