package task;

import bean.JobBean;
import filter.JobBeanFilter;
import filter.JobBeanUnify;
import proxy.ProxyTask;
import save.JobBeanDBUtils;
import save.JobBeanLocalUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class ProcedureTimeTask extends TimerTask {

    @Override
    public void run() {
        //jobUrl容器和jobinfo容器
        List<String> jobUrlList = new ArrayList<>();
        List<JobBean> jobBeanList = new ArrayList<>();

        long start = System.currentTimeMillis();

        //爬取代理ip并保存到数据库
        ProxyTask.goCrawler();

        //获取职位url列表，本地存入备份
        JobUrlTask.goCrawler();

        //获取职位信息列表，本地存入备份
        jobUrlList = JobBeanLocalUtils.loadJobUrlList(GlobalConfiguration.getJoburlSaveName());
        JobInfoTask.goCrawler(jobUrlList);


//以上为爬虫
//------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------
//下为入库清洗等操作

        //本地读取，的操作
        jobBeanList = JobBeanLocalUtils.loadJobBeanList(GlobalConfiguration.getJobinfoSaveName());

        //进行清洗
        JobBeanFilter.filter(jobBeanList);

        //存入数据库
        JobBeanDBUtils.insertJobBeanList(GlobalConfiguration.getJobinfoTablename(), jobBeanList);


        //职位名关键字的添加
        JobBeanUnify.addUnifyName();

        long end = System.currentTimeMillis();
        System.out.println("总计运行时间为" + (end - start) / 1000 + "s");
    }
}
