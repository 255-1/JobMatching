package Spider;

import Base.JobBean;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 从URL列表中爬取所有职位的详细信息
 * @author: PowerZZJ
 * @date: 2019/11/30
 */
public class SpiderJob extends Spider{

    private List<String> urlList;
    private List<JobBean> jobBeanList;

    public SpiderJob(){}
    public SpiderJob(List<String> JobURLList){
        this.urlList = JobURLList;
        this.jobBeanList = new ArrayList<>();
    }

    /**
     *@Author: PowerZZJ
     *@param:
     *@return:
     *@Description:整合爬取职位详细信息的迭代过程
     */
    public void SpiderJob(){
        System.out.println("Job信息开始爬取");
        if(urlList.size()>0){
            for(String url: urlList){
                try{
                    SpiderAll(url);
                    JobBean jobBean = AcquirePageInfo(getDocument(),url);
                    if(jobBean != null){
                        jobBeanList.add(jobBean);
                    }

                }catch (Exception e){
                    continue;
                }
            }
            System.out.println("Job信息爬取结束");
        }
    }

    //详细信息存成一个JobBean
    public JobBean AcquirePageInfo(Document document, String jobURL){
        return new JobBean();
    }

    public List<JobBean> getJobBeanList() {
        if(jobBeanList != null) {
            System.out.println("JobBean列表共"+jobBeanList.size()+"条信息");
        }
        return jobBeanList;
    }
}
